package org.jakartaee.itsm.BEAN;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.jakartaee.itsm.MODEL.Ticket;
import org.jakartaee.itsm.MODEL.Ticket.Status;
import org.jakartaee.itsm.SERVICE.TicketService;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Named("technicienDashboardBean")
@ViewScoped
public class TechnicienDashboardBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject
    private TicketService ticketService;

    @Inject
    private LoginBean loginBean;

    private int mesTicketsTotal;
    private int mesTicketsEnCours;
    private int mesTicketsEscalade;
    private int mesTicketsResolus;
    private List<Ticket> mesTicketsActifs;
    private List<Ticket> ticketsSlaEnDanger;
    private String lastUpdate;

    @PostConstruct
    public void init() {
        try {
            Long userId = loginBean.getUser() != null ? loginBean.getUser().getId() : null;

            List<Ticket> allTickets = ticketService.getAllTickets();

            List<Ticket> mesTickets = allTickets.stream()
                .filter(t -> t.getTechnicien() != null && t.getTechnicien().getId().equals(userId))
                .collect(Collectors.toList());

            mesTicketsTotal   = mesTickets.size();
            mesTicketsEnCours = (int) mesTickets.stream().filter(t -> t.getStatut() == Status.EN_COURS || t.getStatut() == Status.ASSIGNE).count();
            mesTicketsEscalade = (int) mesTickets.stream().filter(t -> t.getStatut() == Status.ESCALADE).count();
            mesTicketsResolus  = (int) mesTickets.stream().filter(t -> t.getStatut() == Status.RESOLU || t.getStatut() == Status.CLOTURE).count();

            mesTicketsActifs = mesTickets.stream()
                .filter(t -> t.getStatut() != Status.RESOLU && t.getStatut() != Status.CLOTURE)
                .limit(10)
                .collect(Collectors.toList());

            // SLA en danger : tickets de ce technicien avec slaPercent >= 80
            ticketsSlaEnDanger = ticketService.getTicketsSlaEnDanger(80).stream()
                .filter(t -> t.getTechnicien() != null && t.getTechnicien().getId().equals(userId))
                .collect(Collectors.toList());

            lastUpdate = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());
        } catch (Exception e) {
            System.err.println("[TechnicienDashboardBean] Erreur init: " + e.getMessage());
            mesTicketsActifs = List.of();
            ticketsSlaEnDanger = List.of();
            lastUpdate = "—";
        }
    }

    // Getters
    public int getMesTicketsTotal()    { return mesTicketsTotal; }
    public int getMesTicketsEnCours()  { return mesTicketsEnCours; }
    public int getMesTicketsEscalade() { return mesTicketsEscalade; }
    public int getMesTicketsResolus()  { return mesTicketsResolus; }
    public List<Ticket> getMesTicketsActifs()    { return mesTicketsActifs; }
    public List<Ticket> getTicketsSlaEnDanger()  { return ticketsSlaEnDanger; }
    public String getLastUpdate()      { return lastUpdate; }
}
