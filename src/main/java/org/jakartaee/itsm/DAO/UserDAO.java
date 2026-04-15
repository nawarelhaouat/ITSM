package org.jakartaee.itsm.DAO;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.jakartaee.itsm.MODEL.User;
import java.util.List;

/**
 * DAO (Data Access Object) pour la gestion des utilisateurs
 * Utilise JPA/EntityManager pour se connecter à la base de données
 */
@Stateless
public class UserDAO {
    
    @PersistenceContext(unitName = "itsmPU")
    private EntityManager em;

    /**
     * Trouver un utilisateur par email
     */
    public User findByEmail(String email) {
        try {
            System.out.println("[UserDAO] Recherche utilisateur: " + email);
            Query q = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class);
            q.setParameter("email", email);
            List<User> results = q.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Trouver un utilisateur par ID
     */
    public User findById(Long id) {
        try {
            return em.find(User.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Récupérer tous les utilisateurs
     */
    public List<User> findAll() {
        try {
            Query q = em.createQuery("SELECT u FROM User u", User.class);
            return q.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    /**
     * Sauvegarder un utilisateur (créer ou mettre à jour)
     */
    public List<User> filter(String search, String role, Boolean actif) {
        try {
            String jpql = "SELECT u FROM User u WHERE 1=1";

            if (search != null && !search.isEmpty()) {
                jpql += " AND (LOWER(u.nom) LIKE :search OR LOWER(u.email) LIKE :search)";
            }

            if (role != null && !role.isEmpty()) {
                jpql += " AND u.role = :role";
            }

            if (actif != null) {
                jpql += " AND u.actif = :actif";
            }

            Query q = em.createQuery(jpql, User.class);

            if (search != null && !search.isEmpty()) {
                q.setParameter("search", "%" + search.toLowerCase() + "%");
            }

            if (role != null && !role.isEmpty()) {
                q.setParameter("role", role);
            }

            if (actif != null) {
                q.setParameter("actif", actif);
            }

            return q.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
    public void update(User user) {
        try {
            em.merge(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void save(User user) {
        try {
            if (user.getId() == null) {
                em.persist(user);
            } else {
                em.merge(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Supprimer un utilisateur
     */
    public void delete(Long id) {
        try {
            User user = em.find(User.class, id);
            if (user != null) {
                em.remove(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Compter le nombre d'utilisateurs
     */
    public long count() {
        try {
            Query q = em.createQuery("SELECT COUNT(u) FROM User u");
            return (Long) q.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public long countActifs() {
    try {
        Query q = em.createQuery("SELECT COUNT(u) FROM User u WHERE u.actif = true");
        return (Long) q.getSingleResult();
    } catch (Exception e) {
        System.err.println("[UserDAO] Erreur countActifs: " + e.getMessage());
        return 0;
    }
}
}
