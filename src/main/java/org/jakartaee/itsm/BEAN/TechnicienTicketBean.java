package org.jakartaee.itsm.BEAN;

import jakarta.annotation.PostConstruct;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.jakartaee.itsm.DAO.CommentaireDAO;
import org.jakartaee.itsm.DAO.HistoriqueStatutDAO;
import org.jakartaee.itsm.MODEL.Commentaire;
import org.jakartaee.itsm.MODEL.HistoriqueStatut;
import org.jakartaee.itsm.MODEL.Ticket;
import org.jakartaee.itsm.MODEL.Ticket.Status;
import org.jakartaee.itsm.SERVICE.TicketService;

import java.io.Serializable;
import java.util.List;

@Named("technicienTicketBean")
@ViewScoped
public class TechnicienTicketBean implements Serializable {
    private static final long serialVersionUID = 1L;

    // f:viewParam
    private Long ticketId;

    @Inject private TicketService ticketService;
    @Inject private CommentaireDAO commentaireDAO;
    @Inject private HistoriqueStatutDAO historiqueStatutDAO;
    @Inject private LoginBean loginBean;

    private Ticket ticket;
    private List<Commentaire> commentaires;
    private List<HistoriqueStatut> historique;

    private String nouveauCommentaire;
    private String nouveauStatut;

    public void loadTicket() {
        if (ticketId != null) {
            ticket = ticketService.getTicket(ticketId);
            if (ticket != null) {
                commentaires = commentaireDAO.findByTicketId(ticketId);
                historique = historiqueStatutDAO.findByTicketId(ticketId);
            }
        }
    }

    public String ajouterCommentaire(boolean interne) {
        if (ticket == null || nouveauCommentaire == null || nouveauCommentaire.trim().isEmpty()) {
            return null;
        }
        Commentaire c = new Commentaire(nouveauCommentaire.trim(), ticket, loginBean.getUser());
        commentaireDAO.create(c);
        nouveauCommentaire = "";
        commentaires = commentaireDAO.findByTicketId(ticketId);
        return null;
    }

    public String prendreEnCharge() {
        if (ticket == null) return null;
        Status ancien = ticket.getStatut();
        ticket.setStatut(Status.EN_COURS);
        ticket.setTechnicien(loginBean.getUser());
        ticketService.updateTicket(ticket);
        enregistrerHistorique(ancien, Status.EN_COURS);
        return null;
    }

    public String changerStatut() {
        if (ticket == null || nouveauStatut == null || nouveauStatut.isEmpty()) return null;
        try {
            Status ancien = ticket.getStatut();
            Status nouveau = Status.valueOf(nouveauStatut);
            ticket.setStatut(nouveau);
            ticketService.updateTicket(ticket);
            enregistrerHistorique(ancien, nouveau);
        } catch (IllegalArgumentException e) {
            System.err.println("[TechnicienTicketBean] Statut invalide: " + nouveauStatut);
        }
        return null;
    }

    public String escalader() {
        if (ticket == null) return null;
        Status ancien = ticket.getStatut();
        ticket.setStatut(Status.ESCALADE);
        ticketService.updateTicket(ticket);
        enregistrerHistorique(ancien, Status.ESCALADE);
        return null;
    }

    private void enregistrerHistorique(Status ancien, Status nouveau) {
        HistoriqueStatut h = new HistoriqueStatut(
            HistoriqueStatut.Status.valueOf(ancien.name()),
            HistoriqueStatut.Status.valueOf(nouveau.name()),
            ticket,
            loginBean.getUser()
        );
        historiqueStatutDAO.create(h);
        historique = historiqueStatutDAO.findByTicketId(ticketId);
    }

    // Getters / Setters
    public Long getTicketId() { return ticketId; }
    public void setTicketId(Long ticketId) { this.ticketId = ticketId; }
    public Ticket getTicket() { return ticket; }
    public List<Commentaire> getCommentaires() { return commentaires; }
    public List<HistoriqueStatut> getHistorique() { return historique; }
    public String getNouveauCommentaire() { return nouveauCommentaire; }
    public void setNouveauCommentaire(String v) { this.nouveauCommentaire = v; }
    public String getNouveauStatut() { return nouveauStatut; }
    public void setNouveauStatut(String v) { this.nouveauStatut = v; }
}
