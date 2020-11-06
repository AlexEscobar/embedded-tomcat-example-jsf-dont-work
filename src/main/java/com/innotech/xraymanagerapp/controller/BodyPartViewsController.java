package com.innotech.xraymanagerapp.controller;

import com.innotech.xraymanagerapp.model.BodyPartViews;
import com.innotech.xraymanagerapp.controller.util.JsfUtil;
import com.innotech.xraymanagerapp.controller.util.JsfUtil.PersistAction;
import com.innotech.xraymanagerapp.dto.BodyPartViewsFacade;

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

@Named("bodyPartViewsController")
@SessionScoped
public class BodyPartViewsController implements Serializable {

    @EJB
    private com.innotech.xraymanagerapp.dto.BodyPartViewsFacade ejbFacade;
    private List<BodyPartViews> items = null;
    private BodyPartViews selected;

    public BodyPartViewsController() {
    }

    public BodyPartViews getSelected() {
        return selected;
    }

    public void setSelected(BodyPartViews selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private BodyPartViewsFacade getFacade() {
        return ejbFacade;
    }

    public BodyPartViews prepareCreate() {
        selected = new BodyPartViews();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        selected.setEntryDate(new Date());
        selected.setStatus(true);
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/BundleBodyParts").getString("BodyPartViewsCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/BundleBodyParts").getString("BodyPartViewsUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/BundleBodyParts").getString("BodyPartViewsDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<BodyPartViews> getItems() {
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

    public BodyPartViews getBodyPartViews(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<BodyPartViews> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<BodyPartViews> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = BodyPartViews.class)
    public static class BodyPartViewsControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            BodyPartViewsController controller = (BodyPartViewsController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "bodyPartViewsController");
            return controller.getBodyPartViews(getKey(value));
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
            if (object instanceof BodyPartViews) {
                BodyPartViews o = (BodyPartViews) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), BodyPartViews.class.getName()});
                return null;
            }
        }

    }

}
