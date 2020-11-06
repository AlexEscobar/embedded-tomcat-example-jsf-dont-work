/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.business;

import com.innotech.xraymanagerapp.controller.StudiesController;
import com.innotech.xraymanagerapp.controller.StudyAnnotationStateController;
import com.innotech.xraymanagerapp.controller.util.AuthenticationUtils;
import com.innotech.xraymanagerapp.controller.util.JsfUtil;
import com.innotech.xraymanagerapp.dto.StudyAnnotationStateFacade;
import com.innotech.xraymanagerapp.model.Studies;
import com.innotech.xraymanagerapp.model.StudyAnnotationState;
import com.innotech.xraymanagerapp.model.Users;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author Alexander Escobar L.
 */
@Stateless
public class StudyAnnotationStateBusinessController {

    @EJB
    private com.innotech.xraymanagerapp.dto.StudyAnnotationStateFacade ejbFacade;
    protected List<StudyAnnotationState> items = null;
    protected StudyAnnotationState selected;
    protected String jsonState;
    protected String viewportState;
    protected Studies studyId;
    protected Users userId;

    public StudyAnnotationStateBusinessController() {
    }

    public StudyAnnotationState getSelected() {
        return selected;
    }

    public void setSelected(StudyAnnotationState selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private StudyAnnotationStateFacade getFacade() {
        return ejbFacade;
    }

    public StudyAnnotationState prepareCreate() {
        selected = new StudyAnnotationState();
        initializeEmbeddableKey();
        return selected;
    }

    public void preCreate() {
        prepareCreate();
        selected.setState(jsonState);
        selected.setViewportAnnotations(viewportState);
        selected.setStatus(true);
        selected.setCreationDate(new Date());
        selected.setUserId(userId);
        selected.setStudies(studyId);
        selected.setStudyId(studyId.getId());

    }

    public boolean create() {
        System.out.println("In create this guy");

        if (studyId != null) {
            preCreate();
            System.out.println("To persist the state of the study id..." + selected.getStudyId());
            
            if (persist(JsfUtil.PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("StudyAnnotationStateCreated"))) {
                items = null;    // Invalidate list of items to trigger re-query.
                return true;
            }
        }
        return false;
    }

    public void update() {
        persist(JsfUtil.PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("StudyAnnotationStateUpdated"));
    }

    public void destroy() {
        persist(JsfUtil.PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("StudyAnnotationStateDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<StudyAnnotationState> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    public StudyAnnotationState find(int id) {
        return getFacade().find(id);
    }

    protected boolean persist(JsfUtil.PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != JsfUtil.PersistAction.DELETE) {
                    getFacade().edit(selected);
                } else {
                    getFacade().remove(selected);
                }
                return true;
//                JsfUtil.addSuccessMessage(successMessage);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
//                    JsfUtil.addErrorMessage(msg);
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, msg);
                } else {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
//                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
//                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }
        return false;
    }

    public StudyAnnotationState getStudyAnnotationState(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<StudyAnnotationState> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<StudyAnnotationState> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    public String getJsonState() {
        return jsonState;
    }

    public void setJsonState(String jsonState) {
        this.jsonState = jsonState;
    }

    public Studies getStudyId() {
        return studyId;
    }

    public void setStudyId(Studies studyId) {
        this.studyId = studyId;
    }

    public void setUserId(Users userId) {
        this.userId = userId;
    }

    public String getViewportState() {
        return viewportState;
    }

    public void setViewportState(String viewportState) {
        this.viewportState = viewportState;
    }

    @FacesConverter(forClass = StudyAnnotationState.class)
    public static class StudyAnnotationStateControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            StudyAnnotationStateController controller = (StudyAnnotationStateController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "studyAnnotationStateController");
            return controller.getStudyAnnotationState(getKey(value));
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
            if (object instanceof StudyAnnotationState) {
                StudyAnnotationState o = (StudyAnnotationState) object;
                return getStringKey(o.getStudyId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), StudyAnnotationState.class.getName()});
                return null;
            }
        }

    }
}
