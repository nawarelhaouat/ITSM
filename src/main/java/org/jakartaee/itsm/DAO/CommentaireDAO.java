package org.jakartaee.itsm.DAO;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.jakartaee.itsm.MODEL.Commentaire;
import java.util.List;

@Stateless
public class CommentaireDAO {

    @PersistenceContext(unitName = "itsmPU")
    private EntityManager em;

    public List<Commentaire> findByTicketId(Long ticketId) {
        try {
            Query q = em.createQuery(
                "SELECT c FROM Commentaire c LEFT JOIN FETCH c.auteur " +
                "WHERE c.ticket.id = :ticketId ORDER BY c.dateCreation ASC",
                Commentaire.class);
            q.setParameter("ticketId", ticketId);
            return q.getResultList();
        } catch (Exception e) {
            System.err.println("[CommentaireDAO] Erreur findByTicketId: " + e.getMessage());
            return List.of();
        }
    }

    /** Alias de save() — pour compatibilité avec TechnicienTicketBean */
    public void create(Commentaire c) {
        save(c);
    }

    public void save(Commentaire c) {
        try {
            em.persist(c);
        } catch (Exception e) {
            System.err.println("[CommentaireDAO] Erreur save: " + e.getMessage());
        }
    }
}
