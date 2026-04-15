package org.jakartaee.itsm.BEAN;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.jakartaee.itsm.SERVICE.TicketService;
import org.jakartaee.itsm.MODEL.Ticket.Status;
import java.io.Serializable;

@Named("notifBean")
@RequestScoped
public class NotifBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject
    private TicketService ticketService;

    @Inject
    private LoginBean loginBean;

    /**
     * Retourne le nombre de tickets escaladés (utilisé comme "non lus" pour la cloche)
     */
    public int getUnreadCount() {
        try {
            if (loginBean == null || loginBean.getUser() == null) return 0;
            String role = loginBean.getUser().getRole().name();
            if ("TECHNICIEN".equals(role)) {
                // Tickets ESCALADE assignés à ce technicien
                return (int) ticketService.getAllTickets().stream()
                    .filter(t -> t.getStatut() == Status.ESCALADE
                        && t.getTechnicien() != null
                        && t.getTechnicien().getId().equals(loginBean.getUser().getId()))
                    .count();
            } else if ("MANAGER".equals(role) || "ADMIN".equals(role)) {
                return (int) ticketService.getCountEscalade();
            }
            return 0;
        } catch (Exception e) {
            return 0;
        }
    }
}
