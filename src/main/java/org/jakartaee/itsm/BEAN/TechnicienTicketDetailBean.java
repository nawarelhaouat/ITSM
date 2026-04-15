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

@Named("technicienTicketDetailBean")
@ViewScoped
public class TechnicienTicketDetailBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject private TicketService ticketService;
    @Inject private LoginBean loginBean;
    @Inject private CommentaireDAO commentaireDAO;
    @Inject private HistoriqueStatutDAO historiqueStatutDAO;

    private Long ticketId;
    private Ticket ticket;
    private List<Commentaire> commentaires;
    private List<HistoriqueStatut> historique;
    private String nouveauCommentaire;
    private String visibiliteCommentaire = "PUBLIC";
    private String nouveauStatut;
    private String successMessage;
    private String errorMessage;

    @PostConstruct
    public void init() {
        // ticketId est injecté via @ViewParam avant @PostConstruct via f:viewAction
    }

    public void loadTicket() {
        try {
            if (ticketId != null) {
                ticket = ticketService.getTicket(ticketId);
                if (ticket != null) {
                    commentaires = commentaireDAO.findByTicketId(ticketId);
                    historique   = historiqueStatutDAO.findByTicketId(ticketId);
                }
            }
        } catch (Exception e) {
            System.err.println("[TechnicienTicketDetailBean] Erreur loadTicket: " + e.getMessage());
            errorMessage = "Impossible de charger le ticket.";
        }
    }

    public String ajouterCommentaire() {
        try {
            if (nouveauCommentaire == null || nouveauCommentaire.trim().isEmpty()) {
                errorMessage = "Le commentaire ne peut pas être vide.";
                return null;
            }
            Commentaire c = new Commentaire(nouveauCommentaire.trim(), ticket, loginBean.getUser());
            commentaireDAO.save(c);
            nouveauCommentaire = "";
            successMessage = "Commentaire ajouté avec succès.";
            errorMessage = null;
            loadTicket();
        } catch (Exception e) {
            errorMessage = "Erreur lors de l'ajout du commentaire.";
        }
        return null;
    }

    public String changerStatut() {
        try {
            if (nouveauStatut == null || nouveauStatut.isEmpty()) {
                errorMessage = "Veuillez sélectionner un statut.";
                return null;
            }
            Status ancienStatut = ticket.getStatut();
            Status newStatus    = Status.valueOf(nouveauStatut);

            // Enregistrer dans l'historique
            HistoriqueStatut h = new HistoriqueStatut(
                HistoriqueStatut.Status.valueOf(ancienStatut.name()),
                HistoriqueStatut.Status.valueOf(newStatus.name()),
                ticket, loginBean.getUser()
            );
            historiqueStatutDAO.save(h);

            ticket.setStatut(newStatus);
            ticketService.updateTicket(ticket);

            nouveauStatut = "";
            successMessage = "Statut mis à jour : " + newStatus.name();
            errorMessage = null;
            loadTicket();
        } catch (Exception e) {
            errorMessage = "Erreur lors du changement de statut : " + e.getMessage();
        }
        return null;
    }

    public String escalader() {
        try {
            Status ancienStatut = ticket.getStatut();
            HistoriqueStatut h = new HistoriqueStatut(
                HistoriqueStatut.Status.valueOf(ancienStatut.name()),
                HistoriqueStatut.Status.ESCALADE,
                ticket, loginBean.getUser()
            );
            historiqueStatutDAO.save(h);

            ticket.setStatut(Status.ESCALADE);
            ticketService.updateTicket(ticket);

            successMessage = "Ticket escaladé avec succès.";
            errorMessage = null;
            loadTicket();
        } catch (Exception e) {
            errorMessage = "Erreur lors de l'escalade : " + e.getMessage();
        }
        return null;
    }

    // Getters / Setters
    public Long getTicketId()                  { return ticketId; }
    public void setTicketId(Long ticketId)     { this.ticketId = ticketId; }
    public Ticket getTicket()                  { return ticket; }
    public List<Commentaire> getCommentaires() { return commentaires; }
    public List<HistoriqueStatut> getHistorique() { return historique; }
    public String getNouveauCommentaire()      { return nouveauCommentaire; }
    public void setNouveauCommentaire(String v){ this.nouveauCommentaire = v; }
    public String getVisibiliteCommentaire()   { return visibiliteCommentaire; }
    public void setVisibiliteCommentaire(String v){ this.visibiliteCommentaire = v; }
    public String getNouveauStatut()           { return nouveauStatut; }
    public void setNouveauStatut(String v)     { this.nouveauStatut = v; }
    public String getSuccessMessage()          { return successMessage; }
    public String getErrorMessage()            { return errorMessage; }
}
