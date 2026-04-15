package org.jakartaee.itsm.DAO;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.jakartaee.itsm.MODEL.Probleme;
import org.jakartaee.itsm.MODEL.Probleme.StatutProbleme;
import java.util.List;

@Stateless
public class ProblemeDAO {

    @PersistenceContext(unitName = "itsmPU")
    private EntityManager em;

    public List<Probleme> findAll() {
        try {
            Query q = em.createQuery("SELECT p FROM Probleme p ORDER BY p.dateCreation DESC", Probleme.class);
            return q.getResultList();
        } catch (Exception e) {
            System.err.println("[ProblemeDAO] Erreur findAll: " + e.getMessage());
            return List.of();
        }
    }

    public Probleme findById(Long id) {
        try {
            return em.find(Probleme.class, id);
        } catch (Exception e) {
            System.err.println("[ProblemeDAO] Erreur findById: " + e.getMessage());
            return null;
        }
    }

    public void save(Probleme p) {
        try {
            if (p.getId() == null) {
                em.persist(p);
            } else {
                em.merge(p);
            }
        } catch (Exception e) {
            System.err.println("[ProblemeDAO] Erreur save: " + e.getMessage());
        }
    }

    public void updateStatut(Long id, StatutProbleme statut) {
        try {
            Probleme p = em.find(Probleme.class, id);
            if (p != null) {
                p.setStatut(statut);
                em.merge(p);
            }
        } catch (Exception e) {
            System.err.println("[ProblemeDAO] Erreur updateStatut: " + e.getMessage());
        }
    }
}
