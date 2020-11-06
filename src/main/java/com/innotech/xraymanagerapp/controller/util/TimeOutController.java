/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.controller.util;

import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;

/**
 *
 * @author Alexander Escobar L.
 */
@Named("timeOutController")
@RequestScoped
public class TimeOutController implements Serializable {

    private boolean isRedirect;

    @EJB
    private com.innotech.xraymanagerapp.dto.UsersFacade ejbFacade;

    public void onIdle() {
        try {
            isRedirect = true;
            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
            logout(externalContext);
            JsfUtil.addErrorMessage("Session Expired...");
        } catch (NullPointerException ex) {
            Logger.getLogger(TimeOutController.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
    }

    public void onActive() {
        isRedirect = false;
        JsfUtil.addSuccessMessage("Welcome Back!");
    }

    public void logout(ExternalContext ec) {
        try {
            ec.invalidateSession();
            ejbFacade.resetCache();
            ec.redirect("/login");
        } catch (IOException ex) {
            Logger.getLogger(TimeOutController.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
    }
}
