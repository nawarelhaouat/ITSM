package org.jakartaee.itsm.BEAN;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.jakartaee.itsm.MODEL.User;
import org.jakartaee.itsm.SERVICE.UserService;

import java.io.Serializable;

@Named
@ViewScoped
public class UserBean implements Serializable {

    @Inject
    private UserService userService;

    @Inject
    private LoginBean loginBean;

    public String updateProfile() {
        User user = loginBean.getUser();

        userService.update(user);

        return "/user/dashboard?faces-redirect=true";
    }
}