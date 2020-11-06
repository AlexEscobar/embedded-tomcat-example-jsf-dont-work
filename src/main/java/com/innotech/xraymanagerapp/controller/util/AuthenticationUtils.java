/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.controller.util;

import com.innotech.xraymanagerapp.controller.ConfigurationController;
import com.innotech.xraymanagerapp.controller.LoginController;
import com.innotech.xraymanagerapp.filter.SysFilter;
import com.innotech.xraymanagerapp.model.Users;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author AlexcobarL
 */
public class AuthenticationUtils implements Serializable {

    private static final long serialVersionUID = 1L;

    // general configuration of the system and server
    @ManagedProperty("#{configurationController}")
    private static ConfigurationController configurationController;

    private List<String> urls;

    public AuthenticationUtils() {
    }

    public List<String> getUrls() {
//        if(urls == null)
//            initUrls();
        return urls;
    }
    
    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    /**
     * Validates whether the current server is a cloud viewer or a local x-ray
     * server.
     *
     * @return true if is a x-ray server
     */
    public static boolean isXrayServer() {
        if (configurationController != null) {
            return configurationController.getIsXrayServer();
        }
        return false;
    }

    /**
     * Returns SHA-256 encoded string
     *
     * @param password - the string to be encoded
     * @return SHA-256 encoded string
     * @throws UnsupportedEncodingException if UTF-8 is not supported by the
     * system
     * @throws NoSuchAlgorithmException if SHA-256 is not supported by the
     * system
     */
    public static String encodeSHA256(String password)
            throws UnsupportedEncodingException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(password.getBytes("UTF-8"));
        byte[] digest = md.digest();
        return DatatypeConverter.printBase64Binary(digest);
    }

    /**
     * Purpose: Inserts the logged user in the sessionMap Date: 06/21/2019
     * Author: AlexcobarL
     *
     * @param u: The user to be inserted
     */
    public static void setUserSession(com.innotech.xraymanagerapp.model.Users u) {
        FacesContext cont = FacesContext.getCurrentInstance();
        ExternalContext extCont = cont.getExternalContext();
        extCont.getSessionMap().put("user", u);
    }

    /**
     * Return the logged user
     *
     * @return
     */
    public static Users getLoggedUser() {
        FacesContext cont = FacesContext.getCurrentInstance();
        ExternalContext extCont = cont.getExternalContext();
        Users user = (com.innotech.xraymanagerapp.model.Users) extCont.getSessionMap().get("user");
        if (user == null) {
            return getLoggedUserByLogingSessionBean(cont);
        }
        return user;
//        return null;
    }

    // get the logged user
    public static Users getLoggedUserByLogingSessionBean(FacesContext facesContext) {
        try {

            LoginController lc = getLoginController(facesContext);

            if (lc != null) {
                return lc.getSelected();
            } else {
                Logger.getLogger(SysFilter.class.getName()).log(Level.SEVERE, "There is not logged User..");
                return null;
            }
        } catch (NullPointerException e) {
            Logger.getLogger(SysFilter.class.getName()).log(Level.SEVERE, "There is not logged User... {0}", e.getMessage());
            return null;
        }
    }

    public static LoginController getLoginController(FacesContext facesContext) {
        LoginController lc = (LoginController) facesContext.getApplication().getELResolver().
                getValue(facesContext.getELContext(), null, "loginController");
        return lc;
    }
    
    
    public static LoginController getLoginController() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        LoginController controller = (LoginController) facesContext.getApplication().getELResolver().
                getValue(facesContext.getELContext(), null, "loginController");
        return controller;
    }

}
