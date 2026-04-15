package org.jakartaee.itsm.SERVICE;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import org.jakartaee.itsm.DAO.MetricsDAO;
import org.jakartaee.itsm.MODEL.Metrics;
import org.jakartaee.itsm.MODEL.Ticket;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Stateless
public class MetricsService {
    @Inject
    private MetricsDAO metricsDAO;
    
    @Inject
    private TicketService ticketService;

    public void createMetrics(Metrics metrics) {
        metricsDAO.create(metrics);
    }

    public Metrics getMetrics(Long id) {
        return metricsDAO.findById(id);
    }

    public List<Metrics> getAllMetrics() {
        return metricsDAO.findAll();
    }

    public Metrics getMetricsByPeriode(Date periode) {
        try {
            return metricsDAO.findByPeriode(periode);
        } catch (Exception e) {
            return null;
        }
    }

    public List<Metrics> getMetricsLastDays(int days) {
        return metricsDAO.findLastDays(days);
    }

    public void updateMetrics(Metrics metrics) {
        metricsDAO.update(metrics);
    }

    public void deleteMetrics(Long id) {
        metricsDAO.delete(id);
    }

    public Double getAverageSlaCompliance() {
        List<Metrics> metrics = getAllMetrics();
        if (metrics.isEmpty()) {
            return 0.0;
        }
        return metrics.stream()
                .mapToDouble(m -> m.getTauxRespectSla() != null ? m.getTauxRespectSla().doubleValue() : 0)
                .average()
                .orElse(0.0);
    }

    public Double getAverageSatisfaction() {
        List<Metrics> metrics = getAllMetrics();
        if (metrics.isEmpty()) {
            return 0.0;
        }
        return metrics.stream()
                .mapToDouble(m -> m.getTauxSatisfaction() != null ? m.getTauxSatisfaction().doubleValue() : 0)
                .average()
                .orElse(0.0);
    }

    /**
     * Calcule et sauvegarde les métriques pour une période donnée
     * Basé sur les tickets résolus durant cette période
     */
    public void calculateAndSaveMetrics(Date periode) {
        try {
            System.out.println("[MetricsService] Calcul des métriques pour: " + periode);
            
            List<Ticket> allTickets = ticketService.getAllTickets();
            
            // Calculer les métriques
            BigDecimal tauxSla = calculateSlaCompliance(allTickets);
            BigDecimal tempsMoyen = calculateAverageResolutionTime(allTickets);
            BigDecimal tauxSatisfaction = calculateAverageSatisfaction(allTickets);
            
            // Créer et sauvegarder
            Metrics metrics = new Metrics(periode, tauxSla, tempsMoyen, tauxSatisfaction);
            metricsDAO.create(metrics);
            
            System.out.println("[MetricsService] Metrics calculées et sauvegardées");
            System.out.println("  - Taux SLA: " + tauxSla);
            System.out.println("  - Temps moyen résolution: " + tempsMoyen);
            System.out.println("  - Taux satisfaction: " + tauxSatisfaction);
            
        } catch (Exception e) {
            System.err.println("[MetricsService] Erreur lors du calcul des métriques: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Calcule le taux de respect SLA
     * = (nombre de tickets résolus dans le délai SLA / nombre de tickets résolus) * 100
     */
    private BigDecimal calculateSlaCompliance(List<Ticket> tickets) {
        if (tickets == null || tickets.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        // Compter les tickets résolus
        List<Ticket> resolvedTickets = tickets.stream()
                .filter(t -> t.getStatut().name().equals("RESOLU"))
                .toList();
        
        if (resolvedTickets.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        // Compter les tickets résolus dans le délai SLA
        long onTimeSla = resolvedTickets.stream()
                .filter(t -> t.getDateResolution() != null && 
                            t.getDateLimiteSla() != null &&
                            t.getDateResolution().getTime() <= t.getDateLimiteSla().getTime())
                .count();
        
        double compliance = (double) onTimeSla / resolvedTickets.size() * 100;
        return new BigDecimal(String.format("%.2f", compliance));
    }

    /**
     * Calcule le temps moyen de résolution en heures
     */
    private BigDecimal calculateAverageResolutionTime(List<Ticket> tickets) {
        if (tickets == null || tickets.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        List<Ticket> resolvedTickets = tickets.stream()
                .filter(t -> t.getStatut().name().equals("RESOLU") &&
                            t.getDateResolution() != null &&
                            t.getDateCreation() != null)
                .toList();
        
        if (resolvedTickets.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        double averageTimeMs = resolvedTickets.stream()
                .mapToLong(t -> t.getDateResolution().getTime() - t.getDateCreation().getTime())
                .average()
                .orElse(0);
        
        // Convertir en heures
        double averageTimeHours = averageTimeMs / (1000 * 60 * 60);
        return new BigDecimal(String.format("%.2f", averageTimeHours));
    }

    /**
     * Calcule le taux de satisfaction moyen (en %)
     */
    private BigDecimal calculateAverageSatisfaction(List<Ticket> tickets) {
        if (tickets == null || tickets.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        List<Ticket> satisfactionTickets = tickets.stream()
                .filter(t -> t.getSatisfaction() != null)
                .toList();
        
        if (satisfactionTickets.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        double averageSatisfaction = satisfactionTickets.stream()
                .mapToDouble(Ticket::getSatisfaction)
                .average()
                .orElse(0);
        
        return new BigDecimal(String.format("%.2f", averageSatisfaction));
    }
}
