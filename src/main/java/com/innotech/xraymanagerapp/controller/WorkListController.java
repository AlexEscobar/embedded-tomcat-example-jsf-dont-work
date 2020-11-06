package com.innotech.xraymanagerapp.controller;

import com.innotech.xraymanagerapp.model.WorkList;
import com.innotech.xraymanagerapp.controller.util.JsfUtil;
import com.innotech.xraymanagerapp.controller.util.JsfUtil.PersistAction;
import com.innotech.xraymanagerapp.dto.WorkListFacade;
import com.innotech.xraymanagerapp.controller.util.AuthenticationUtils;
import com.innotech.xraymanagerapp.controller.util.SensorFileRunner;
import com.innotech.xraymanagerapp.model.Users;
import java.io.IOException;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

public class WorkListController implements Serializable, Observer {

    @EJB
    private com.innotech.xraymanagerapp.dto.WorkListFacade ejbFacade;
    private List<WorkList> items = null;
    private WorkList selected;
    private Date initialDate;
    private Date finalDate;
    private boolean isSelectAll;
    private String todaysDate;
    private String selectedWorklistDate;

    public WorkListController() {
    }

    public void initializeVariables() {
        selected = null;
        initialDate = null;
        finalDate = null;
        todaysDate = JsfUtil.getStringDate(new Date(), "yyyyMMdd");
    }

    public void doubleClickRedirection() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("/x-ray");
        } catch (IOException ex) {
            Logger.getLogger(WorkListController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setItemsNull() {
        items = null;
        isSelectAll = false;
    }

    public void getTodaysWorklist() {
        initialDate = JsfUtil.zoneDateTimeToDate(JsfUtil.getTodaysLocalDateTime());
        finalDate = JsfUtil.zoneDateTimeToDate(JsfUtil.getDaysAfter(1));
    }

    public void getAllStudies() {
        setItemsNull();
        initializeVariables();
        isSelectAll = true;
    }

    public WorkList getSelected() {
        return selected;
    }

    public void setSelected(WorkList selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private WorkListFacade getFacade() {
        return ejbFacade;
    }

    public WorkList prepareCreate() {
        selected = new WorkList();
        initializeEmbeddableKey();
        return selected;
    }

    public void setSelectedPetPatient() {
        PetPatientsController petC = (PetPatientsController) JsfUtil.getSessionBean("petPatientsController");
        if (petC != null) {
            petC.setNullItemsByOwner();
            petC.setSelected(selected.getPatientId());
        }
        setSelectedStudy();
    }

    public void setSelectedStudy() {
        StudiesController petC = (StudiesController) JsfUtil.getSessionScopeBean("studiesController");
        if (petC != null) {
            petC.setSelected(petC.getFacade().find(selected.getStudyId()));
        }
    }

    public void create() {
        //selected.setUsers(JsfUtil.getLoggedUser());
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("WorkListCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public WorkList createOnPatientCreation(WorkList workList, WorkListFacade facade) {
        selected = workList;
        ejbFacade = facade;
        //selected.setUsers(JsfUtil.getLoggedUser());
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("WorkListCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
        return selected;
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("WorkListUpdated"));
    }

    public void destroy() {
        String action;
        if (selected.getStatus()) {
            selected.setStatus(false);
            action = ResourceBundle.getBundle("/Bundle").getString("WorkListDeleted");
        } else {
            selected.setStatus(true);
            action = ResourceBundle.getBundle("/Bundle").getString("WorkListUpdated");
        }

        persist(PersistAction.UPDATE, action);

        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<WorkList> getItems() {
        if (items == null) {
            Users loggedUser = AuthenticationUtils.getLoggedUserByLogingSessionBean(FacesContext.getCurrentInstance());
            if (isSelectAll) {
                if (loggedUser.getId() == 1) {
                    return items = getFacade().findAll();
                }
                return items = getFacade().findByStatusAndClinic("WorkList.findByClinicId", "clinicId", loggedUser.getClinicId().getId());
            } else {
                getTodaysWorklist();
                items = getFacade().findByDateRangeAndClinicId(initialDate, finalDate, loggedUser.getClinicId().getId());
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

    public WorkList getWorkList(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<WorkList> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<WorkList> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    public Date getInitialDate() {
        return initialDate;
    }

    public void setInitialDate(Date initialDate) {
        this.initialDate = initialDate;
    }

    public Date getFinalDate() {
        return finalDate;
    }

    public void setFinalDate(Date finalDate) {
        this.finalDate = finalDate;
    }

    @Override
    public void update(Observable o, Object arg) {
        System.out.println("observer has observed that the observable just changed: " + arg);
        if ((int) arg == 13) {
            JsfUtil.addErrorMessage("Error", "There is not annotation setted, must select an annotation before you can shoot an x-ray");
        }
    }

    public String getTodaysDate() {
        return todaysDate;
    }

    public void setTodaysDate(String todaysDate) {
        this.todaysDate = todaysDate;
    }

    public String getSelectedWorklistDate() {
        if (selected != null) {

            selectedWorklistDate = JsfUtil.formatDateWithPattern(selected.getCreationDate(), "yyyyMMdd");
        }
        return selectedWorklistDate;
    }

    public void setSelectedWorklistDate(String selectedWorklistDate) {
        this.selectedWorklistDate = selectedWorklistDate;
    }

    @FacesConverter(forClass = WorkList.class)
    public static class WorkListControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            WorkListController controller = (WorkListController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "workListController");
            return controller.getWorkList(getKey(value));
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
            if (object instanceof WorkList) {
                WorkList o = (WorkList) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), WorkList.class.getName()});
                return null;
            }
        }

    }

}
