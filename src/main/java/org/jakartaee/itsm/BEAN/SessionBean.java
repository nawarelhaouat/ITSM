package org.jakartaee.itsm.BEAN;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import java.io.Serializable;
import java.io.IOException;

@Named("sessionBean")
@SessionScoped
public class SessionBean implements Serializable {
    private static final long serialVersionUID = 1L;

    public String logout() {
        try {
            FacesContext ctx = FacesContext.getCurrentInstance();
            ctx.getExternalContext().invalidateSession();
            String contextPath = ctx.getExternalContext().getRequestContextPath();
            ctx.getExternalContext().redirect(contextPath + "/login.xhtml");
            ctx.responseComplete();
        } catch (IOException e) {
            System.err.println("[SessionBean] Erreur logout: " + e.getMessage());
        }
        return null;
    }
}
