package org.jakartaee.itsm.SERVICE;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import org.jakartaee.itsm.DAO.TicketDAO;
import org.jakartaee.itsm.MODEL.Ticket;
import org.jakartaee.itsm.MODEL.Ticket.Status;
import org.jakartaee.itsm.MODEL.Ticket.Priority;
import java.util.List;

@Stateless
public class TicketService {
    @Inject
    private TicketDAO ticketDAO;

    public void createTicket(Ticket ticket) {
        ticketDAO.create(ticket);
    }

    public Ticket getTicket(Long id) {
        return ticketDAO.findById(id);
    }

    public List<Ticket> getAllTickets() {
        return ticketDAO.findAll();
    }

    public List<Ticket> getTicketsByStatus(Status status) {
        return ticketDAO.findByStatus(status);
    }

    public List<Ticket> getTicketsByPriority(Priority priority) {
        return ticketDAO.findByPriority(priority);
    }

    public List<Ticket> getEscaledes() {
        return ticketDAO.findEscalades();
    }

    public List<Ticket> getCritiques() {
        return ticketDAO.findCritiques();
    }

    public long countTicketsByStatus(Status status) {
        return ticketDAO.countByStatus(status);
    }

    public long getTotalTickets() {
        return ticketDAO.countAll();
    }

    public void updateTicket(Ticket ticket) {
        ticketDAO.update(ticket);
    }

    public void deleteTicket(Long id) {
        ticketDAO.delete(id);
    }

    public long getCountOuvert() {
        return countTicketsByStatus(Status.OUVERT);
    }

    public long getCountAssigne() {
        return countTicketsByStatus(Status.ASSIGNE);
    }

    public long getCountEnCours() {
        return countTicketsByStatus(Status.EN_COURS);
    }

    public long getCountResolu() {
        return countTicketsByStatus(Status.RESOLU);
    }

    public long getCountEscalade() {
        return countTicketsByStatus(Status.ESCALADE);
    }
    public List<Ticket> getRecentTickets(int limit) {
    return ticketDAO.findRecent(limit);
}

public List<Ticket> getTicketsSlaEnDanger(int seuilPct) {
    return ticketDAO.findSlaEnDanger(seuilPct);
}
}
