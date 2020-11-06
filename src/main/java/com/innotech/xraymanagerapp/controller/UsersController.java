package com.innotech.xraymanagerapp.controller;

import com.innotech.xraymanagerapp.model.Users;
import com.innotech.xraymanagerapp.controller.util.JsfUtil;
import com.innotech.xraymanagerapp.controller.util.JsfUtil.PersistAction;
import com.innotech.xraymanagerapp.dto.UsersFacade;
import com.innotech.xraymanagerapp.controller.util.AuthenticationUtils;
import com.innotech.xraymanagerapp.model.PermisionProfile;
import com.innotech.xraymanagerapp.model.Permisions;
import com.innotech.xraymanagerapp.model.Profiles;
import com.innotech.xraymanagerapp.model.UserProfile;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

public class UsersController implements Serializable {

    @EJB
    private com.innotech.xraymanagerapp.dto.UsersFacade ejbFacade;
    private List<Users> items = null;
    private Users selected;
//    private List<PermisionProfile> allPermissions = null;
    private boolean isEdit = false;
    private String userRole = "user";

    public UsersController() {
    }

    public Users getLoggedUser() {
        return AuthenticationUtils.getLoggedUser();
    }

    public void edit() {
        isEdit = true;
    }

    public Users getSelected() {
        return selected;
    }

    public void setSelected(Users selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private UsersFacade getFacade() {
        return ejbFacade;
    }

    public Users prepareCreate() {
        selected = new Users();
        isEdit = false;
        initializeEmbeddableKey();
        return selected;
    }

    public boolean validateUser() {
        if (getFacade().getUserByUserName(selected.getUsername().toLowerCase().trim()) == null) {
            return true;
        }
        JsfUtil.addErrorMessage("User (" + selected.getUsername() + ") already exists.");
        return false;
    }

    public void addProfile() {
        List<UserProfile> userProfileList = new ArrayList();
        switch (userRole) {
            case "user": {
                userProfileList.add(new UserProfile(selected.getId(), 9));
                selected.setUserProfileList(userProfileList);
                break;
            }
            case "veterinarian": {
                // User = 9, vet = 3, 
                userProfileList.add(new UserProfile(selected.getId(), 9));
                userProfileList.add(new UserProfile(selected.getId(), 3));
                selected.setUserProfileList(userProfileList);
                break;
            }
            case "admin": {
                // User = 9, vet = 3, administrator = 5
                userProfileList.add(new UserProfile(selected.getId(), 9));
                userProfileList.add(new UserProfile(selected.getId(), 3));
                userProfileList.add(new UserProfile(selected.getId(), 5));
                selected.setUserProfileList(userProfileList);
                break;
            }
            case "dealer": {
                Profiles p = new Profiles(4);// dealer
                p.setStatus(true);
                p.setEntryDate(new Date());
                userProfileList.add(new UserProfile(selected.getId(), p.getId()));
                selected.setUserProfileList(userProfileList);
                break;
            }
            default:
                break;
        }
    }

    public void create() {
        if (isEdit) {
            selected.setStatus(true);
            update();
            isEdit = false;
        } else {
            if (validateUser()) {

                try {
                    //selected.setUsers(JsfUtil.getLoggedUser());
                    selected.setUsername(selected.getUsername().toLowerCase().trim());
                    Date now = new Date();
                    Calendar c = Calendar.getInstance();
                    c.setTime(now);
                    selected.setSignupDate(now);
                    selected.setStatus(Boolean.TRUE);
                    selected.setLastDate(now);
                    selected.setAttempts((short) 0);
                    selected.setlPageId(new Permisions(8));// Landing page worklist by default

//            Sets three times SHA256 encription for the typed password
                    selected.setPassword(AuthenticationUtils.encodeSHA256(AuthenticationUtils.encodeSHA256(AuthenticationUtils.encodeSHA256(selected.getPassword()))));

//            If there is not option for the current user to select a clinic, 
//            then the new user will be under the same clinic as the user who is creating it.
                    if (selected.getClinicId() == null) {
                        selected.setClinicId(AuthenticationUtils.getLoggedUser().getClinicId());
                    }
                } catch (UnsupportedEncodingException | NoSuchAlgorithmException ex) {
                    Logger.getLogger(UsersController.class.getName()).log(Level.SEVERE, null, ex);
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("OnCreateUserError"));
                }
                persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("UsersCreated"));
                if (!JsfUtil.isValidationFailed()) {
                    items = null;    // Invalidate list of items to trigger re-query.
                    addProfile();
                    update();
                }
            }
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("UsersUpdated"));
    }

    public void destroy() {
        String action;
        if (selected.getStatus()) {
            selected.setStatus(false);
            action = ResourceBundle.getBundle("/Bundle").getString("UsersDeleted");
        } else {
            selected.setStatus(true);
            action = ResourceBundle.getBundle("/Bundle").getString("UsersUpdated");
        }

        persist(PersistAction.UPDATE, action);

        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Users> getItems() {
        if (items == null) {
            Users loggedUser = getLoggedUser();
            FacesContext cont = FacesContext.getCurrentInstance();
            LoginController lc = AuthenticationUtils.getLoginController(cont);
            try {
                if (loggedUser != null) {
                    if (loggedUser.getUsername().equals("root")) {
                        items = getFacade().findAll();
                        // permission 25 is clinic administration which alows the current user to administrate the other users of the clinic
                    } else if (lc.hasPermission(25)) {
                        items = getFacade().findUsersByClinicAdmin(loggedUser.getClinicId().getId());
                    }
                }
            } catch (NullPointerException e) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage());
            }
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    selected = getFacade().edit(selected);
                } else {
                    getFacade().remove(selected);
                }
                JsfUtil.addSuccessMessage(successMessage);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, ex.getMessage());
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }
    }

    public Users getUsers(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<Users> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Users> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    public void resetPassword() {
        if (selected != null) {
            selected.setPassword(null);
        }
    }

    public void changePassword() {
        try {
            if (selected != null) {
                selected.setPassword(AuthenticationUtils.encodeSHA256(selected.getPassword()));
                selected.setPassword(AuthenticationUtils.encodeSHA256(selected.getPassword()));
                selected.setPassword(AuthenticationUtils.encodeSHA256(selected.getPassword()));
            }
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
        }
        persist(PersistAction.UPDATE, "Password Successfully Updated");
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
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
//    public boolean hasPermission(int requestedPermission) {
//        if (permissionList == null) {
//            permissionList = new ArrayList<>();
//            selected = AuthenticationUtils.getLoggedUser();
//            if (selected != null) {
//                setPermissionList();
//            }
//        }
//        return checkPermissions(requestedPermission);
//    }
    /**
     * Iterates the permission list
     *
     * @param requestedPermission
     * @return True if requestedPermission exists on the list. False otherwise.
     */
//    public boolean checkPermissions(int requestedPermission) {
//        for (Integer permisionId : permissionList) {
//            if (requestedPermission == permisionId) {
//                return true;
//            }
//        }
//        return false;
//    }
    /**
     * Inserts into the permission lists all permissions the current user has to
     * allow it to get access to different views
     */
//    public void setPermissionList() {
////        List<UserProfile> userProfileList = getFacade().getUserByUserName(selected.getUsername().toLowerCase().trim()).getUserProfileList();
//        List<UserProfile> userProfileList = selected.getUserProfileList();
//        List<String> urls = new ArrayList();
//        for (UserProfile userProfile : userProfileList) {
//            for (PermisionProfile permisionProfile : userProfile.getProfiles().getPermisionProfileList()) {
//                urls.add(permisionProfile.getPermisions().getAccessName());     //adds the allowed urls for a user to the urls list
//                permissionList.add(permisionProfile.getPermisions().getId());
//            }
//        }
//    }
//
//    public boolean getPermissions(int requestedPermission) {
//        selected = AuthenticationUtils.getLoggedUser();
//        if (selected != null) {
//            List<UserProfile> userProfileList = getFacade().getUserByUserName(selected.getUsername().toLowerCase().trim()).getUserProfileList();
//            for (UserProfile userProfile : userProfileList) {
//                allPermissions = userProfile.getProfiles().getPermisionProfileList();
//                for (PermisionProfile permisionProfile : allPermissions) {
//                    if (permisionProfile.getPermisions().getId() == requestedPermission) {
//                        return true;
//                    }
//                }
//            }
//        }
//        // if don't find any permission
//        return false;
//    }
    //****************** User authentication methods end ***********************//
    public boolean isIsEdit() {
        return isEdit;
    }

    public void setIsEdit(boolean isEdit) {
        this.isEdit = isEdit;
    }

    public com.innotech.xraymanagerapp.dto.UsersFacade getEjbFacade() {
        return ejbFacade;
    }

    public void setEjbFacade(com.innotech.xraymanagerapp.dto.UsersFacade ejbFacade) {
        this.ejbFacade = ejbFacade;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    @FacesConverter(forClass = Users.class)
    public static class UsersControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            UsersController controller = (UsersController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "usersController");
            return controller.getUsers(getKey(value));
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Users) {
                Users o = (Users) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Users.class.getName()});
                return null;
            }
        }

    }

}
