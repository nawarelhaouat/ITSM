package org.jakartaee.itsm.MODEL;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "users")
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "mot_de_passe",nullable = false, length = 255)
    private String motDePasse;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(nullable = false, length = 100)
    private String prenom;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Role role;

    @Column(length = 100)
    private String departement;

    @Column(nullable = false)
    private boolean actif = true;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_creation",nullable = false, updatable = false)
    private Date dateCreation = new Date();

    // Constructeurs
    public User() {
    }

    public User(Long id, String email, String motDePasse, String nom, String prenom, Role role, 
                String departement, boolean actif, Date dateCreation) {
        this.id = id;
        this.email = email;
        this.motDePasse = motDePasse;
        this.nom = nom;
        this.prenom = prenom;
        this.role = role;
        this.departement = departement;
        this.actif = actif;
        this.dateCreation = dateCreation;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getDepartement() {
        return departement;
    }

    public void setDepartement(String departement) {
        this.departement = departement;
    }

    public Date getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", role='" + role + '\'' +
                ", departement='" + departement + '\'' +
                ", actif=" + actif +
                ", dateCreation=" + dateCreation +
                '}';
    }

    // Enum pour les rôles
    public enum Role {
        UTILISATEUR, TECHNICIEN, MANAGER, ADMIN
    }

    public String getInitials() {
        String i = "";

        if (prenom != null && !prenom.isEmpty()) {
            i += prenom.substring(0, 1).toUpperCase();
        }

        if (nom != null && !nom.isEmpty()) {
            i += nom.substring(0, 1).toUpperCase();
        }

        return i;
    }
}
