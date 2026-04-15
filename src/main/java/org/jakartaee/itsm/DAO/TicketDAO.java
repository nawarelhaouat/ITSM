package org.jakartaee.itsm.DAO;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.jakartaee.itsm.MODEL.Ticket;
import org.jakartaee.itsm.MODEL.Ticket.Status;
import org.jakartaee.itsm.MODEL.Ticket.Priority;
import org.jakartaee.itsm.MODEL.User;
import java.util.List;

@Stateless
public class TicketDAO {
    
    @PersistenceContext(unitName = "itsmPU")
    private EntityManager em;

    public void create(Ticket ticket) {
        try {
            em.persist(ticket);
            System.out.println("[TicketDAO] Ticket créé: " + ticket.getTitre());
        } catch (Exception e) {
            System.err.println("[TicketDAO] Erreur création ticket: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Ticket findById(Long id) {
        try {
            return em.find(Ticket.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Ticket> findAll() {
        try {
            Query q = em.createQuery("SELECT t FROM Ticket t ORDER BY t.dateCreation DESC", Ticket.class);
            List<Ticket> tickets = q.getResultList();
            System.out.println("[TicketDAO] Total tickets trouvés: " + tickets.size());
            return tickets;
        } catch (Exception e) {
            System.err.println("[TicketDAO] Erreur findAll: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    public List<Ticket> findByStatus(Status status) {
        try {
            Query q = em.createQuery("SELECT t FROM Ticket t WHERE t.statut = :status ORDER BY t.priorite DESC", Ticket.class);
            q.setParameter("status", status);
            List<Ticket> tickets = q.getResultList();
            System.out.println("[TicketDAO] Tickets avec statut " + status + ": " + tickets.size());
            return tickets;
        } catch (Exception e) {
            System.err.println("[TicketDAO] Erreur findByStatus: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    public List<Ticket> findByPriority(Priority priority) {
        try {
            Query q = em.createQuery("SELECT t FROM Ticket t WHERE t.priorite = :priority ORDER BY t.dateCreation DESC", Ticket.class);
            q.setParameter("priority", priority);
            List<Ticket> tickets = q.getResultList();
            System.out.println("[TicketDAO] Tickets avec priorité " + priority + ": " + tickets.size());
            return tickets;
        } catch (Exception e) {
            System.err.println("[TicketDAO] Erreur findByPriority: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    public List<Ticket> findEscalades() {
        return findByStatus(Status.ESCALADE);
    }

    public List<Ticket> findCritiques() {
        return findByPriority(Priority.CRITIQUE);
    }

    public long countByStatus(Status status) {
        try {
            Query q = em.createQuery("SELECT COUNT(t) FROM Ticket t WHERE t.statut = :status");
            q.setParameter("status", status);
            long count = (Long) q.getSingleResult();
            System.out.println("[TicketDAO] Count tickets statut " + status + ": " + count);
            return count;
        } catch (Exception e) {
            System.err.println("[TicketDAO] Erreur countByStatus: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    public long countAll() {
        try {
            Query q = em.createQuery("SELECT COUNT(t) FROM Ticket t");
            long count = (Long) q.getSingleResult();
            System.out.println("[TicketDAO] Total tickets: " + count);
            return count;
        } catch (Exception e) {
            System.err.println("[TicketDAO] Erreur countAll: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    public void update(Ticket ticket) {
        try {
            em.merge(ticket);
            System.out.println("[TicketDAO] Ticket mis à jour: " + ticket.getId());
        } catch (Exception e) {
            System.err.println("[TicketDAO] Erreur update: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void delete(Long id) {
        try {
            Ticket ticket = em.find(Ticket.class, id);
            if (ticket != null) {
                em.remove(ticket);
                System.out.println("[TicketDAO] Ticket supprimé: " + id);
            }
        } catch (Exception e) {
            System.err.println("[TicketDAO] Erreur delete: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Ticket> findRecent(int limit) {
        try {
            Query q = em.createQuery(
                    "SELECT t FROM Ticket t " +
                            "LEFT JOIN FETCH t.declarant " +   // ← ajoute ça
                            "LEFT JOIN FETCH t.technicien " +  // ← et ça (utilisé ailleurs)
                            "ORDER BY t.dateCreation DESC", Ticket.class);
            q.setMaxResults(limit);
            return q.getResultList();
        } catch (Exception e) {
            System.err.println("[TicketDAO] Erreur findRecent: " + e.getMessage());
            return List.of();
        }
    }

public List<Ticket> findSlaEnDanger(int seuilPct) {
    try {
        // On récupère tous les tickets non résolus avec dateLimiteSla définie
        // Le filtrage par % se fait en Java via getSlaPercent()
        Query q = em.createQuery(
            "SELECT t FROM Ticket t WHERE t.dateLimiteSla IS NOT NULL " +
            "AND t.statut NOT IN ('RESOLU', 'CLOTURE') " +
            "ORDER BY t.dateLimiteSla ASC", Ticket.class);
        
        List<Ticket> tous = q.getResultList();
        
        // Filtrer ceux dont le slaPercent >= seuil
        return tous.stream()
            .filter(t -> t.getSlaPercent() >= seuilPct)
            .collect(java.util.stream.Collectors.toList());
            
    } catch (Exception e) {
        System.err.println("[TicketDAO] Erreur findSlaEnDanger: " + e.getMessage());
        return List.of();
    }
}
}


