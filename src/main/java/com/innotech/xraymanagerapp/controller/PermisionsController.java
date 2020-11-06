package com.innotech.xraymanagerapp.controller;

import com.innotech.xraymanagerapp.model.Permisions;
import com.innotech.xraymanagerapp.controller.util.JsfUtil;
import com.innotech.xraymanagerapp.controller.util.JsfUtil.PersistAction;
import com.innotech.xraymanagerapp.dto.PermisionsFacade;
import com.innotech.xraymanagerapp.model.Users;

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
import javax.faces.view.ViewScoped;

@Named("permisionsController")
@ViewScoped
public class PermisionsController implements Serializable {

    @EJB
    private com.innotech.xraymanagerapp.dto.PermisionsFacade ejbFacade;
    private List<Permisions> items = null;
    private Permisions selected;

    public PermisionsController() {
    }

    public Permisions getSelected() {
        return selected;
    }

    public void setSelected(Permisions selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private PermisionsFacade getFacade() {
        return ejbFacade;
    }

    public Permisions prepareCreate() {
        selected = new Permisions();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        //selected.setUsers(JsfUtil.getLoggedUser());
        selected.setEntryDate(new Date());
        selected.setStatus(true);
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("PermisionsCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("PermisionsUpdated"));
    }

    public void destroy() {
        String action;
        if (selected.getStatus()) {
            selected.setStatus(false);
            action = ResourceBundle.getBundle("/Bundle").getString("PermisionsDeleted");
        } else {
            selected.setStatus(true);
            action = ResourceBundle.getBundle("/Bundle").getString("PermisionsUpdated");
        }

        persist(PersistAction.UPDATE, action);

        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Permisions> getItems() {
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

    public Permisions getPermisions(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<Permisions> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Permisions> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    /**
     * Uses the current selected user to get its profile in order to get allowed
     * permissions to the user.
     *
     * @return permission list
     */
    public List<Permisions> getItemsAvailableBySelectedUser() {
        UsersController userC = (UsersController) JsfUtil.getSessionBean("usersController");
        if (userC != null) {
            if (userC.getSelected() != null) {
                Users u = userC.getSelected();
                if (u != null) {
                    return getFacade().getPermissionsByUser(u.getId());
                }
            }
        }
        return null;
    }
     @FacesConverter(forClass = Permisions.class)
    public static class PermisionsControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            PermisionsController controller = (PermisionsController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "permisionsController");
            return controller.getPermisions(getKey(value));
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
            if (object instanceof Permisions) {
                Permisions o = (Permisions) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Permisions.class.getName()});
                return null;
            }
        }

    }
}
