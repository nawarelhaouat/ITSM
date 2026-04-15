package org.jakartaee.itsm.MODEL;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "change_request")
public class ChangeRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String titre;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Impact impact = Impact.MOYEN;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Risque risque = Risque.MOYEN;

    @Column(name = "plan_rollback", columnDefinition = "TEXT")
    private String planRollback;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutChangement statut = StatutChangement.BROUILLON;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_execution")
    private Date dateExecution;

    @Column(name = "duree_minutes")
    private Integer dureeMinutes;

    @Enumerated(EnumType.STRING)
    private ResultatChangement resultat;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_creation", nullable = false, updatable = false)
    private Date dateCreation = new Date();

    @ManyToOne
    @JoinColumn(name = "demandeur_id")
    private User demandeur;

    @ManyToOne
    @JoinColumn(name = "approbateur_id")
    private User approbateur;

    public ChangeRequest() {}

    // Getters/Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Impact getImpact() { return impact; }
    public void setImpact(Impact impact) { this.impact = impact; }

    public Risque getRisque() { return risque; }
    public void setRisque(Risque risque) { this.risque = risque; }

    public String getPlanRollback() { return planRollback; }
    public void setPlanRollback(String planRollback) { this.planRollback = planRollback; }

    public StatutChangement getStatut() { return statut; }
    public void setStatut(StatutChangement statut) { this.statut = statut; }

    public Date getDateExecution() { return dateExecution; }
    public void setDateExecution(Date dateExecution) { this.dateExecution = dateExecution; }

    public Integer getDureeMinutes() { return dureeMinutes; }
    public void setDureeMinutes(Integer dureeMinutes) { this.dureeMinutes = dureeMinutes; }

    public ResultatChangement getResultat() { return resultat; }
    public void setResultat(ResultatChangement resultat) { this.resultat = resultat; }

    public Date getDateCreation() { return dateCreation; }
    public void setDateCreation(Date dateCreation) { this.dateCreation = dateCreation; }

    public User getDemandeur() { return demandeur; }
    public void setDemandeur(User demandeur) { this.demandeur = demandeur; }

    public User getApprobateur() { return approbateur; }
    public void setApprobateur(User approbateur) { this.approbateur = approbateur; }

    public enum Impact { BAS, MOYEN, ELEVE }
    public enum Risque { FAIBLE, MOYEN, ELEVE }
    public enum StatutChangement { BROUILLON, SOUMIS, EN_REVUE, APPROUVE, REJETE, IMPLEMENTE, CLOTURE }
    public enum ResultatChangement { SUCCES, ECHEC, ROLLBACK }
}
