package com.innotech.xraymanagerapp.controller;

import com.innotech.xraymanagerapp.model.Species;
import com.innotech.xraymanagerapp.controller.util.JsfUtil;
import com.innotech.xraymanagerapp.controller.util.JsfUtil.PersistAction;
import com.innotech.xraymanagerapp.dto.SpeciesFacade;

import java.io.Serializable;
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

@Named("speciesController")
@ViewScoped
public class SpeciesController implements Serializable {

    @EJB
    private com.innotech.xraymanagerapp.dto.SpeciesFacade ejbFacade;
    private List<Species> items = null;
    private Species selected;

    public SpeciesController() {
    }

    public Species getSelected() {
        return selected;
    }

    public void setSelected(Species selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private SpeciesFacade getFacade() {
        return ejbFacade;
    }

    public Species prepareCreate() {
        selected = new Species();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        //selected.setUsers(JsfUtil.getLoggedUser());
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("SpeciesCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("SpeciesUpdated"));
    }

    public void destroy() {
        String action;
        if (selected.getStatus()) {
            selected.setStatus(false);
            action = ResourceBundle.getBundle("/Bundle").getString("SpeciesDeleted");
        } else {
            selected.setStatus(true);
            action = ResourceBundle.getBundle("/Bundle").getString("SpeciesUpdated");
        }

        persist(PersistAction.UPDATE, action);

        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Species> getItems() {
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

    public Species getSpecies(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<Species> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Species> getItemsAvailableSelectOne() {
        return getFacade().findByStatus("Species.findByStatus", "status");
    }

    @FacesConverter(forClass = Species.class)
    public static class SpeciesControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            SpeciesController controller = (SpeciesController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "speciesController");
            return controller.getSpecies(getKey(value));
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
            if (object instanceof Species) {
                Species o = (Species) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Species.class.getName()});
                return null;
            }
        }

    }

}
