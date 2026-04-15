package org.jakartaee.itsm.DAO;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.jakartaee.itsm.MODEL.SlaConfig;
import org.jakartaee.itsm.MODEL.Ticket.Priority;
import java.util.List;

@Stateless
public class SlaConfigDAO {

    @PersistenceContext(unitName = "itsmPU")
    private EntityManager em;

    public void create(SlaConfig slaConfig) {
        try {
            em.persist(slaConfig);
            System.out.println("[SlaConfigDAO] SLA Config créée: " + slaConfig.getPriorite());
        } catch (Exception e) {
            System.err.println("[SlaConfigDAO] Erreur création SLA: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public SlaConfig findById(Long id) {
        try {
            return em.find(SlaConfig.class, id);
        } catch (Exception e) {
            System.err.println("[SlaConfigDAO] Erreur findById: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public SlaConfig findByPriority(Priority priority) {
        try {
            Query q = em.createQuery("SELECT s FROM SlaConfig s WHERE s.priorite = :priority", SlaConfig.class);
            q.setParameter("priority", priority);
            List<SlaConfig> results = q.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } catch (Exception e) {
            System.err.println("[SlaConfigDAO] Erreur findByPriority: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public List<SlaConfig> findAll() {
        try {
            Query q = em.createQuery("SELECT s FROM SlaConfig s ORDER BY s.priorite", SlaConfig.class);
            List<SlaConfig> configs = q.getResultList();
            System.out.println("[SlaConfigDAO] Total SLA configs: " + configs.size());
            return configs;
        } catch (Exception e) {
            System.err.println("[SlaConfigDAO] Erreur findAll: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    public void update(SlaConfig slaConfig) {
        try {
            em.merge(slaConfig);
            System.out.println("[SlaConfigDAO] SLA Config mise à jour: " + slaConfig.getId());
        } catch (Exception e) {
            System.err.println("[SlaConfigDAO] Erreur update: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void delete(Long id) {
        try {
            SlaConfig config = em.find(SlaConfig.class, id);
            if (config != null) {
                em.remove(config);
                System.out.println("[SlaConfigDAO] SLA Config supprimée: " + id);
            }
        } catch (Exception e) {
            System.err.println("[SlaConfigDAO] Erreur delete: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
