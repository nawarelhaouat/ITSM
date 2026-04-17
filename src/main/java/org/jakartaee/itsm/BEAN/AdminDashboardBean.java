package org.jakartaee.itsm.BEAN;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.inject.Inject;
import jakarta.annotation.PostConstruct;
import org.jakartaee.itsm.MODEL.Ticket;
import org.jakartaee.itsm.MODEL.Ticket.Status;
import org.jakartaee.itsm.MODEL.SlaConfig;
import org.jakartaee.itsm.MODEL.Metrics;
import org.jakartaee.itsm.SERVICE.TicketService;
import org.jakartaee.itsm.SERVICE.SlaService;
import org.jakartaee.itsm.SERVICE.MetricsService;
import org.jakartaee.itsm.SERVICE.UserService;
import java.io.Serializable;
import java.util.List;

@Named("adminDashboardBean")
@ViewScoped
public class AdminDashboardBean implements Serializable {
    private static final long serialVersionUID = 1L;

    // Services - Injected by CDI
    @Inject
    private TicketService ticketService;
    @Inject
    private SlaService slaService;
    @Inject
    private MetricsService metricsService;
    @Inject
    private UserService userService;


    // Statistiques
    private long totalTickets = 0;
    private long ticketsOuvert = 0;
    private long ticketsAssigne = 0;
    private long ticketsEnCours = 0;
    private long ticketsResolu = 0;
    private long ticketsEscalade = 0;
    private long totalUtilisateurs = 0;

    // Données pour les graphiques
    private List<Ticket> ticketsEscalades;
    private List<Ticket> ticketsCritiques;
    private List<SlaConfig> slaConfigs;
    private List<Metrics> metricsRecentes;

    private List<Ticket> recentTickets;
private List<Ticket> ticketsSlaEnDanger;

    // Moyennes
    private Double tauxSlaGlobal = 0.0;
    private Double tauxSatisfactionGlobal = 0.0;

    public AdminDashboardBean() {
        System.out.println("[AdminDashboardBean] Constructeur appelé");
    }

    @PostConstruct
    public void init() {
        System.out.println("[AdminDashboardBean] @PostConstruct init() appelé");
        loadDashboardData();
    }

    /**
     * Charge toutes les données du dashboard depuis la base de données
     */
    public void loadDashboardData() {
        // statistiques générales
        try {
            System.out.println("[AdminDashboard] === DÉBUT CHARGEMENT DONNÉES ===");

            totalTickets = ticketService.getTotalTickets();
            System.out.println("[AdminDashboard] Total tickets: " + totalTickets);

            ticketsOuvert = ticketService.getCountOuvert();
            System.out.println("[AdminDashboard] Tickets ouverts: " + ticketsOuvert);

            ticketsAssigne = ticketService.getCountAssigne();
            System.out.println("[AdminDashboard] Tickets assignés: " + ticketsAssigne);

            ticketsEnCours = ticketService.getCountEnCours();
            System.out.println("[AdminDashboard] Tickets en cours: " + ticketsEnCours);

            ticketsResolu = ticketService.getCountResolu();
            System.out.println("[AdminDashboard] Tickets résolus: " + ticketsResolu);

            ticketsEscalade = ticketService.getCountEscalade();
            System.out.println("[AdminDashboard] Tickets en escalade: " + ticketsEscalade);

            ticketsEscalades = ticketService.getEscaledes();
            System.out.println("[AdminDashboard] Escalades chargées: " + (ticketsEscalades != null ? ticketsEscalades.size() : 0));

            ticketsCritiques = ticketService.getCritiques();
            System.out.println("[AdminDashboard] Critiques chargées: " + (ticketsCritiques != null ? ticketsCritiques.size() : 0));

            slaConfigs = slaService.getAllSlaConfigs();
            System.out.println("[AdminDashboard] SLA configs chargées: " + (slaConfigs != null ? slaConfigs.size() : 0));

            metricsRecentes = metricsService.getMetricsLastDays(7);
            System.out.println("[AdminDashboard] Metrics chargées: " + (metricsRecentes != null ? metricsRecentes.size() : 0));

            tauxSlaGlobal = metricsService.getAverageSlaCompliance();
            System.out.println("[AdminDashboard] Taux SLA global: " + tauxSlaGlobal);

            tauxSatisfactionGlobal = metricsService.getAverageSatisfaction();
            System.out.println("[AdminDashboard] Taux satisfaction global: " + tauxSatisfactionGlobal);

            System.out.println("[AdminDashboard] === DONNÉES CHARGÉES AVEC SUCCÈS ===");

        } catch (Exception e) {
            System.err.println("[AdminDashboard] ERREUR bloc principal: " + e.getMessage());
            e.printStackTrace();
        }

        // tickets récents (isolé)
        try {
            recentTickets = ticketService.getRecentTickets(10);
            System.out.println("[AdminDashboard] recentTickets size: " + (recentTickets != null ? recentTickets.size() : "NULL"));
        } catch (Exception e) {
            System.err.println("[AdminDashboard] ERREUR recentTickets: " + e.getMessage());
            recentTickets = new java.util.ArrayList<>();
        }

        // SLA en danger (isolé)
        try {
            ticketsSlaEnDanger = ticketService.getTicketsSlaEnDanger(80);
            System.out.println("[AdminDashboard] ticketsSlaEnDanger size: " + (ticketsSlaEnDanger != null ? ticketsSlaEnDanger.size() : "NULL"));
        } catch (Exception e) {
            System.err.println("[AdminDashboard] ERREUR slaEnDanger: " + e.getMessage());
            ticketsSlaEnDanger = new java.util.ArrayList<>();
        }

        // utilisateurs (isolé)
        try {
            totalUtilisateurs = userService.countActiveUsers();
            System.out.println("[AdminDashboard] totalUtilisateurs: " + totalUtilisateurs);
        } catch (Exception e) {
            System.err.println("[AdminDashboard] ERREUR totalUtilisateurs: " + e.getMessage());
            totalUtilisateurs = 0;
        }
    }

    /**
     * Retourne le pourcentage de tickets en cours de résolution
     */
    public Double getPercentageEnCours() {
        if (totalTickets == 0) return 0.0;
        return (ticketsEnCours * 100.0) / totalTickets;

        
    }

    /**
     * Retourne le pourcentage de tickets résolus
     */
    public Double getPercentageResolu() {
        if (totalTickets == 0) return 0.0;
        return (ticketsResolu * 100.0) / totalTickets;
    }

    /**
     * Retourne le pourcentage d'escalades
     */
    public Double getPercentageEscalade() {
        if (totalTickets == 0) return 0.0;
        return (ticketsEscalade * 100.0) / totalTickets;
    }

    /**
     * Retourne la santé générale du système (score sur 100)
     * Basé sur: respect SLA + satisfaction + résolution
     */
    public Integer getHealthScore() {
        double slaScore = tauxSlaGlobal != null ? tauxSlaGlobal : 0;
        double satScore = tauxSatisfactionGlobal != null ? tauxSatisfactionGlobal : 0;
        double resolScore = totalTickets > 0 ? ((double) ticketsResolu / totalTickets * 100) : 0;

        int score = (int) ((slaScore + satScore + resolScore) / 3);
        return Math.min(100, Math.max(0, score));
    }

    /**
     * Retourne un badge de couleur selon le health score
     */
    public String getHealthBadgeClass() {
        Integer score = getHealthScore();
        if (score >= 80) return "badge-success";
        if (score >= 60) return "badge-warning";
        return "badge-danger";
    }

    // ============ GETTERS ET SETTERS ============

    public long getTotalTickets() {
        return totalTickets;
    }

    public long getTicketsOuvert() {
        return ticketsOuvert;
    }

    public long getTicketsAssigne() {
        return ticketsAssigne;
    }

    public long getTicketsEnCours() {
        return ticketsEnCours;
    }

    public long getTicketsResolu() {
        return ticketsResolu;
    }

    public long getTicketsEscalade() {
        return ticketsEscalade;
    }

    public long getTotalUtilisateurs() {
        return totalUtilisateurs;
    }

    public void setTotalUtilisateurs(long totalUtilisateurs) {
        this.totalUtilisateurs = totalUtilisateurs;
    }

    public List<Ticket> getTicketsEscalades() {
        return ticketsEscalades;
    }

    public List<Ticket> getTicketsCritiques() {
        return ticketsCritiques;
    }

    public List<SlaConfig> getSlaConfigs() {
        return slaConfigs;
    }

    public List<Metrics> getMetricsRecentes() {
        return metricsRecentes;
    }

    public Double getTauxSlaGlobal() {
        return tauxSlaGlobal;
    }

    /**
     * Retourne le taux SLA global formaté à 2 décimales
     */
    public String getTauxSlaGlobalFormatted() {
        return tauxSlaGlobal != null ? String.format("%.2f", tauxSlaGlobal) : "0.00";
    }

    public Double getTauxSatisfactionGlobal() {
        return tauxSatisfactionGlobal;
    }

    /**
     * Retourne le taux de satisfaction global formaté à 2 décimales
     */
    public String getTauxSatisfactionGlobalFormatted() {
        return tauxSatisfactionGlobal != null ? String.format("%.2f", tauxSatisfactionGlobal) : "0.00";
    }

    public TicketService getTicketService() {
        return ticketService;
    }

    public SlaService getSlaService() {
        return slaService;
    }

    public MetricsService getMetricsService() {
        return metricsService;
    }
    // Ajoute ces getters alias en bas du bean :
public String getLastUpdate() {
    return new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(new java.util.Date());
}

public Double getTauxSLA() { 
    return tauxSlaGlobal; 
}

public Double getSatisfactionMoyenne() { 
    return tauxSatisfactionGlobal; 
}

    public List<Ticket> getRecentTickets() {
        System.out.println("[DEBUG] getRecentTickets() appelé, size=" +
                (recentTickets != null ? recentTickets.size() : "NULL"));
        return recentTickets;
    }

public List<Ticket> getTicketsSlaEnDanger() { 
    return ticketsSlaEnDanger; 
}
}
