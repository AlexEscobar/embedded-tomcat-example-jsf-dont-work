/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.business;

import com.innotech.xraymanagerapp.controller.UsersController;
import com.innotech.xraymanagerapp.controller.util.AuthenticationUtils;
import com.innotech.xraymanagerapp.controller.util.CloseApp;
import com.innotech.xraymanagerapp.controller.util.JsfUtil;
import com.innotech.xraymanagerapp.model.PermisionProfile;
import com.innotech.xraymanagerapp.model.UserProfile;
import com.innotech.xraymanagerapp.model.Users;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

/**
 *
 * @author Alexander Escobar L.
 */
public abstract class LoginBusinessController {

    @EJB
    private com.innotech.xraymanagerapp.dto.UsersFacade ejbFacade;
    @EJB
    private com.innotech.xraymanagerapp.dto.ProfilesFacade ejbProfilesFacade;

    private final short maxLogingAttemps = 3;

    private final AuthenticationUtils au;
    private Users selected;
    private List<Integer> permissionList = null;// permission list of the logged user
    private boolean clinicType = true;// the clinic type could be Veterinary = TRUE OR Human = FALSE

    public LoginBusinessController() {
        au = new AuthenticationUtils();
    }

    public void setSelected() {
        selected = new Users();
    }

    /**
     * Updates the logging attempts and the last date the user logged in into
     * the system
     *
     * @param logingAttemps
     */
    private void updateUser(short logingAttemps) {
        selected.setAttempts(logingAttemps);
        selected.setLastDate(new java.util.Date());
        if (logingAttemps >= maxLogingAttemps) // if the user inserts the password wrong three times or more, the system will inactivate the user
        {
            selected.setStatus(false);
        }

        getFacade().edit(selected);
    }

    /*
        @purpose: compares system users with typed data in login page.
            If the typed user exists and the password is the same, this function will redirect user to its landing page
            depending on the user profile. 
            Otherwise will show an appropiate message.
        @date: 6/21/2019
        @author: AlexcobarL    
     */
    public void login() {
        try {
//         searches for the typed user on database
            String typedPass = selected.getPassword();
            try {
                selected = getFacade().getUserByUserName(selected.getUsername().toLowerCase().trim());
                if (selected != null) {
                    try {
//                Cyphers the typed password to compare it with the password on the database
                        String pass = AuthenticationUtils.encodeSHA256(typedPass);
                        pass = AuthenticationUtils.encodeSHA256(pass);
                        pass = AuthenticationUtils.encodeSHA256(pass);
                        if (pass.equals(selected.getPassword())) {
                            try {
//                      If the typed user is found  inserts it in the session map
                                selected.setToken(AuthenticationToken.createToken(selected.getUsername(), pass));
                                selected.setTokenDate(new java.util.Date());
                                selected.setTokenExpDate(AuthenticationToken.getTokenExpirationDate());
                                AuthenticationUtils.setUserSession(selected);//this should be on the session bean 
                                updateUser((short) 0);
                                defineCilnicType();

//                        Redirects it to the landing page 
                                selected.getlPageId().getAccessName();
                                setPermissionList();
                                String landingPage = selected.getlPageId().getAccessName();
                                System.out.println("Logged User:" + AuthenticationUtils.getLoggedUser());
                                boolean isXrayServer = JsfUtil.isXrayLocalServer();
                                if (!isXrayServer) {
                                    landingPage = "studies";
                                }
                                if (landingPage == null) {
                                    landingPage = "worklist";
                                }
//                                landingPage = "worklist";

//                                Logger.getLogger(UsersController.class.getName()).log(Level.INFO, "The User {0} of the Clinic {1}, has been successfully logged.",new Object[]{selected.getUsername(),selected.getClinicId().getName()});
                                JsfUtil.redirect("/" + landingPage);
                            } catch (IOException e) {
                                Logger.getLogger(UsersController.class.getName()).log(Level.SEVERE, e.getMessage());
                                System.out.println("Error: filter.JsfUtil.redirect cannot redirect user ");
                            }

                        } else {
                            Short oneMore = (short) (selected.getAttempts() + 1);
                            String attepmsLeft = "";

                            if ((maxLogingAttemps - oneMore) == 2) {
                                attepmsLeft = "TWO";
                            }
                            if ((maxLogingAttemps - oneMore) == 1) {
                                attepmsLeft = "ONE";
                            }
                            if ((maxLogingAttemps - oneMore) == 0) {
                                attepmsLeft = "ZERO";
                            }

                            updateUser(oneMore);
                            selected = null;
                            logout();
                            JsfUtil.addErrorMessage(
                                    ResourceBundle.getBundle("/Bundle").getString("FailLogin"),
                                    ResourceBundle.getBundle("/Bundle").getString("PassNotFound"));

                            JsfUtil.addErrorMessage(
                                    ResourceBundle.getBundle("/Bundle").getString("WrongPassword"),
                                    new StringBuilder(attepmsLeft).append(" (").append(maxLogingAttemps - oneMore).append(") Login attempts left").toString()
                            );

                            if (maxLogingAttemps - oneMore == 0) {
                                JsfUtil.addErrorMessage(
                                        ResourceBundle.getBundle("/Bundle").getString("ContactSupportHeader"),
                                        ResourceBundle.getBundle("/Bundle").getString("ContactSupportMessage")
                                );
                            }
                        }
                    } catch (UnsupportedEncodingException | NoSuchAlgorithmException ex) {
                        logout();
                        Logger.getLogger(UsersController.class.getName()).log(Level.SEVERE, ex.getMessage());
                        JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("ErrorLogin"));
                    }

                } else {
                    logout();
                    JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("FailLogin"), ResourceBundle.getBundle("/Bundle").getString("UserNotFound"));
                    setSelected();
                }
            } catch (NoResultException | NonUniqueResultException e) {
                logout();
                Logger.getLogger(UsersController.class.getName()).log(Level.SEVERE, e.getMessage());
//                JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("FailLogin"), ResourceBundle.getBundle("/Bundle").getString("UserNotFound"));
                setSelected();
            }
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage());
        }
    }
    //****************** User authentication methods start ***********************//

    /**
     * @purpose Consults the user profiles to get the profile permissions. If
     * the requested permission exists then the user is allowed to access the
     * required view or component
     * @param requestedPermission
     * @return True if requestedPermission exists on the permission list. False
     * otherwise.
     */
    public boolean hasPermission(int requestedPermission) {
        if (permissionList == null) {
            permissionList = new ArrayList<>();
//            selected = AuthenticationUtils.getLoggedUser();
//            selected = AuthenticationUtils.getLoggedUserByLogingSessionBean(FacesContext.getCurrentInstance());
            if (selected != null) {
                setPermissionList();
            }
        }
        return checkPermissions(requestedPermission);
    }

    /**
     * Iterates the permission list
     *
     * @param requestedPermission
     * @return True if requestedPermission exists on the list. False otherwise.
     */
    public boolean checkPermissions(int requestedPermission) {
        for (Integer permisionId : permissionList) {
            if (requestedPermission == permisionId) {
                return true;
            }
        }
        return false;
    }

    /**
     * Inserts into the permission lists all permissions the current user has to
     * allow it get access to different views. This url's are taken from the
     * permissions table related to the user profile, and used by the
     * com.innotech.xraymanagerapp.filter.SysFylter class to allow the logged
     * user to get access only to them.
     *
     */
    public void setPermissionList() {
//        List<UserProfile> userProfileList = getFacade().getUserByUserName(selected.getUsername().toLowerCase().trim()).getUserProfileList();
        List<UserProfile> userProfileList = selected.getUserProfileList();
        List<String> urls = new ArrayList();
        for (UserProfile userProfile : userProfileList) {// ALL THE PROFILES OF AN USER
            for (PermisionProfile permisionProfile : userProfile.getProfiles().getPermisionProfileList()) {// ALL THE PERMISSIONS OF A PROFILE
                if (permisionProfile.getPermisions().getAccessName() != null) {
                    urls.add(permisionProfile.getPermisions().getAccessName());     //adds the allowed urls of the user to the urls list
                }
                if (permissionList != null) {
                    permissionList.add(permisionProfile.getPermisions().getId());
                }
            }
        }
        au.setUrls(urls);
    }

    //****************** User authentication methods end ***********************//
    /**
     * @puporse drops the whole session beans
     *
     * @return login.xhml url to be redirected there.
     */
    public String logout() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        getFacade().resetCache();
        return "pretty:login";
    }

    public void closeApp() {
        try {
            CloseApp.closeApp();
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, ex.getMessage());
        }
    }

    public Users getSelected() {
        return selected;
    }

    private com.innotech.xraymanagerapp.dto.UsersFacade getFacade() {
        return ejbFacade;
    }

    private com.innotech.xraymanagerapp.dto.ProfilesFacade getProfilesFacade() {
        return ejbProfilesFacade;
    }

    public AuthenticationUtils getAu() {
        return au;
    }

    public AnnotationsDentalXrayController getAnnotationsController() {
        AnnotationsDentalXrayController ac = (AnnotationsDentalXrayController) JsfUtil.getSessionScopeBean("annotationsDentalXrayController");
        return ac;
    }

    private void defineCilnicType() {
        try {

            if (selected != null) {
                clinicType = selected.getClinicId().getClinicType();
            }
        } catch (NullPointerException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Clinic type error - LoginController.defineClinicType() - {0}", e.getMessage());
        }
    }

    public boolean isClinicType() {
        return clinicType;
    }

    public void setClinicType(boolean clinicType) {
        this.clinicType = clinicType;
    }
}
