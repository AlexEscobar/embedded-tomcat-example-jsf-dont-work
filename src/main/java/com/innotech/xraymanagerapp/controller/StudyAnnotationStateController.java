package com.innotech.xraymanagerapp.controller;

import com.innotech.xraymanagerapp.business.StudyAnnotationStateBusinessController;
import com.innotech.xraymanagerapp.controller.util.JsfUtil;
import com.innotech.xraymanagerapp.controller.util.AuthenticationUtils;
import com.innotech.xraymanagerapp.model.Studies;

import java.io.Serializable;
import java.util.Date;
import java.util.ResourceBundle;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;

@Named("studyAnnotationStateController")
@SessionScoped
public class StudyAnnotationStateController extends StudyAnnotationStateBusinessController implements Serializable {

    @Override
    public void preCreate() {
        prepareCreate();
        selected.setState(jsonState);
        selected.setStatus(true);
        selected.setCreationDate(new Date());
        selected.setUserId(AuthenticationUtils.getLoggedUser());
        selected.setStudies(studyId);
        selected.setStudyId(studyId.getId());

    }

    @Override
    public boolean create() {
        System.out.println("In create this guy");
        if (studyId == null) {
            studyId = getCurrentStudyByCurrentPatient();
        }
        if (studyId != null) {
            preCreate();
            System.out.println("To persist the state of the study id..." + selected.getStudyId());
            persist(JsfUtil.PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("StudyAnnotationStateCreated"));
            if (!JsfUtil.isValidationFailed()) {
                items = null;    // Invalidate list of items to trigger re-query.
                return true;
            }
        }
        return false;
    }

    /**
     * gets today's study of a patient on patient creation.
     *
     * @return
     */
    public Studies getCurrentStudyByCurrentPatient() {
        StudiesController petC = (StudiesController) JsfUtil.getSessionScopeBean("studiesController");
        if (petC != null) {
            return petC.getSelected();
        }
        return null;
    }

}
