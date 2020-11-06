package com.innotech.xraymanagerapp.controller;

import com.innotech.xraymanagerapp.model.PermisionProfile;
import com.innotech.xraymanagerapp.controller.util.JsfUtil;
import com.innotech.xraymanagerapp.controller.util.JsfUtil.PersistAction;
import com.innotech.xraymanagerapp.dto.PermisionProfileFacade;
import com.innotech.xraymanagerapp.model.Permisions;
import com.innotech.xraymanagerapp.model.Profiles;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.view.ViewScoped;

@Named("permisionProfileController")
@ViewScoped
public class PermisionProfileController implements Serializable {

    @EJB
    private com.innotech.xraymanagerapp.dto.PermisionProfileFacade ejbFacade;
    private List<PermisionProfile> items = null;
    private PermisionProfile selected;
    private List<Permisions> selectedPermissions;
    private Profiles profiles;

    public PermisionProfileController() {
    }

    public PermisionProfile getSelected() {
        return selected;
    }

    public void setSelected(PermisionProfile selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
        selected.getPermisionProfilePK().setProfileId(selected.getProfiles().getId());
        selected.getPermisionProfilePK().setPermisionId(selected.getPermisions().getId());
    }

    protected void initializeEmbeddableKey() {
        selected.setPermisionProfilePK(new com.innotech.xraymanagerapp.model.PermisionProfilePK());
    }

    private PermisionProfileFacade getFacade() {
        return ejbFacade;
    }

    public PermisionProfile prepareCreate() {
        selected = new PermisionProfile();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        //selected.setUsers(JsfUtil.getLoggedUser());
        selected.setEntryDate(new Date());
        selected.setStatus(true);
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("PermisionProfileCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
            selectedPermissions = null;
        }
    }

    public void addPermissionsToUserProfile() {
        profiles = selected.getProfiles();
        if (selectedPermissions != null) {
            for (Permisions selectedPermission : selectedPermissions) {
                prepareCreate();
                selected.setPermisions(selectedPermission);
                selected.setProfiles(profiles);
                create();
            }
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("PermisionProfileUpdated"));
    }

    public void destroy() {
        String action;
        if (selected.getStatus()) {
            selected.setStatus(false);
            action = ResourceBundle.getBundle("/Bundle").getString("PermisionProfileDeleted");
        } else {
            selected.setStatus(true);
            action = ResourceBundle.getBundle("/Bundle").getString("PermisionProfileUpdated");
        }

        persist(PersistAction.DELETE, action);

        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<PermisionProfile> getItems() {
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

    public PermisionProfile getPermisionProfile(com.innotech.xraymanagerapp.model.PermisionProfilePK id) {
        return getFacade().find(id);
    }

    public List<PermisionProfile> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<PermisionProfile> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    public List<Permisions> getSelectedPermissions() {
        return selectedPermissions;
    }

    public void setSelectedPermissions(List<Permisions> selectedPermissions) {
        this.selectedPermissions = selectedPermissions;
    }

    @FacesConverter(forClass = PermisionProfile.class)
    public static class PermisionProfileControllerConverter implements Converter {

        private static final String SEPARATOR = "#";
        private static final String SEPARATOR_ESCAPED = "\\#";

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            PermisionProfileController controller = (PermisionProfileController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "permisionProfileController");
            return controller.getPermisionProfile(getKey(value));
        }

        com.innotech.xraymanagerapp.model.PermisionProfilePK getKey(String value) {
            com.innotech.xraymanagerapp.model.PermisionProfilePK key;
            String values[] = value.split(SEPARATOR_ESCAPED);
            key = new com.innotech.xraymanagerapp.model.PermisionProfilePK();
            key.setProfileId(Integer.parseInt(values[0]));
            key.setPermisionId(Integer.parseInt(values[1]));
            return key;
        }

        String getStringKey(com.innotech.xraymanagerapp.model.PermisionProfilePK value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value.getProfileId());
            sb.append(SEPARATOR);
            sb.append(value.getPermisionId());
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof PermisionProfile) {
                PermisionProfile o = (PermisionProfile) object;
                return getStringKey(o.getPermisionProfilePK());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), PermisionProfile.class.getName()});
                return null;
            }
        }

    }

}
