package org.jakartaee.itsm.BEAN;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.jakartaee.itsm.DAO.ProblemeDAO;
import org.jakartaee.itsm.MODEL.Probleme;
import org.jakartaee.itsm.MODEL.Probleme.StatutProbleme;
import java.io.Serializable;
import java.util.List;

@Named("problemeBean")
@ViewScoped
public class ProblemeBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject private ProblemeDAO problemeDAO;

    private List<Probleme> allProblemes;
    private String nouveauTitre;
    private String nouvelleDescription;
    private String nouvelleCauseRacine;
    private String nouveauWorkaround;
    private String updateStatut;
    private String successMessage;
    private String errorMessage;

    @PostConstruct
    public void init() {
        try {
            allProblemes = problemeDAO.findAll();
        } catch (Exception e) {
            System.err.println("[ProblemeBean] Erreur init: " + e.getMessage());
            allProblemes = List.of();
        }
    }

    public String creerProbleme() {
        try {
            if (nouveauTitre == null || nouveauTitre.trim().isEmpty()) {
                errorMessage = "Le titre est obligatoire.";
                return null;
            }
            Probleme p = new Probleme(nouveauTitre.trim(), nouvelleDescription);
            p.setCauseRacine(nouvelleCauseRacine);
            p.setWorkaround(nouveauWorkaround);
            problemeDAO.save(p);

            // Réinitialiser les champs
            nouveauTitre      = "";
            nouvelleDescription = "";
            nouvelleCauseRacine = "";
            nouveauWorkaround = "";
            successMessage    = "Problème créé avec succès.";
            errorMessage      = null;
            allProblemes      = problemeDAO.findAll();
        } catch (Exception e) {
            errorMessage = "Erreur lors de la création : " + e.getMessage();
        }
        return null;
    }

    public String updateStatutProbleme(Long id) {
        try {
            if (updateStatut == null || updateStatut.isEmpty()) {
                errorMessage = "Veuillez sélectionner un statut.";
                return null;
            }
            problemeDAO.updateStatut(id, StatutProbleme.valueOf(updateStatut));
            successMessage = "Statut mis à jour.";
            errorMessage   = null;
            updateStatut   = "";
            allProblemes   = problemeDAO.findAll();
        } catch (Exception e) {
            errorMessage = "Erreur mise à jour statut : " + e.getMessage();
        }
        return null;
    }

    // Getters / Setters
    public List<Probleme> getAllProblemes()      { return allProblemes; }
    public String getNouveauTitre()              { return nouveauTitre; }
    public void   setNouveauTitre(String v)      { this.nouveauTitre = v; }
    public String getNouvelleDescription()       { return nouvelleDescription; }
    public void   setNouvelleDescription(String v){ this.nouvelleDescription = v; }
    public String getNouvelleCauseRacine()       { return nouvelleCauseRacine; }
    public void   setNouvelleCauseRacine(String v){ this.nouvelleCauseRacine = v; }
    public String getNouveauWorkaround()         { return nouveauWorkaround; }
    public void   setNouveauWorkaround(String v) { this.nouveauWorkaround = v; }
    public String getUpdateStatut()              { return updateStatut; }
    public void   setUpdateStatut(String v)      { this.updateStatut = v; }
    public String getSuccessMessage()            { return successMessage; }
    public String getErrorMessage()              { return errorMessage; }
}
