package com.innotech.xraymanagerapp.controller;

import com.innotech.xraymanagerapp.model.Clinics;
import com.innotech.xraymanagerapp.controller.util.JsfUtil;
import com.innotech.xraymanagerapp.controller.util.JsfUtil.PersistAction;
import com.innotech.xraymanagerapp.dto.ClinicsFacade;
import com.innotech.xraymanagerapp.controller.util.AuthenticationUtils;
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

@Named("clinicsController")
@SessionScoped
public class ClinicsController implements Serializable {

    @EJB
    private com.innotech.xraymanagerapp.dto.ClinicsFacade ejbFacade;
    private List<Clinics> items = null;
    private Clinics selected;

    public ClinicsController() {
    }

    // get the logged user
    public Users getLoggedUser() {
        FacesContext cont = FacesContext.getCurrentInstance();
        return AuthenticationUtils.getLoggedUserByLogingSessionBean(cont);
    }

    public Clinics getSelected() {
        return selected;
    }

    public void setSelected(Clinics selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private ClinicsFacade getFacade() {
        return ejbFacade;
    }

    public Clinics prepareCreate() {
        selected = new Clinics();
        selected.setClinicType(true);// default is 1 = true = veterinary clinic. The other option is 0 = false = human clinic
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        //selected.setUsers(JsfUtil.getLoggedUser());
        selected.setEntryDate(new Date());
        selected.setStatus(Boolean.TRUE);
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("ClinicsCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("ClinicsUpdated"));
    }

    public void destroy() {
        String action;
        if (selected.getStatus()) {
            selected.setStatus(false);
            action = ResourceBundle.getBundle("/Bundle").getString("ClinicsDeleted");
        } else {
            selected.setStatus(true);
            action = ResourceBundle.getBundle("/Bundle").getString("ClinicsUpdated");
        }

        persist(PersistAction.UPDATE, action);

        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Clinics> getItems() {
        if (items == null) {
            Users user = AuthenticationUtils.getLoggedUser();
            if(user != null)
                if(user.getUsername().equals("root"))
                    items = getFacade().findAll();
                else 
                    items = getFacade().findClinicByUserId(user.getId());
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

    public Clinics getClinics(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<Clinics> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Clinics> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    public List<Clinics> getItemsAvailableByUser() {
        try {
            Users user = getLoggedUser();
            if (user != null) {
                if (user.getUsername().equals("root")) {
                    // if the user is root user, retrieve the whole clinics
                    return getFacade().findAll();
                } else {
                    return getFacade().findClinicByUserId(user.getId());
                }
            }
        } catch (NullPointerException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @FacesConverter(forClass = Clinics.class)
    public static class ClinicsControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ClinicsController controller = (ClinicsController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "clinicsController");
            return controller.getClinics(getKey(value));
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
            if (object instanceof Clinics) {
                Clinics o = (Clinics) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Clinics.class.getName()});
                return null;
            }
        }

    }

}
