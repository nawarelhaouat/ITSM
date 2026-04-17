package org.jakartaee.itsm.SERVICE;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import org.jakartaee.itsm.DAO.UserDAO;
import org.jakartaee.itsm.MODEL.User;

import java.io.Serializable;
import org.mindrot.jbcrypt.BCrypt;

@Stateless
public class UserService implements Serializable {
    @Inject
    private UserDAO userDAO;

    public UserService() {
        // CDI injection will handle userDAO
    }

    /**
     * Authentifier un utilisateur
     * @param email Email de l'utilisateur
     * @param password Mot de passe de l'utilisateur
     * @return L'objet User si authentification réussie, null sinon
     */
    public User authenticateUser(String email, String password) {
        // Chercher l'utilisateur par email
        System.out.println("[UserService] Authentification pour: " + email);
        User user = userDAO.findByEmail(email);

        if (user != null) {
            System.out.println("[UserService] Utilisateur trouvé - Actif: " + user.isActif() + ", Mot de passe BD: " + user.getMotDePasse());
            if (user.isActif()) {
                // Vérifier le mot de passe
                // Pour la démo, on compare directement (en production, utiliser du hashage)
                if (BCrypt.checkpw(password, user.getMotDePasse())) {
                    System.out.println("[UserService] Mot de passe correct - Retour utilisateur");
                    return user;
                } else {
                    System.out.println("[UserService] Mot de passe incorrect");
                }
            } else {
                System.out.println("[UserService] Utilisateur inactif");
            }
        } else {
            System.out.println("[UserService] Utilisateur non trouvé en BD");
        }

        return null;
    }

    /**
     * Récupérer un utilisateur par email
     */
    public User getUserByEmail(String email) {
        return userDAO.findByEmail(email);
    }

    /**
     * Créer un nouvel utilisateur
     */
    public boolean createUser(User user) {
        try {
            String hashedPassword = BCrypt.hashpw(user.getMotDePasse(), BCrypt.gensalt());
            user.setMotDePasse(hashedPassword);
            userDAO.save(user);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public long countActiveUsers() {
    return userDAO.countActifs();
}


    public java.util.List<User> findAll() {
        return userDAO.findAll();
    }

    public java.util.List<User> filter(String search, String role, Boolean actif) {
        return userDAO.filter(search, role, actif);
    }

    public void update(User user) {
        userDAO.update(user);
    }

    public void create(User user) {
        userDAO.save(user);
    }
}
