package org.jakartaee.itsm.MODEL;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "historique_statut")
public class HistoriqueStatut implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Status ancienStatut;

    @Enumerated(EnumType.STRING)
    private Status nouveauStatut;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date dateChangement = new Date();

    @ManyToOne
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private User utilisateur;

    // Constructeurs
    public HistoriqueStatut() {
    }

    public HistoriqueStatut(Status ancienStatut, Status nouveauStatut, Ticket ticket, User utilisateur) {
        this.ancienStatut = ancienStatut;
        this.nouveauStatut = nouveauStatut;
        this.ticket = ticket;
        this.utilisateur = utilisateur;
        this.dateChangement = new Date();
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Status getAncienStatut() {
        return ancienStatut;
    }

    public void setAncienStatut(Status ancienStatut) {
        this.ancienStatut = ancienStatut;
    }

    public Status getNouveauStatut() {
        return nouveauStatut;
    }

    public void setNouveauStatut(Status nouveauStatut) {
        this.nouveauStatut = nouveauStatut;
    }

    public Date getDateChangement() {
        return dateChangement;
    }

    public void setDateChangement(Date dateChangement) {
        this.dateChangement = dateChangement;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public User getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(User utilisateur) {
        this.utilisateur = utilisateur;
    }

    // Enum
    public enum Status {
        OUVERT, ASSIGNE, EN_COURS, ESCALADE, RESOLU, CLOTURE
    }
}
