package com.innotech.xraymanagerapp.controller;

import com.innotech.xraymanagerapp.model.BodyParts;
import com.innotech.xraymanagerapp.controller.util.JsfUtil;
import com.innotech.xraymanagerapp.controller.util.JsfUtil.PersistAction;
import com.innotech.xraymanagerapp.dto.BodyPartsFacade;

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

@Named("bodyPartsController")
@SessionScoped
public class BodyPartsController implements Serializable {

    @EJB
    private com.innotech.xraymanagerapp.dto.BodyPartsFacade ejbFacade;
    private List<BodyParts> items = null;
    private BodyParts selected;

    public BodyPartsController() {
    }

    public BodyParts getSelected() {
        return selected;
    }

    public void setSelected(BodyParts selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private BodyPartsFacade getFacade() {
        return ejbFacade;
    }

    public BodyParts prepareCreate() {
        selected = new BodyParts();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        selected.setEntryDate(new Date());
        selected.setStatus(true);
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/BundleBodyParts").getString("BodyPartsCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/BundleBodyParts").getString("BodyPartsUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/BundleBodyParts").getString("BodyPartsDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<BodyParts> getItems() {
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
                    getFacade().edit(selected);
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
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/BundleBodyParts").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/BundleBodyParts").getString("PersistenceErrorOccured"));
            }
        }
    }

    public BodyParts getBodyParts(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<BodyParts> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<BodyParts> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = BodyParts.class)
    public static class BodyPartsControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            BodyPartsController controller = (BodyPartsController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "bodyPartsController");
            return controller.getBodyParts(getKey(value));
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
            if (object instanceof BodyParts) {
                BodyParts o = (BodyParts) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), BodyParts.class.getName()});
                return null;
            }
        }

    }

}
