package com.innotech.xraymanagerapp.controller;

import com.innotech.xraymanagerapp.model.GeneratorSpeciesBySizeConfig;
import com.innotech.xraymanagerapp.controller.util.JsfUtil;
import com.innotech.xraymanagerapp.controller.util.JsfUtil.PersistAction;
import com.innotech.xraymanagerapp.dto.GeneratorSpeciesBySizeConfigFacade;

import java.io.Serializable;
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

@Named("generatorSpeciesBySizeConfigController")
@SessionScoped
public class GeneratorSpeciesBySizeConfigController implements Serializable {

    @EJB
    private com.innotech.xraymanagerapp.dto.GeneratorSpeciesBySizeConfigFacade ejbFacade;
    private List<GeneratorSpeciesBySizeConfig> items = null;
    private GeneratorSpeciesBySizeConfig selected;

    public GeneratorSpeciesBySizeConfigController() {
    }

    public GeneratorSpeciesBySizeConfig getSelected() {
        return selected;
    }

    public void setSelected(GeneratorSpeciesBySizeConfig selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private GeneratorSpeciesBySizeConfigFacade getFacade() {
        return ejbFacade;
    }

    public void setFacade(com.innotech.xraymanagerapp.dto.GeneratorSpeciesBySizeConfigFacade ejbFacade) {
        this.ejbFacade = ejbFacade;
    }

    public GeneratorSpeciesBySizeConfig prepareCreate() {
        selected = new GeneratorSpeciesBySizeConfig();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("GeneratorSpeciesBySizeConfigCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("GeneratorSpeciesBySizeConfigUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("GeneratorSpeciesBySizeConfigDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<GeneratorSpeciesBySizeConfig> getItems() {
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
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }
    }

    public GeneratorSpeciesBySizeConfig getGeneratorSpeciesBySizeConfig(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<GeneratorSpeciesBySizeConfig> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<GeneratorSpeciesBySizeConfig> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = GeneratorSpeciesBySizeConfig.class)
    public static class GeneratorSpeciesBySizeConfigControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            GeneratorSpeciesBySizeConfigController controller = (GeneratorSpeciesBySizeConfigController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "generatorSpeciesBySizeConfigController");
            return controller.getGeneratorSpeciesBySizeConfig(getKey(value));
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
            if (object instanceof GeneratorSpeciesBySizeConfig) {
                GeneratorSpeciesBySizeConfig o = (GeneratorSpeciesBySizeConfig) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), GeneratorSpeciesBySizeConfig.class.getName()});
                return null;
            }
        }

    }

}
