package org.jakartaee.itsm.DAO;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.jakartaee.itsm.MODEL.HistoriqueStatut;
import java.util.List;

@Stateless
public class HistoriqueStatutDAO {

    @PersistenceContext(unitName = "itsmPU")
    private EntityManager em;

    public List<HistoriqueStatut> findByTicketId(Long ticketId) {
        try {
            Query q = em.createQuery(
                "SELECT h FROM HistoriqueStatut h LEFT JOIN FETCH h.utilisateur " +
                "WHERE h.ticket.id = :ticketId ORDER BY h.dateChangement ASC",
                HistoriqueStatut.class);
            q.setParameter("ticketId", ticketId);
            return q.getResultList();
        } catch (Exception e) {
            System.err.println("[HistoriqueStatutDAO] Erreur findByTicketId: " + e.getMessage());
            return List.of();
        }
    }

    /** Alias de save() — pour compatibilité avec TechnicienTicketBean */
    public void create(HistoriqueStatut h) {
        save(h);
    }

    public void save(HistoriqueStatut h) {
        try {
            em.persist(h);
        } catch (Exception e) {
            System.err.println("[HistoriqueStatutDAO] Erreur save: " + e.getMessage());
        }
    }
}
