package com.innotech.xraymanagerapp.controller;

import com.innotech.xraymanagerapp.model.Persons;
import com.innotech.xraymanagerapp.controller.util.JsfUtil;
import com.innotech.xraymanagerapp.controller.util.JsfUtil.PersistAction;
import com.innotech.xraymanagerapp.dto.PersonsFacade;
import com.innotech.xraymanagerapp.controller.util.AuthenticationUtils;
import com.innotech.xraymanagerapp.model.PetPatients;
import com.innotech.xraymanagerapp.model.Users;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
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

@Named("personsController")
@SessionScoped
public class PersonsController implements Serializable {

    @EJB
    private com.innotech.xraymanagerapp.dto.PersonsFacade ejbFacade;
    private List<Persons> items = null;
    private Persons selected;
    private boolean isPetOwner = false;

    public PersonsController() {
    }

    public Persons getSelected() {
        return selected;
    }

    public void setSelected(Persons selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private PersonsFacade getFacade() {
        return ejbFacade;
    }

    public Persons prepareCreate() {
        selected = new Persons();
        isPetOwner = false;
        initializeEmbeddableKey();
        return selected;
    }

    public void setPetOwner() {
        if (getPetPatientsFromPatientController() != null) {
            getPetPatientsFromPatientController().setOwnerId(selected);
        }
    }

    /**
     * Sets the selected owner based on the selected pet patient on the worklist
     * page on the petPatient create form.
     */
    public void getSelectedOwnerFromPatientCreate() {
        if (Objects.nonNull(getPetPatientsFromPatientController())) {
            if (Objects.nonNull(getPetPatientsFromPatientController().getOwnerId())) {
                selected = getPetPatientsFromPatientController().getOwnerId();
            }
        }
    }

    private PetPatients getPetPatientsFromPatientController() {
        PetPatientsController petController = (PetPatientsController) JsfUtil.getSessionBean("petPatientsController");
        if (petController != null) {
            return petController.getSelected();
        }
        return null;
    }

    public void initializePetPatient() {
        PetPatientsController petController = (PetPatientsController) JsfUtil.getSessionBean("petPatientsController");
        if (petController != null) {
            petController.prepareCreateOnWorklistLoad();
        }
    }

    public void initializeFromPetRegistrationForm() {
        initializePetPatient();
        prepareCreate();
        isPetOwner = true;
    }

    public void create() {
        selected.setUserId(AuthenticationUtils.getLoggedUser());
        selected.setStatus(true);
        selected.setTypeId(new com.innotech.xraymanagerapp.model.PersonTypes(1));
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("PersonsCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
        if (isPetOwner) {
            setPetOwner();
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("PersonsUpdated"));
         if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
        if (isPetOwner) {
            setPetOwner();
        }
    }

    public void destroy() {
        String action;
        if (selected.getStatus()) {
            selected.setStatus(false);
            action = ResourceBundle.getBundle("/Bundle").getString("PersonsDeleted");
        } else {
            selected.setStatus(true);
            action = ResourceBundle.getBundle("/Bundle").getString("PersonsUpdated");
        }

        persist(PersistAction.UPDATE, action);

        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Persons> getItems() {
        if (items == null) {
            LoginController lc = AuthenticationUtils.getLoginController();
            if (lc != null) {

                if (lc.getSelected() != null) {

                    Integer clinicId = lc.getSelected().getClinicId().getId();
                    if (lc.getSelected().getUsername().equals("root")) {
                        items = getFacade().findAll();
                    } else {
                        items = getFacade().findByStatusAndClinic("Persons.findByStatusAndClinic", "status", clinicId);
                    }
                }
            }
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

    public Persons getPersons(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<Persons> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Persons> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Persons.class)
    public static class PersonsControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            PersonsController controller = (PersonsController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "personsController");
            return controller.getPersons(getKey(value));
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
            if (object instanceof Persons) {
                Persons o = (Persons) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Persons.class.getName()});
                return null;
            }
        }

    }

}
