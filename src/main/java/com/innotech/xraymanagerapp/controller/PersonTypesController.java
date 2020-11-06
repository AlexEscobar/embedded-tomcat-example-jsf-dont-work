package com.innotech.xraymanagerapp.controller;

import com.innotech.xraymanagerapp.model.PersonTypes;
import com.innotech.xraymanagerapp.controller.util.JsfUtil;
import com.innotech.xraymanagerapp.controller.util.JsfUtil.PersistAction;
import com.innotech.xraymanagerapp.dto.PersonTypesFacade;

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

@Named("personTypesController")
@ViewScoped
public class PersonTypesController implements Serializable {

    @EJB
    private com.innotech.xraymanagerapp.dto.PersonTypesFacade ejbFacade;
    private List<PersonTypes> items = null;
    private PersonTypes selected;
    private boolean isEdit = false;

    public PersonTypesController() {
    }

    public void edit() {
        isEdit = true;
    }

    public PersonTypes getSelected() {
        return selected;
    }

    public void setSelected(PersonTypes selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private PersonTypesFacade getFacade() {
        return ejbFacade;
    }

    public PersonTypes prepareCreate() {
        selected = new PersonTypes();
        initializeEmbeddableKey();
        isEdit = false;
        return selected;
    }

    public void create() {
        //selected.setUsers(JsfUtil.getLoggedUser());
        if (isEdit) {
            update();
            isEdit = false;
        } else {
            selected.setStatus(true);
            persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("PersonTypesCreated"));
            if (!JsfUtil.isValidationFailed()) {
                items = null;    // Invalidate list of items to trigger re-query.
            }
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("PersonTypesUpdated"));
    }

    public void destroy() {
        String action;
        if (selected.getStatus()) {
            selected.setStatus(false);
            action = ResourceBundle.getBundle("/Bundle").getString("PersonTypesDeleted");
        } else {
            selected.setStatus(true);
            action = ResourceBundle.getBundle("/Bundle").getString("PersonTypesUpdated");
        }

        persist(PersistAction.UPDATE, action);

        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<PersonTypes> getItems() {
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

    public PersonTypes getPersonTypes(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<PersonTypes> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<PersonTypes> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = PersonTypes.class)
    public static class PersonTypesControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            PersonTypesController controller = (PersonTypesController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "personTypesController");
            return controller.getPersonTypes(getKey(value));
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
            if (object instanceof PersonTypes) {
                PersonTypes o = (PersonTypes) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), PersonTypes.class.getName()});
                return null;
            }
        }

    }

}