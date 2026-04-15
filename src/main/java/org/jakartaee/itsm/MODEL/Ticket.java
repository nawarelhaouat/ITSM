package org.jakartaee.itsm.MODEL;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "ticket")
public class Ticket implements Serializable {
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
    private Priority priorite;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status statut = Status.OUVERT;

    @Column(length = 100)
    private String categorie;

    @Column(name = "date_limite_sla")
    private Date dateLimiteSla;

    @Column(name = "date_resolution")
    private Date dateResolution;

    @Column(nullable = true)
    private Integer satisfaction;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_creation", nullable = false, updatable = false)
    private Date dateCreation = new Date();

    @ManyToOne
    @JoinColumn(name = "declarant_id")
    private User declarant;

    @ManyToOne
    @JoinColumn(name = "technicien_id")
    private User technicien;

    @ManyToOne
    @JoinColumn(name = "sla_config_id")
    private SlaConfig slaConfig;

    // Constructeurs
    public Ticket() {
    }

    public Ticket(String titre, String description, Priority priorite) {
        this.titre = titre;
        this.description = description;
        this.priorite = priorite;
        this.statut = Status.OUVERT;
        this.dateCreation = new Date();
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    @Transient
    public int getSlaPercent() {
        if (dateLimiteSla == null || dateCreation == null) return 0;
        long total = dateLimiteSla.getTime() - dateCreation.getTime();
        long ecoule = new Date().getTime() - dateCreation.getTime();
        if (total <= 0) return 100;
        int percent = (int) ((ecoule * 100) / total);
        return Math.min(100, Math.max(0, percent));
    }
    public Priority getPriorite() {
        return priorite;
    }

    public void setPriorite(Priority priorite) {
        this.priorite = priorite;
    }

    public Status getStatut() {
        return statut;
    }

    public void setStatut(Status statut) {
        this.statut = statut;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public Date getDateLimiteSla() {
        return dateLimiteSla;
    }

    public void setDateLimiteSla(Date dateLimiteSla) {
        this.dateLimiteSla = dateLimiteSla;
    }

    public Date getDateResolution() {
        return dateResolution;
    }

    public void setDateResolution(Date dateResolution) {
        this.dateResolution = dateResolution;
    }

    public Integer getSatisfaction() {
        return satisfaction;
    }

    public void setSatisfaction(Integer satisfaction) {
        this.satisfaction = satisfaction;
    }

    public Date getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }

    public User getDeclarant() {
        return declarant;
    }

    public void setDeclarant(User declarant) {
        this.declarant = declarant;
    }

    public User getTechnicien() {
        return technicien;
    }

    public void setTechnicien(User technicien) {
        this.technicien = technicien;
    }

    public SlaConfig getSlaConfig() {
        return slaConfig;
    }

    public void setSlaConfig(SlaConfig slaConfig) {
        this.slaConfig = slaConfig;
    }

    // Enums
    public enum Priority {
        CRITIQUE, HAUTE, MOYENNE, BASSE
    }

    public enum Status {
        OUVERT, ASSIGNE, EN_COURS, ESCALADE, RESOLU, CLOTURE
    }
}
