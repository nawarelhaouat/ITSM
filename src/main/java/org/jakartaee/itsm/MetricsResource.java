package org.jakartaee.itsm;

import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.jakartaee.itsm.SERVICE.MetricsService;
import org.jakartaee.itsm.SERVICE.MetricsTimerService;
import java.util.Date;

/**
 * REST Endpoint pour gérer les métriques
 * Endpoint: /api/metrics
 */
@Path("/api/metrics")
@Produces("application/json")
@Consumes("application/json")
public class MetricsResource {

    @EJB
    private MetricsService metricsService;
    
    @EJB
    private MetricsTimerService metricsTimerService;

    /**
     * Endpoint pour déclencher le calcul des métriques manuellement
     * GET /api/metrics/calculate
     */
    @GET
    @Path("/calculate")
    public Response calculateMetrics() {
        try {
            System.out.println("[MetricsResource] Demande de calcul des métriques");
            metricsTimerService.forceCalculateMetrics();
            
            return Response.ok()
                    .entity("{\"message\": \"Métriques calculées avec succès\", \"timestamp\": \"" + new Date() + "\"}")
                    .build();
        } catch (Exception e) {
            System.err.println("[MetricsResource] Erreur: " + e.getMessage());
            return Response.serverError()
                    .entity("{\"error\": \"Erreur lors du calcul: " + e.getMessage() + "\"}")
                    .build();
        }
    }

    /**
     * Endpoint pour récupérer toutes les métriques récentes
     * GET /api/metrics/recent
     */
    @GET
    @Path("/recent")
    public Response getRecentMetrics() {
        try {
            var metrics = metricsService.getMetricsLastDays(7);
            return Response.ok(metrics).build();
        } catch (Exception e) {
            return Response.serverError()
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    /**
     * Endpoint pour récupérer les moyennes globales
     * GET /api/metrics/average
     */
    @GET
    @Path("/average")
    public Response getAverageMetrics() {
        try {
            var response = new java.util.HashMap<>();
            response.put("avgSlaCompliance", metricsService.getAverageSlaCompliance());
            response.put("avgSatisfaction", metricsService.getAverageSatisfaction());
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.serverError()
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }
}
