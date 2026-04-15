package org.jakartaee.itsm.MODEL;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "probleme")
public class Probleme implements Serializable {
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
    private StatutProbleme statut = StatutProbleme.OUVERT;

    @Column(name = "cause_racine", columnDefinition = "TEXT")
    private String causeRacine;

    @Column(columnDefinition = "TEXT")
    private String workaround;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_creation", nullable = false, updatable = false)
    private Date dateCreation = new Date();

    public Probleme() {}

    public Probleme(String titre, String description) {
        this.titre = titre;
        this.description = description;
        this.statut = StatutProbleme.OUVERT;
        this.dateCreation = new Date();
    }

    // Getters/Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public StatutProbleme getStatut() { return statut; }
    public void setStatut(StatutProbleme statut) { this.statut = statut; }

    public String getCauseRacine() { return causeRacine; }
    public void setCauseRacine(String causeRacine) { this.causeRacine = causeRacine; }

    public String getWorkaround() { return workaround; }
    public void setWorkaround(String workaround) { this.workaround = workaround; }

    public Date getDateCreation() { return dateCreation; }
    public void setDateCreation(Date dateCreation) { this.dateCreation = dateCreation; }

    public enum StatutProbleme {
        OUVERT, EN_INVESTIGATION, SOLUTION_IDENTIFIEE, CLOTURE
    }
}
