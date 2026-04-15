package org.jakartaee.itsm.BEAN;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

import org.jakartaee.itsm.MODEL.User;
import org.jakartaee.itsm.SERVICE.UserService;

@Named("userAdminBean")
@ViewScoped
public class UserAdminBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private UserService userService;

    // =========================
    // 📊 DATA
    // =========================
    private List<User> users;
    private int totalUsers;

    private int currentPage = 1;
    private int pageSize = 10;
    private int totalPages;

    // =========================
    // 🔍 FILTRES
    // =========================
    private String searchTerm;
    private String filterRole;
    private Boolean filterActif;

    // =========================
    // 🧾 FORM
    // =========================
    private User selectedUser;
    private String plainPassword;

    private boolean showDialog = false;

    // =========================
    // INIT
    // =========================
    @PostConstruct
    public void init() {
        loadUsers();
    }

    // =========================
    // 🔁 LOAD
    // =========================
    public void loadUsers() {
        users = userService.findAll(); // simple pour commencer

        totalUsers = users.size();
        totalPages = (int) Math.ceil((double) totalUsers / pageSize);
    }

    // =========================
    // 🔍 FILTER
    // =========================
    public void applyFilters() {
        users = userService.filter(searchTerm, filterRole, filterActif);

        totalUsers = users.size();
        currentPage = 1;
        totalPages = (int) Math.ceil((double) totalUsers / pageSize);
    }

    // =========================
    // 📄 PAGINATION
    // =========================
    public void nextPage() {
        if (currentPage < totalPages) {
            currentPage++;
        }
    }

    public void prevPage() {
        if (currentPage > 1) {
            currentPage--;
        }
    }

    // =========================
    // ✏ EDIT / CREATE
    // =========================
    public void openCreateDialog() {
        selectedUser = new User();
        showDialog = true;
    }

    public void editUser(User user) {
        this.selectedUser = user;
        showDialog = true;
    }

    public void closeDialog() {
        selectedUser = null;
        showDialog = false;
    }

    // =========================
    // 💾 SAVE
    // =========================
    public void saveUser() {
        try {
            if (selectedUser.getId() == null) {
                // création
                selectedUser.setMotDePasse(plainPassword);
                selectedUser.setActif(true);
                userService.create(selectedUser);
            } else {
                // update
                userService.update(selectedUser);
            }

            loadUsers();
            closeDialog();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================
    // 🔄 ACTIF / INACTIF
    // =========================
    public void toggleActif(User user) {
        user.setActif(!user.isActif());
        userService.update(user);
        loadUsers();
    }

    // =========================
    // GETTERS
    // =========================

    public List<User> getUsers() {
        return users;
    }

    public int getTotalUsers() {
        return totalUsers;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public String getFilterRole() {
        return filterRole;
    }

    public void setFilterRole(String filterRole) {
        this.filterRole = filterRole;
    }

    public Boolean getFilterActif() {
        return filterActif;
    }

    public void setFilterActif(Boolean filterActif) {
        this.filterActif = filterActif;
    }

    public User getSelectedUser() {
        return selectedUser;
    }

    public String getPlainPassword() {
        return plainPassword;
    }

    public void setPlainPassword(String plainPassword) {
        this.plainPassword = plainPassword;
    }

    public boolean isShowDialog() {
        return showDialog;
    }
}