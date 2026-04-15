package org.jakartaee.itsm;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * Configuration REST pour l'application ITSM
 * Définit le base path pour tous les endpoints: /api
 */
@ApplicationPath("/api")
public class RestConfig extends Application {
    // Cette classe configure le base path pour tous les endpoints REST
    // Aucune méthode n'est nécessaire
}
