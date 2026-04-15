package org.jakartaee.itsm.BEAN;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.jakartaee.itsm.DAO.CommentaireDAO;
import org.jakartaee.itsm.DAO.HistoriqueStatutDAO;
import org.jakartaee.itsm.DAO.UserDAO;
import org.jakartaee.itsm.MODEL.Commentaire;
import org.jakartaee.itsm.MODEL.HistoriqueStatut;
import org.jakartaee.itsm.MODEL.Ticket;
import org.jakartaee.itsm.MODEL.Ticket.Status;
import org.jakartaee.itsm.MODEL.User;
import org.jakartaee.itsm.MODEL.User.Role;
import org.jakartaee.itsm.SERVICE.TicketService;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Named("managerTicketDetailBean")
@ViewScoped
public class ManagerTicketDetailBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject private TicketService ticketService;
    @Inject private UserDAO userDAO;
    @Inject private CommentaireDAO commentaireDAO;
    @Inject private HistoriqueStatutDAO historiqueStatutDAO;
    @Inject private LoginBean loginBean;

    private Long ticketId;
    private Ticket ticket;
    private List<Commentaire> commentaires;
    private List<HistoriqueStatut> historique;
    private List<User> techniciens;
    private String nouveauCommentaire;
    private String nouveauStatut;
    private Long selectedTechnicienId;
    private String successMessage;
    private String errorMessage;

    @PostConstruct
    public void init() {
        try {
            techniciens = userDAO.findAll().stream()
                .filter(u -> u.getRole() == Role.TECHNICIEN && u.isActif())
                .collect(Collectors.toList());
        } catch (Exception e) {
            techniciens = List.of();
        }
    }

    public void loadTicket() {
        try {
            if (ticketId != null) {
                ticket       = ticketService.getTicket(ticketId);
                commentaires = commentaireDAO.findByTicketId(ticketId);
                historique   = historiqueStatutDAO.findByTicketId(ticketId);
            }
        } catch (Exception e) {
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
            successMessage = "Commentaire ajouté.";
            errorMessage   = null;
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
            Status ancien    = ticket.getStatut();
            Status newStatus = Status.valueOf(nouveauStatut);

            HistoriqueStatut h = new HistoriqueStatut(
                HistoriqueStatut.Status.valueOf(ancien.name()),
                HistoriqueStatut.Status.valueOf(newStatus.name()),
                ticket, loginBean.getUser()
            );
            historiqueStatutDAO.save(h);

            ticket.setStatut(newStatus);
            ticketService.updateTicket(ticket);
            successMessage = "Statut mis à jour : " + newStatus.name();
            errorMessage   = null;
            nouveauStatut  = "";
            loadTicket();
        } catch (Exception e) {
            errorMessage = "Erreur changement statut : " + e.getMessage();
        }
        return null;
    }

    public String assignerTicket() {
        try {
            if (selectedTechnicienId == null) {
                errorMessage = "Veuillez sélectionner un technicien.";
                return null;
            }
            User tech    = userDAO.findById(selectedTechnicienId);
            Status ancien = ticket.getStatut();

            HistoriqueStatut h = new HistoriqueStatut(
                HistoriqueStatut.Status.valueOf(ancien.name()),
                HistoriqueStatut.Status.ASSIGNE,
                ticket, loginBean.getUser()
            );
            historiqueStatutDAO.save(h);

            ticket.setTechnicien(tech);
            ticket.setStatut(Status.ASSIGNE);
            ticketService.updateTicket(ticket);
            successMessage = "Ticket assigné à " + tech.getPrenom() + " " + tech.getNom();
            errorMessage   = null;
            loadTicket();
        } catch (Exception e) {
            errorMessage = "Erreur assignation : " + e.getMessage();
        }
        return null;
    }

    // Getters / Setters
    public Long getTicketId()                    { return ticketId; }
    public void setTicketId(Long v)              { this.ticketId = v; }
    public Ticket getTicket()                    { return ticket; }
    public List<Commentaire> getCommentaires()   { return commentaires; }
    public List<HistoriqueStatut> getHistorique(){ return historique; }
    public List<User> getTechniciens()           { return techniciens; }
    public String getNouveauCommentaire()        { return nouveauCommentaire; }
    public void setNouveauCommentaire(String v)  { this.nouveauCommentaire = v; }
    public String getNouveauStatut()             { return nouveauStatut; }
    public void setNouveauStatut(String v)       { this.nouveauStatut = v; }
    public Long getSelectedTechnicienId()        { return selectedTechnicienId; }
    public void setSelectedTechnicienId(Long v)  { this.selectedTechnicienId = v; }
    public String getSuccessMessage()            { return successMessage; }
    public String getErrorMessage()              { return errorMessage; }
}
