package com.innotech.xraymanagerapp.controller;

import com.innotech.xraymanagerapp.business.ConfigurationBusinessController;
import com.innotech.xraymanagerapp.model.Studies;
import com.innotech.xraymanagerapp.controller.util.JsfUtil;
import com.innotech.xraymanagerapp.controller.util.constants.ConfigurationProperties;
import com.innotech.xraymanagerapp.dto.StudiesFacade;
import com.innotech.xraymanagerapp.model.Users;

import java.io.Serializable;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@Named("studiesController")
@SessionScoped
public class StudiesController extends com.innotech.xraymanagerapp.business.StudiesBusinessController implements Serializable {

    @EJB
    private com.innotech.xraymanagerapp.dto.StudiesFacade ejbFacade;
    @EJB
    private ConfigurationBusinessController ejbConfigurationController;  
    
    @PostConstruct
    public void init(){
        serverUrl = ejbConfigurationController.getConfigurationMap().get(ConfigurationProperties.APP_SERVER_NAME);
        setLoggedUser(getLoggedUser());
        setSelectedPatient(getSelectedPatient());
    }
    
    @Override
    public void setSelectedPatient(Integer patientId) {
        this.patientId = patientId;
    }
    
    @Override
    public Integer getSelectedPatient() {
        PetPatientsController petController = (PetPatientsController) JsfUtil.getSessionBean("petPatientsController");
        if (petController != null) {
            if (petController.getSelected() != null) {
                if (getSelected() != null) {
                    getSelected().setPatientId(petController.getSelected());
                    patientId = petController.getSelected().getId();
                }
                return petController.getSelected().getId();
            }
        }
        return null;
    }

    @Override
    public void persist(JsfUtil.PersistAction persistAction, String successMessage) {
        if (getSelected() != null) {
            try {
                if (persistAction != JsfUtil.PersistAction.DELETE) {
                    setSelected(getFacade().edit(getSelected()));
                } else {
                    getFacade().remove(getSelected());
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

    public void setSelectedPetPatient() {
        PetPatientsController petC = (PetPatientsController) JsfUtil.getSessionBean("petPatientsController");
        if (petC != null) {
            petC.setSelected(getSelected().getPatientId());
            petC.setNullItemsByOwner();
        }
    }
    /**
     * This function is called from PetPatients List.xhtml view to find the
     * studies that belongs only to the selected patient
     */
    public void getItemsByPet() {
        setItems(null);
        setFindByPetId(true);
//        PetPatientsController petController = (PetPatientsController) JsfUtil.getSessionBean("petPatientsController");
//        if(petController != null){
//            petController.setNullItemsByOwner();
//        }
        // reset the annotation view to load new changes on it when the search boton is used from annotations controller
        FacesContext.getCurrentInstance().getViewRoot().getViewMap().remove("annotationsController");
    }
    
    @Override
    public Users getLoggedUser() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        LoginController controller = (LoginController) facesContext.getApplication().getELResolver().
                getValue(facesContext.getELContext(), null, "loginController");        
        return controller.getSelected();
    }
    
    @Override
    public void setLoggedUser(Users user) {
        this.loggedUser = user;
    }
    
    
    @Override
    public StudiesFacade getEjbFacade() {
        return ejbFacade;
    }

    @Override
    public ConfigurationBusinessController getEjbConfigurationController() {
        return ejbConfigurationController;
    }
    
    @FacesConverter(forClass = Studies.class)
    public static class StudiesControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            StudiesController controller = (StudiesController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "studiesController");
            return controller.getStudies(getKey(value));
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
            if (object instanceof Studies) {
                Studies o = (Studies) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Studies.class.getName()});
                return null;
            }
        }

    }

}
