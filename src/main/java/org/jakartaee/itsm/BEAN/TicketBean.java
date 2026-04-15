package org.jakartaee.itsm.BEAN;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.jakartaee.itsm.MODEL.Ticket;
import org.jakartaee.itsm.SERVICE.TicketService;

import java.io.Serializable;
import java.util.List;

@Named("ticketBean")
@ViewScoped
public class TicketBean implements Serializable {

    @Inject
    private LoginBean loginBean;

    @Inject
    private TicketService ticketService;

    private List<Ticket> allTickets;

    private String titre;
    private String description;
    private String priorite;
    private String categorie;

    @PostConstruct
    public void init() {
        allTickets = ticketService.getAllTickets(); // IMPORTANT
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPriorite() {
        return priorite;
    }

    public void setPriorite(String priorite) {
        this.priorite = priorite;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }
    public List<Ticket> getAllTickets() {
        return allTickets;
    }
    public int getTotalTickets() {
        return allTickets != null ? allTickets.size() : 0;
    }

    public long getTicketsEnCours() {
        return allTickets == null ? 0 :
                allTickets.stream()
                        .filter(t -> t.getStatut().name().equals("EN_COURS"))
                        .count();
    }

    public String createTicket() {
        Ticket t = new Ticket();

        t.setTitre(titre);
        t.setDescription(description);
        t.setPriorite(Ticket.Priority.valueOf(priorite));
        t.setCategorie(categorie);

        t.setDeclarant(loginBean.getUser()); // IMPORTANT

        ticketService.createTicket(t);

        return "/user/dashboard?faces-redirect=true";
    }

    public long getTicketsResolus() {
        return allTickets == null ? 0 :
                allTickets.stream()
                        .filter(t -> t.getStatut().name().equals("RESOLU"))
                        .count();
    }

    public List<Ticket> getMesTickets() {
        if (allTickets == null) return null;

        return allTickets.stream()
                .filter(t -> t.getDeclarant().getId()
                        .equals(loginBean.getUser().getId()))
                .toList();
    }
}