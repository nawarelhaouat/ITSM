package org.jakartaee.itsm.BEAN;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.inject.Inject;
import jakarta.annotation.PostConstruct;
import jakarta.faces.context.FacesContext;
import org.jakartaee.itsm.MODEL.User;
import org.jakartaee.itsm.SERVICE.UserService;
import java.io.Serializable;
import java.io.IOException;

@Named("loginBean")
@SessionScoped
public class LoginBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private String email;
    private String password;
    private String errorMessage;
    
    @Inject
    private UserService userService;
    private User user;
    public LoginBean() {
        this.errorMessage = null;
    }
    
    @PostConstruct
    public void init() {
        // Service is injected by CDI container
    }
    public User getUser() {
        return user;
    }
    public String login() {
        // Validation des champs
        if (email == null || email.trim().isEmpty()) {
            errorMessage = "Veuillez entrer votre email";
            return null;
        }

        if (password == null || password.trim().isEmpty()) {
            errorMessage = "Veuillez entrer votre mot de passe";
            return null;
        }

        try {
            System.out.println("[LoginBean] Tentative de connexion avec email: " + email);
            // Vérifier l'utilisateur dans la base de données
            this.user = userService.authenticateUser(email, password);
            System.out.println("[LoginBean] Résultat authentification: " + (user != null ? "SUCCÈS" : "ÉCHEC"));

            if (user != null) {
                // Authentification réussie
                System.out.println("[LoginBean] Utilisateur trouvé: " + user.getEmail() + ", Rôle: " + user.getRole());
                errorMessage = "";
                
                // Déterminer l'URL de redirection selon le rôle
                User.Role role = user.getRole();
                String redirectUrl = null;
                
                if (role != null) {
                    switch (role) {
                        case ADMIN:
                            redirectUrl = "admin/dashboard.xhtml";
                            break;
                        case TECHNICIEN:
                            redirectUrl = "technicien/dashboard.xhtml";
                            break;
                        case MANAGER:
                            redirectUrl = "manager/dashboard.xhtml";
                            break;
                        case UTILISATEUR:
                            redirectUrl = "user/dashboard.xhtml";
                            break;
                        default:
                            redirectUrl = "user/dashboard.xhtml";
                    }
                } else {
                    System.out.println("[LoginBean] ATTENTION: Rôle est NULL");
                    redirectUrl = "user/dashboard.xhtml";
                }
                
                System.out.println("[LoginBean] Redirection vers: " + redirectUrl);
                
                // Effectuer la redirection HTTP
                try {
                    FacesContext context = FacesContext.getCurrentInstance();
                    String redirectPath = context.getExternalContext().getRequestContextPath() + "/" + redirectUrl;
                    context.getExternalContext().redirect(redirectPath);
                    context.responseComplete();
                } catch (IOException e) {
                    System.out.println("[LoginBean] Erreur during redirect: " + e.getMessage());
                    e.printStackTrace();
                }
                
                return null;
            } else {
                // Identifiants invalides
                System.out.println("[LoginBean] Authentification échouée pour: " + email);
                errorMessage = "Email ou mot de passe incorrect";
                return null;
            }
        } catch (Exception e) {
            System.out.println("[LoginBean] Exception: " + e.getMessage());
            errorMessage = "Erreur lors de la connexion : " + e.getMessage();
            e.printStackTrace();
            return null;
        }
    }

    // Getters et Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
