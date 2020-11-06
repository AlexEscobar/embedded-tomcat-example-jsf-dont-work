package com.innotech.xraymanagerapp.controller;

import com.innotech.xraymanagerapp.model.TeethNumbers;
import com.innotech.xraymanagerapp.controller.util.JsfUtil;
import com.innotech.xraymanagerapp.controller.util.JsfUtil.PersistAction;
import com.innotech.xraymanagerapp.dto.TeethNumbersFacade;

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

@Named("teethNumbersController")
@SessionScoped
public class TeethNumbersController implements Serializable {

    @EJB
    private com.innotech.xraymanagerapp.dto.TeethNumbersFacade ejbFacade;
    private List<TeethNumbers> items = null;
    private TeethNumbers selected;

    public TeethNumbersController() {
    }

    public TeethNumbers getSelected() {
        return selected;
    }

    public void setSelected(TeethNumbers selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private TeethNumbersFacade getFacade() {
        return ejbFacade;
    }

    public TeethNumbers prepareCreate() {
        selected = new TeethNumbers();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        //selected.setUsers(JsfUtil.getLoggedUser());
        selected.setStatus(true);
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("TeethNumbersCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("TeethNumbersUpdated"));
    }

    public void destroy() {
        String action;
        if (selected.getStatus()) {
            selected.setStatus(false);
            action = ResourceBundle.getBundle("/Bundle").getString("TeethNumbersDeleted");
        } else {
            selected.setStatus(true);
            action = ResourceBundle.getBundle("/Bundle").getString("TeethNumbersUpdated");
        }

        persist(PersistAction.UPDATE, action);

        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<TeethNumbers> getItems() {
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
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, ex.getMessage());
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }
    }

    public TeethNumbers getTeethNumbers(String id) {
        return getFacade().find(id);
    }

    public List<TeethNumbers> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<TeethNumbers> getItemsAvailableSelectOne() {
        return getBySelectedStudy();
    }

    private List<TeethNumbers> getBySelectedStudy() {
        try {            
            StudiesController ppc = (StudiesController) JsfUtil.getSessionScopeBean("studiesController");
            if (ppc != null) {
                if (ppc.getSelected() != null) {
                    return getFacade().findBySpecies(ppc.getSelected().getPatientId().getSpecieId().getId());
                }
            }
        } catch (NullPointerException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage());
        }
        return null;
    }

    @FacesConverter(forClass = TeethNumbers.class)
    public static class TeethNumbersControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            TeethNumbersController controller = (TeethNumbersController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "teethNumbersController");
            return controller.getTeethNumbers(getKey(value));
        }

        String getKey(String value) {
            String key;
            if (value.contains("|")) {
                value = value.substring(0, value.indexOf("|")).trim();
            }
            key = value;
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
//            if (object instanceof TeethNumbers) {
//                TeethNumbers o = (TeethNumbers) object;
//                return getStringKey(String.valueOf(o.getId()));
//            } else {
//                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), TeethNumbers.class.getName()});
//                return null;
//            }
            return null;
        }

    }

}
