package org.jakartaee.itsm.BEAN;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.jakartaee.itsm.MODEL.Ticket;
import org.jakartaee.itsm.MODEL.Ticket.Status;
import org.jakartaee.itsm.MODEL.User;
import org.jakartaee.itsm.MODEL.User.Role;
import org.jakartaee.itsm.DAO.ChangeRequestDAO;
import org.jakartaee.itsm.MODEL.ChangeRequest.StatutChangement;
import org.jakartaee.itsm.SERVICE.TicketService;
import org.jakartaee.itsm.DAO.UserDAO;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Named("managerDashboardBean")
@ViewScoped
public class ManagerDashboardBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject private TicketService ticketService;
    @Inject private UserDAO userDAO;
    @Inject private ChangeRequestDAO changeRequestDAO;

    private long totalTickets;
    private long ticketsEnCours;
    private long ticketsEscalade;
    private String tauxSlaFormatted;
    private long changementsPendants;
    private double satisfactionMoyenne;
    private List<Ticket> recentTickets;
    private List<Ticket> ticketsSlaEnDanger;
    private List<TechnicienCharge> chargeTechniciens;
    private String lastUpdate;

    @PostConstruct
    public void init() {
        try {
            totalTickets    = ticketService.getTotalTickets();
            ticketsEnCours  = ticketService.getCountEnCours();
            ticketsEscalade = ticketService.getCountEscalade();

            // Taux conformité SLA : tickets résolus sans dépasser SLA / total résolus
            long resolus = ticketService.getCountResolu();
            long enDanger = ticketService.getTicketsSlaEnDanger(100).size(); // dépassé
            double taux = resolus > 0 ? Math.max(0, (resolus - enDanger) * 100.0 / Math.max(1, resolus)) : 100.0;
            tauxSlaFormatted = String.format("%.0f", Math.min(100, taux));

            changementsPendants = changeRequestDAO.countByStatut(StatutChangement.SOUMIS)
                                + changeRequestDAO.countByStatut(StatutChangement.EN_REVUE);

            // Satisfaction moyenne
            List<Ticket> all = ticketService.getAllTickets();
            satisfactionMoyenne = all.stream()
                .filter(t -> t.getSatisfaction() != null && t.getSatisfaction() > 0)
                .mapToInt(Ticket::getSatisfaction)
                .average()
                .orElse(0.0);

            recentTickets       = ticketService.getRecentTickets(8);
            ticketsSlaEnDanger  = ticketService.getTicketsSlaEnDanger(80);

            // Charge par technicien
            List<User> techniciens = userDAO.findAll().stream()
                .filter(u -> u.getRole() == Role.TECHNICIEN && u.isActif())
                .collect(Collectors.toList());

            chargeTechniciens = new ArrayList<>();
            for (User tech : techniciens) {
                long enCours   = all.stream().filter(t -> t.getTechnicien() != null && t.getTechnicien().getId().equals(tech.getId()) && (t.getStatut() == Status.EN_COURS || t.getStatut() == Status.ASSIGNE)).count();
                long escalades = all.stream().filter(t -> t.getTechnicien() != null && t.getTechnicien().getId().equals(tech.getId()) && t.getStatut() == Status.ESCALADE).count();
                long resolus2  = all.stream().filter(t -> t.getTechnicien() != null && t.getTechnicien().getId().equals(tech.getId()) && (t.getStatut() == Status.RESOLU || t.getStatut() == Status.CLOTURE)).count();
                chargeTechniciens.add(new TechnicienCharge(tech.getPrenom(), tech.getNom(), tech.getEmail(), enCours, escalades, resolus2));
            }

            lastUpdate = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());

        } catch (Exception e) {
            System.err.println("[ManagerDashboardBean] Erreur init: " + e.getMessage());
            recentTickets      = List.of();
            ticketsSlaEnDanger = List.of();
            chargeTechniciens  = List.of();
            tauxSlaFormatted   = "0";
            lastUpdate         = "—";
        }
    }

    // Getters
    public long getTotalTickets()            { return totalTickets; }
    public long getTicketsEnCours()          { return ticketsEnCours; }
    public long getTicketsEscalade()         { return ticketsEscalade; }
    public String getTauxSlaFormatted()      { return tauxSlaFormatted; }
    public long getChangementsPendants()     { return changementsPendants; }
    public double getSatisfactionMoyenne()   { return satisfactionMoyenne; }
    public List<Ticket> getRecentTickets()   { return recentTickets; }
    public List<Ticket> getTicketsSlaEnDanger() { return ticketsSlaEnDanger; }
    public List<TechnicienCharge> getChargeTechniciens() { return chargeTechniciens; }
    public String getLastUpdate()            { return lastUpdate; }

    // DTO interne pour la charge technicien
    public static class TechnicienCharge implements Serializable {
        private String prenom, nom, email;
        private long ticketsEnCours, ticketsEscalades, ticketsResolus;

        public TechnicienCharge(String prenom, String nom, String email,
                                long enCours, long escalades, long resolus) {
            this.prenom          = prenom;
            this.nom             = nom;
            this.email           = email;
            this.ticketsEnCours  = enCours;
            this.ticketsEscalades= escalades;
            this.ticketsResolus  = resolus;
        }

        public String getPrenom()           { return prenom; }
        public String getNom()              { return nom; }
        public String getEmail()            { return email; }
        public long getTicketsEnCours()     { return ticketsEnCours; }
        public long getTicketsEscalades()   { return ticketsEscalades; }
        public long getTicketsResolus()     { return ticketsResolus; }
    }
}
