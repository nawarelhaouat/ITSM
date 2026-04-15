package org.jakartaee.itsm.BEAN;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.jakartaee.itsm.MODEL.Ticket;
import org.jakartaee.itsm.MODEL.Ticket.Status;
import org.jakartaee.itsm.SERVICE.TicketService;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Named("technicienBean")
@ViewScoped
public class TechnicienBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject
    private TicketService ticketService;

    @Inject
    private LoginBean loginBean;

    private List<Ticket> mesTickets;
    private List<Ticket> ticketsEscalades;

    @PostConstruct
    public void init() {
        try {
            Long userId = loginBean.getUser() != null ? loginBean.getUser().getId() : null;
            List<Ticket> all = ticketService.getAllTickets();

            mesTickets = all.stream()
                .filter(t -> t.getTechnicien() != null && t.getTechnicien().getId().equals(userId))
                .collect(Collectors.toList());

            ticketsEscalades = all.stream()
                .filter(t -> t.getStatut() == Status.ESCALADE
                    && t.getTechnicien() != null
                    && t.getTechnicien().getId().equals(userId))
                .collect(Collectors.toList());

        } catch (Exception e) {
            System.err.println("[TechnicienBean] Erreur init: " + e.getMessage());
            mesTickets = List.of();
            ticketsEscalades = List.of();
        }
    }

    public List<Ticket> getMesTickets()      { return mesTickets; }
    public List<Ticket> getTicketsEscalades() { return ticketsEscalades; }
}
