package org.jakartaee.itsm.SERVICE;

import jakarta.ejb.Schedule;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;
import java.util.Date;

/**
 * Service EJB pour calculer les métriques automatiquement
 * S'exécute tous les jours à minuit (00:00)
 */
@Singleton
@Startup
public class MetricsTimerService {

    @Inject
    private MetricsService metricsService;

    /**
     * Calcule les métriques chaque jour à minuit
     * Expression cron: seconde, minute, heure, jour du mois, mois, jour de la semaine
     */
    @Schedule(hour = "0", minute = "0", second = "0", persistent = false)
    public void calculateDailyMetrics() {
        System.out.println("[MetricsTimerService] ===== CALCUL DES MÉTRIQUES QUOTIDIENNES =====");
        System.out.println("[MetricsTimerService] Heure: " + new Date());
        
        try {
            // Calculer les métriques pour aujourd'hui
            Date aujourd_hui = new Date();
            metricsService.calculateAndSaveMetrics(aujourd_hui);
            
            System.out.println("[MetricsTimerService]  Métriques calculées avec succès");
        } catch (Exception e) {
            System.err.println("[MetricsTimerService]  Erreur lors du calcul des métriques: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Optionnel: Calcul manuel des métriques (peut être appelé via endpoint)
     */
    public void forceCalculateMetrics() {
        System.out.println("[MetricsTimerService] Calcul forcé des métriques");
        Date aujourd_hui = new Date();
        metricsService.calculateAndSaveMetrics(aujourd_hui);
    }
}
