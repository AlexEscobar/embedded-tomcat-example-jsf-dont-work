package com.innotech.xraymanagerapp.controller;

import com.innotech.xraymanagerapp.model.UserProfile;
import com.innotech.xraymanagerapp.controller.util.JsfUtil;
import com.innotech.xraymanagerapp.controller.util.JsfUtil.PersistAction;
import com.innotech.xraymanagerapp.dto.UserProfileFacade;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@Named("userProfileController")
@SessionScoped
public class UserProfileController implements Serializable {

    @EJB
    private com.innotech.xraymanagerapp.dto.UserProfileFacade ejbFacade;
    private List<UserProfile> items = null;
    private UserProfile selected;

    public UserProfileController() {
    }

    public UserProfile getSelected() {
        return selected;
    }

    public void setSelected(UserProfile selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
        selected.getUserProfilePK().setUserId(selected.getUsers().getId());
        selected.getUserProfilePK().setProfileId(selected.getProfiles().getId());
    }

    protected void initializeEmbeddableKey() {
        selected.setUserProfilePK(new com.innotech.xraymanagerapp.model.UserProfilePK());
    }

    private UserProfileFacade getFacade() {
        return ejbFacade;
    }

    public UserProfile prepareCreate() {
        selected = new UserProfile();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        //selected.setUsers(JsfUtil.getLoggedUser());
        selected.setStatus(true);
        selected.setEntryDate(new Date());
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("UserProfileCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("UserProfileUpdated"));
    }

    public void destroy() {
        String action;
        if (selected.getStatus()) {
            selected.setStatus(false);
            action = ResourceBundle.getBundle("/Bundle").getString("UserProfileDeleted");
        } else {
            selected.setStatus(true);
            action = ResourceBundle.getBundle("/Bundle").getString("UserProfileUpdated");
        }

        persist(PersistAction.DELETE, action);

        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<UserProfile> getItems() {
        if (items == null) {
            items = getFacade().findAll();
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
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }
    }

    public UserProfile getUserProfile(com.innotech.xraymanagerapp.model.UserProfilePK id) {
        return getFacade().find(id);
    }

    public List<UserProfile> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<UserProfile> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = UserProfile.class)
    public static class UserProfileControllerConverter implements Converter {

        private static final String SEPARATOR = "#";
        private static final String SEPARATOR_ESCAPED = "\\#";

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            UserProfileController controller = (UserProfileController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "userProfileController");
            return controller.getUserProfile(getKey(value));
        }

        com.innotech.xraymanagerapp.model.UserProfilePK getKey(String value) {
            com.innotech.xraymanagerapp.model.UserProfilePK key;
            String values[] = value.split(SEPARATOR_ESCAPED);
            key = new com.innotech.xraymanagerapp.model.UserProfilePK();
            key.setUserId(Integer.parseInt(values[0]));
            key.setProfileId(Integer.parseInt(values[1]));
            return key;
        }

        String getStringKey(com.innotech.xraymanagerapp.model.UserProfilePK value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value.getUserId());
            sb.append(SEPARATOR);
            sb.append(value.getProfileId());
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof UserProfile) {
                UserProfile o = (UserProfile) object;
                return getStringKey(o.getUserProfilePK());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), UserProfile.class.getName()});
                return null;
            }
        }

    }

}
