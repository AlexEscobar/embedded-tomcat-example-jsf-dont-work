package com.innotech.xraymanagerapp.controller;

import com.innotech.xraymanagerapp.model.Profiles;
import com.innotech.xraymanagerapp.controller.util.JsfUtil;
import com.innotech.xraymanagerapp.controller.util.JsfUtil.PersistAction;
import com.innotech.xraymanagerapp.dto.ProfilesFacade;
import com.innotech.xraymanagerapp.controller.util.AuthenticationUtils;

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

@Named("profilesController")
@SessionScoped
public class ProfilesController implements Serializable {

    @EJB
    private com.innotech.xraymanagerapp.dto.ProfilesFacade ejbFacade;
    private List<Profiles> items = null;
    private Profiles selected;

    public ProfilesController() {
    }

    public Profiles getSelected() {
        return selected;
    }

    public void setSelected(Profiles selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private ProfilesFacade getFacade() {
        return ejbFacade;
    }

    public Profiles prepareCreate() {
        selected = new Profiles();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        selected.setEntryDate(new Date());
        selected.setStatus(true);
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("ProfilesCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("ProfilesUpdated"));
    }

    public void destroy() {
        String action;
        if (selected.getStatus()) {
            selected.setStatus(false);
            action = ResourceBundle.getBundle("/Bundle").getString("ProfilesDeleted");
        } else {
            selected.setStatus(true);
            action = ResourceBundle.getBundle("/Bundle").getString("ProfilesUpdated");
        }

        persist(PersistAction.UPDATE, action);

        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Profiles> getItems() {
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

    public Profiles getProfiles(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<Profiles> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Profiles> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }
    
    @FacesConverter(forClass = Profiles.class)
    public static class ProfilesControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ProfilesController controller = (ProfilesController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "profilesController");
            return controller.getProfiles(getKey(value));
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
            if (object instanceof Profiles) {
                Profiles o = (Profiles) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Profiles.class.getName()});
                return null;
            }
        }

    }
}
