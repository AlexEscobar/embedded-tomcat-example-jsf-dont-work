/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.business;

import com.innotech.xraymanagerapp.controller.LoginController;
import com.innotech.xraymanagerapp.controller.PetPatientsController;
import com.innotech.xraymanagerapp.controller.StudiesController;
import com.innotech.xraymanagerapp.controller.util.AuthenticationUtils;
import com.innotech.xraymanagerapp.controller.util.JsfUtil;
import com.innotech.xraymanagerapp.controller.util.constants.ConfigurationProperties;
import com.innotech.xraymanagerapp.dto.StudiesFacade;
import com.innotech.xraymanagerapp.model.Studies;
import com.innotech.xraymanagerapp.model.Users;
import com.innotech.xraymanagerapp.model.dicom.DicomUtils;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import org.primefaces.PrimeFaces;

/**
 *
 * @author Alexander Escobar L.
 */
public abstract class StudiesBusinessController {

    public abstract com.innotech.xraymanagerapp.dto.StudiesFacade getEjbFacade();

    public abstract ConfigurationBusinessController getEjbConfigurationController();

    public abstract Integer getSelectedPatient();

    public abstract void setSelectedPatient(Integer patientId);

    public abstract Users getLoggedUser();

    public abstract void setLoggedUser(Users user);

    private List<Studies> items = null;
    private Studies selected;
    private Date initialDate;
    private Date finalDate;
    private boolean findByPetId;
    private boolean findAll;
    private String studyDate;
    private String todaysDate;
    protected String serverUrl;
    protected Users loggedUser;
    protected Integer patientId;

    public StudiesBusinessController() {
//        getTodayStudies();
        todaysDate = JsfUtil.getStringDate(new Date(), "yyyyMMdd");
    }

    public Studies getSelected() {
        return selected;
    }

    public void setSelected(Studies selected) {
        this.selected = selected;
    }

    public StudiesFacade getFacade() {
        return getEjbFacade();
    }

    public Studies prepareCreate() {
        selected = new Studies();
        getSelectedPatient();
        return selected;
    }

    public void create() {
        selected.setUserId(loggedUser);
        selected.setCreationDate(new Date());
        selected.setStatus(true);
        selected.setStudyInstanceUID(DicomUtils.getStudyInstanceUID() + JsfUtil.formatDateWithPattern(new Date(), "yyyyMMddHHmmss"));
        persist(JsfUtil.PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("StudiesCreated"));
        items = null;    // Invalidate list of items to trigger re-query.
    }

    public Studies createOnPatientCreation(Studies study) {
//        getEjbFacade() = facade;
        selected = study;
        selected.setStudyInstanceUID(DicomUtils.getStudyInstanceUID() + JsfUtil.formatDateWithPattern(new Date(), "yyyyMMddHHmmss"));
        persist(JsfUtil.PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("StudiesCreated"));
        items = null;    // Invalidate list of items to trigger re-query.
        return selected;
    }

    public void update() {
        persist(JsfUtil.PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("StudiesUpdated"));
    }

    public void destroy() {
        String action;
        if (selected.getStatus()) {
            selected.setStatus(false);
            action = ResourceBundle.getBundle("/Bundle").getString("StudiesDeleted");
        } else {
            selected.setStatus(true);
            action = ResourceBundle.getBundle("/Bundle").getString("StudiesUpdated");
        }

        persist(JsfUtil.PersistAction.UPDATE, action);

        selected = null; // Remove selection
        items = null;    // Invalidate list of items to trigger re-query.
    }

    public void setItemsNull() {
        items = null;
    }

    public void getTodayStudies() {
        initialDate = JsfUtil.zoneDateTimeToDate(JsfUtil.getTodaysLocalDateTime());
        finalDate = JsfUtil.zoneDateTimeToDate(JsfUtil.getDaysAfter(1));
        resetAllFilters();
    }

    public void getTodaysStudies() {
        initialDate = JsfUtil.zoneDateTimeToDate(JsfUtil.getTodaysLocalDateTime());
        finalDate = JsfUtil.zoneDateTimeToDate(JsfUtil.getDaysAfter(1));
        resetAllFilters();
    }

    public void getYesterdayStudies() {
        initialDate = JsfUtil.zoneDateTimeToDate(JsfUtil.getBeforeDays(1L));
        finalDate = JsfUtil.zoneDateTimeToDate(JsfUtil.getTodaysLocalDateTime());
        resetAllFilters();
    }

    public void getWeekStudies() {
        initialDate = JsfUtil.zoneDateTimeToDate(JsfUtil.getBeforeDays(8L));
        finalDate = JsfUtil.zoneDateTimeToDate(JsfUtil.getDaysAfter(1));
        resetAllFilters();
    }

    public void getMonthStudies() {
        initialDate = JsfUtil.zoneDateTimeToDate(JsfUtil.getBeforeDays(30L));
        finalDate = JsfUtil.zoneDateTimeToDate(JsfUtil.getDaysAfter(1));
        resetAllFilters();
    }

    public void getAllStudies() {
        resetAllFilters();
        findAll = true;
    }

    public void resetAllFilters() {
        findAll = false;
        findByPetId = false;
        items = null;//triggers the call for the new items
    }

    public List<Studies> getItems() {
        if (items == null) {

            boolean isXrayServer = JsfUtil.isXrayLocalServer();
            String namedQuery = "Studies.findByStatusAndClient";
            if (isXrayServer) {
                namedQuery = "Studies.findByStatusAndClientLocal";
            }
            if (findByPetId) {
                items = getFacade().findByOwnerId(getSelectedPatient());
                findByPetId = false;
            } else {
                if (loggedUser != null) {
                    Integer clinicId = loggedUser.getClinicId().getId();
                    if (findAll) {
                        if (loggedUser.getUsername().equals("root")) {
                            items = getFacade().findAll();
                        } else {
                            items = getFacade().findByStatusAndClinic(namedQuery, "status", clinicId);
                        }
                    } else {
                        if (loggedUser.getUsername().equals("root")) {
                            if (initialDate == null) {
                                items = getFacade().findByDateRange(JsfUtil.zoneDateTimeToDate(JsfUtil.getTodaysLocalDateTime()), JsfUtil.zoneDateTimeToDate(JsfUtil.getTodaysLocalDateTime()));
                            } else {
                                items = getFacade().findByDateRange(initialDate, finalDate);
                            }
                        } else {
                            if (initialDate == null) {
                                getTodayStudies();
                            }
                            if (isXrayServer) {
                                items = getFacade().findByDateRangeAndClinicId(initialDate, finalDate, clinicId);
                            } else {
//                                items = getFacade().findStudyByDateRange(formatedDate, formatedDate, clinicId);
                            }
                        }
                    }

                }
            }

//            items = getFacade().findAll();
        }
        return items;
    }

    public void openOHIFViewer() {
        String serverFullPath = serverUrl.substring(0, serverUrl.indexOf(":"));
        System.out.println("Selected studyInstanceUID: " + selected.getStudyInstanceUID() + "Server URL:" + serverFullPath);
        PrimeFaces.current().executeScript("openViewer('" + serverFullPath + "','" + selected.getStudyInstanceUID() + "');");
    }

    public void persist(JsfUtil.PersistAction persistAction, String successMessage) {
        if (selected != null) {
            try {
                if (persistAction != JsfUtil.PersistAction.DELETE) {
                    selected = getFacade().edit(selected);
                } else {
                    getFacade().remove(selected);
                }
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, successMessage);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, msg);
                } else {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"), ex);
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public Studies getStudies(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<Studies> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Studies> getItemsAvailableSelectOne() {
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

    public String getStudyDate() {
        if (selected != null) {
            Date realStudyDate;
            if (Objects.isNull(selected.getExternalDate())) {
                realStudyDate = selected.getCreationDate();
            } else {
                realStudyDate = JsfUtil.convertToDateFromString(selected.getExternalDate(), "yyyyMMdd");
            }
            studyDate = JsfUtil.formatDateWithPattern(realStudyDate, "yyyyMMdd");
        }
        return studyDate;
    }

    public void setStudyDate(String studyDate) {
        this.studyDate = studyDate;
    }

    public String getTodaysDate() {
        return todaysDate;
    }

    public void setTodaysDate(String todaysDate) {
        this.todaysDate = todaysDate;
    }

    public void setItems(List<Studies> items) {
        this.items = items;
    }

    public boolean isFindByPetId() {
        return findByPetId;
    }

    public void setFindByPetId(boolean findByPetId) {
        this.findByPetId = findByPetId;
    }

}
