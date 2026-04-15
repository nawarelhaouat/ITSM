package org.jakartaee.itsm.BEAN;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.jakartaee.itsm.MODEL.Ticket;
import org.jakartaee.itsm.MODEL.Ticket.Status;
import org.jakartaee.itsm.MODEL.User;
import org.jakartaee.itsm.MODEL.User.Role;
import org.jakartaee.itsm.MODEL.HistoriqueStatut;
import org.jakartaee.itsm.SERVICE.TicketService;
import org.jakartaee.itsm.DAO.UserDAO;
import org.jakartaee.itsm.DAO.HistoriqueStatutDAO;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Named("managerTicketBean")
@ViewScoped
public class ManagerTicketBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject private TicketService ticketService;
    @Inject private UserDAO userDAO;
    @Inject private HistoriqueStatutDAO historiqueStatutDAO;
    @Inject private LoginBean loginBean;

    private List<Ticket> allTickets;
    private List<Ticket> ticketsEscalades;
    private List<User>   techniciens;
    private Long selectedTicketId;
    private Long selectedTechnicienId;
    private String successMessage;
    private String errorMessage;

    @PostConstruct
    public void init() {
        try {
            allTickets       = ticketService.getAllTickets();
            ticketsEscalades = ticketService.getEscaledes();
            techniciens      = userDAO.findAll().stream()
                .filter(u -> u.getRole() == Role.TECHNICIEN && u.isActif())
                .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("[ManagerTicketBean] Erreur init: " + e.getMessage());
            allTickets       = List.of();
            ticketsEscalades = List.of();
            techniciens      = List.of();
        }
    }

    /**
     * Assigner depuis la ligne du tableau (ticketId passé en parametre)
     */
    public String assignerTicketFromRow(Long ticketId) {
        try {
            if (ticketId == null || selectedTechnicienId == null || selectedTechnicienId == 0) {
                errorMessage = "Veuillez selectionner un technicien.";
                successMessage = null;
                return null;
            }
            Ticket t  = ticketService.getTicket(ticketId);
            User tech = userDAO.findById(selectedTechnicienId);
            if (t == null || tech == null) {
                errorMessage = "Ticket ou technicien introuvable.";
                successMessage = null;
                return null;
            }
            Status ancien = t.getStatut();
            t.setTechnicien(tech);
            if (ancien == Status.OUVERT) {
                t.setStatut(Status.ASSIGNE);
            }
            ticketService.updateTicket(t);

            HistoriqueStatut h = new HistoriqueStatut(
                HistoriqueStatut.Status.valueOf(ancien.name()),
                HistoriqueStatut.Status.valueOf(t.getStatut().name()),
                t, loginBean.getUser()
            );
            historiqueStatutDAO.save(h);

            successMessage = "Ticket #" + ticketId + " assigne a " + tech.getPrenom() + " " + tech.getNom();
            errorMessage   = null;
            selectedTechnicienId = null;
            init(); // reload list
        } catch (Exception e) {
            errorMessage   = "Erreur: " + e.getMessage();
            successMessage = null;
        }
        return null;
    }

    /** Ancienne methode conservee pour compatibilite */
    public String assignerTicket() {
        return assignerTicketFromRow(selectedTicketId);
    }

    // Getters / Setters
    public List<Ticket> getAllTickets()       { return allTickets; }
    public List<Ticket> getTicketsEscalades() { return ticketsEscalades; }
    public List<User>   getTechniciens()      { return techniciens; }
    public Long getSelectedTicketId()         { return selectedTicketId; }
    public void setSelectedTicketId(Long v)   { this.selectedTicketId = v; }
    public Long getSelectedTechnicienId()         { return selectedTechnicienId; }
    public void setSelectedTechnicienId(Long v)   { this.selectedTechnicienId = v; }
    public String getSuccessMessage()         { return successMessage; }
    public String getErrorMessage()           { return errorMessage; }
}
