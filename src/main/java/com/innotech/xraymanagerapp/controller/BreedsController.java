package com.innotech.xraymanagerapp.controller;

import com.innotech.xraymanagerapp.model.Breeds;
import com.innotech.xraymanagerapp.controller.util.JsfUtil;
import com.innotech.xraymanagerapp.controller.util.JsfUtil.PersistAction;
import com.innotech.xraymanagerapp.dto.BreedsFacade;
import com.innotech.xraymanagerapp.controller.util.AuthenticationUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedProperty;
import javax.inject.Named;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.view.ViewScoped;

@Named("breedsController")
@ViewScoped
public class BreedsController implements Serializable {

    @EJB
    private com.innotech.xraymanagerapp.dto.BreedsFacade ejbFacade;
    private List<Breeds> items = null;
    private Breeds selected;

    public BreedsController() {
    }

    public Breeds getSelected() {
        return selected;
    }

    public void setSelected(Breeds selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private BreedsFacade getFacade() {
        return ejbFacade;
    }

    public Breeds prepareCreate() {
        selected = new Breeds();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        selected.setUserId(AuthenticationUtils.getLoggedUser());
        selected.setStatus(Boolean.TRUE);
        selected.setEntryDate(new Date());
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("BreedsCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("BreedsUpdated"));
    }

    public void destroy() {
        String action;
        if (selected.getStatus()) {
            selected.setStatus(false);
            action = ResourceBundle.getBundle("/Bundle").getString("BreedsDeleted");
        } else {
            selected.setStatus(true);
            action = ResourceBundle.getBundle("/Bundle").getString("BreedsUpdated");
        }

        persist(PersistAction.UPDATE, action);

        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Breeds> getItems() {
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

    public Breeds getBreeds(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<Breeds> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Breeds> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

//    @ManagedProperty(value="#{petPatientsController}")
//    PetPatientsController patientC; 
    public List<Breeds> getItemsBySpecie() {
        PetPatientsController patientC = (PetPatientsController)JsfUtil.getSessionBean("petPatientsController");
        if(patientC != null){
            if(patientC.getSelected() != null)
                if(patientC.getSelected().getSpecieId() != null)
                    if(patientC.getSelected().getSpecieId().getId() > 0)
                        return getFacade().getBreedsBySpecie(patientC.getSelected().getSpecieId().getId());
        }
        return null;
    }

    @FacesConverter(forClass = Breeds.class)
    public static class BreedsControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            BreedsController controller = (BreedsController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "breedsController");
            return controller.getBreeds(getKey(value));
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
            if (object instanceof Breeds) {
                Breeds o = (Breeds) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Breeds.class.getName()});
                return null;
            }
        }

    }

}
