package org.jakartaee.itsm.BEAN;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.jakartaee.itsm.DAO.ChangeRequestDAO;
import org.jakartaee.itsm.MODEL.ChangeRequest;
import org.jakartaee.itsm.MODEL.ChangeRequest.Impact;
import org.jakartaee.itsm.MODEL.ChangeRequest.Risque;
import org.jakartaee.itsm.MODEL.ChangeRequest.StatutChangement;
import java.io.Serializable;
import java.util.List;

@Named("changementBean")
@ViewScoped
public class ChangementBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject private ChangeRequestDAO changeRequestDAO;
    @Inject private LoginBean loginBean;

    private List<ChangeRequest> allChangements;
    private List<ChangeRequest> changementsSoumis;
    private Long selectedId;

    // Formulaire nouveau changement
    private String nouveauTitre;
    private String nouvelleDescription;
    private String nouvelImpact   = "MOYEN";
    private String nouveauRisque  = "MOYEN";
    private String nouveauPlanRollback;
    private String nouvelleDateExecution;
    private Integer nouvelleDureeMinutes;

    private String successMessage;
    private String errorMessage;

    @PostConstruct
    public void init() {
        try {
            allChangements    = changeRequestDAO.findAll();
            changementsSoumis = changeRequestDAO.findByStatut(StatutChangement.SOUMIS);
        } catch (Exception e) {
            System.err.println("[ChangementBean] Erreur init: " + e.getMessage());
            allChangements    = List.of();
            changementsSoumis = List.of();
        }
    }

    public String creerChangement() {
        try {
            if (nouveauTitre == null || nouveauTitre.trim().isEmpty()) {
                errorMessage = "Le titre est obligatoire.";
                return null;
            }
            ChangeRequest cr = new ChangeRequest();
            cr.setTitre(nouveauTitre.trim());
            cr.setDescription(nouvelleDescription);
            cr.setImpact(Impact.valueOf(nouvelImpact));
            cr.setRisque(Risque.valueOf(nouveauRisque));
            cr.setPlanRollback(nouveauPlanRollback);
            cr.setDureeMinutes(nouvelleDureeMinutes);
            cr.setStatut(StatutChangement.SOUMIS);
            cr.setDemandeur(loginBean.getUser());
            changeRequestDAO.save(cr);

            // Reset
            nouveauTitre = ""; nouvelleDescription = ""; nouveauPlanRollback = "";
            nouvelleDureeMinutes = null; nouvelleDateExecution = "";
            successMessage = "Demande de changement soumise avec succès.";
            errorMessage   = null;
            init();
        } catch (Exception e) {
            errorMessage = "Erreur lors de la création : " + e.getMessage();
        }
        return null;
    }

    public String approuver(Long id) {
        try {
            ChangeRequest cr = changeRequestDAO.findById(id);
            if (cr != null) {
                cr.setStatut(StatutChangement.APPROUVE);
                cr.setApprobateur(loginBean.getUser());
                changeRequestDAO.save(cr);
                successMessage = "Changement approuvé.";
                errorMessage   = null;
                init();
            }
        } catch (Exception e) {
            errorMessage = "Erreur approbation : " + e.getMessage();
        }
        return null;
    }

    public String rejeter(Long id) {
        try {
            ChangeRequest cr = changeRequestDAO.findById(id);
            if (cr != null) {
                cr.setStatut(StatutChangement.REJETE);
                cr.setApprobateur(loginBean.getUser());
                changeRequestDAO.save(cr);
                successMessage = "Changement rejeté.";
                errorMessage   = null;
                init();
            }
        } catch (Exception e) {
            errorMessage = "Erreur rejet : " + e.getMessage();
        }
        return null;
    }

    // Getters / Setters
    public List<ChangeRequest> getAllChangements()       { return allChangements; }
    public List<ChangeRequest> getChangementsSoumis()   { return changementsSoumis; }
    public Long getSelectedId()                          { return selectedId; }
    public void setSelectedId(Long v)                    { this.selectedId = v; }
    public String getNouveauTitre()                      { return nouveauTitre; }
    public void   setNouveauTitre(String v)              { this.nouveauTitre = v; }
    public String getNouvelleDescription()               { return nouvelleDescription; }
    public void   setNouvelleDescription(String v)       { this.nouvelleDescription = v; }
    public String getNouvelImpact()                      { return nouvelImpact; }
    public void   setNouvelImpact(String v)              { this.nouvelImpact = v; }
    public String getNouveauRisque()                     { return nouveauRisque; }
    public void   setNouveauRisque(String v)             { this.nouveauRisque = v; }
    public String getNouveauPlanRollback()               { return nouveauPlanRollback; }
    public void   setNouveauPlanRollback(String v)       { this.nouveauPlanRollback = v; }
    public String getNouvelleDateExecution()             { return nouvelleDateExecution; }
    public void   setNouvelleDateExecution(String v)     { this.nouvelleDateExecution = v; }
    public Integer getNouvelleDureeMinutes()             { return nouvelleDureeMinutes; }
    public void   setNouvelleDureeMinutes(Integer v)     { this.nouvelleDureeMinutes = v; }
    public String getSuccessMessage()                    { return successMessage; }
    public String getErrorMessage()                      { return errorMessage; }
}
