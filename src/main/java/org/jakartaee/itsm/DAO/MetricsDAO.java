package org.jakartaee.itsm.DAO;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TemporalType;
import org.jakartaee.itsm.MODEL.Metrics;
import java.util.Date;
import java.util.List;

@Stateless
public class MetricsDAO {
    
    @PersistenceContext(unitName = "itsmPU")
    private EntityManager em;

    public void create(Metrics metrics) {
        try {
            em.persist(metrics);
            System.out.println("[MetricsDAO] Metrics créées pour: " + metrics.getPeriode());
        } catch (Exception e) {
            System.err.println("[MetricsDAO] Erreur création metrics: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Metrics findById(Long id) {
        try {
            return em.find(Metrics.class, id);
        } catch (Exception e) {
            System.err.println("[MetricsDAO] Erreur findById: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public List<Metrics> findAll() {
        try {
            Query q = em.createQuery("SELECT m FROM Metrics m ORDER BY m.periode DESC", Metrics.class);
            List<Metrics> metrics = q.getResultList();
            System.out.println("[MetricsDAO] Total metrics: " + metrics.size());
            return metrics;
        } catch (Exception e) {
            System.err.println("[MetricsDAO] Erreur findAll: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    public Metrics findByPeriode(Date periode) {
        try {
            Query q = em.createQuery("SELECT m FROM Metrics m WHERE m.periode = :periode", Metrics.class);
            q.setParameter("periode", periode, TemporalType.DATE);
            List<Metrics> results = q.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } catch (Exception e) {
            System.err.println("[MetricsDAO] Erreur findByPeriode: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public List<Metrics> findLastDays(int days) {
        try {
            Query q = em.createQuery("SELECT m FROM Metrics m WHERE m.periode >= CURRENT_DATE - " + days + " ORDER BY m.periode DESC", Metrics.class);
            List<Metrics> metrics = q.getResultList();
            System.out.println("[MetricsDAO] Metrics des " + days + " derniers jours: " + metrics.size());
            return metrics;
        } catch (Exception e) {
            System.err.println("[MetricsDAO] Erreur findLastDays: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    public void update(Metrics metrics) {
        try {
            em.merge(metrics);
            System.out.println("[MetricsDAO] Metrics mise à jour: " + metrics.getId());
        } catch (Exception e) {
            System.err.println("[MetricsDAO] Erreur update: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void delete(Long id) {
        try {
            Metrics metrics = em.find(Metrics.class, id);
            if (metrics != null) {
                em.remove(metrics);
                System.out.println("[MetricsDAO] Metrics supprimée: " + id);
            }
        } catch (Exception e) {
            System.err.println("[MetricsDAO] Erreur delete: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
