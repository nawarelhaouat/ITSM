package org.jakartaee.itsm.DAO;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.jakartaee.itsm.MODEL.ChangeRequest;
import org.jakartaee.itsm.MODEL.ChangeRequest.StatutChangement;
import java.util.List;

@Stateless
public class ChangeRequestDAO {

    @PersistenceContext(unitName = "itsmPU")
    private EntityManager em;

    public List<ChangeRequest> findAll() {
        try {
            Query q = em.createQuery(
                "SELECT cr FROM ChangeRequest cr LEFT JOIN FETCH cr.demandeur ORDER BY cr.dateCreation DESC",
                ChangeRequest.class);
            return q.getResultList();
        } catch (Exception e) {
            System.err.println("[ChangeRequestDAO] Erreur findAll: " + e.getMessage());
            return List.of();
        }
    }

    public List<ChangeRequest> findByStatut(StatutChangement statut) {
        try {
            Query q = em.createQuery(
                "SELECT cr FROM ChangeRequest cr LEFT JOIN FETCH cr.demandeur WHERE cr.statut = :statut ORDER BY cr.dateCreation DESC",
                ChangeRequest.class);
            q.setParameter("statut", statut);
            return q.getResultList();
        } catch (Exception e) {
            System.err.println("[ChangeRequestDAO] Erreur findByStatut: " + e.getMessage());
            return List.of();
        }
    }

    public long countByStatut(StatutChangement statut) {
        try {
            Query q = em.createQuery("SELECT COUNT(cr) FROM ChangeRequest cr WHERE cr.statut = :statut");
            q.setParameter("statut", statut);
            return (Long) q.getSingleResult();
        } catch (Exception e) {
            return 0;
        }
    }

    public ChangeRequest findById(Long id) {
        try {
            return em.find(ChangeRequest.class, id);
        } catch (Exception e) {
            return null;
        }
    }

    public void save(ChangeRequest cr) {
        try {
            if (cr.getId() == null) {
                em.persist(cr);
            } else {
                em.merge(cr);
            }
        } catch (Exception e) {
            System.err.println("[ChangeRequestDAO] Erreur save: " + e.getMessage());
        }
    }
}
