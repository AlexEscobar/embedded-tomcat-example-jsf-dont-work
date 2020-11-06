/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.controller.util;

import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

/**
 * Safely starts and stops the watch service thread that watches the sensor x-ray image output folder
 * This bean is initialized from x-ray page by calling the property manager in a hidden input.
 * @author Alexander Escobar Luna
 */
@Named("backgroundJobManager")
@ViewScoped
public class BackgroundJobManager implements Serializable {

    @EJB
    private com.innotech.xraymanagerapp.dto.AnnotationsFacade ejbFacade;
    private ScheduledExecutorService scheduler;
    private String manager;

    @PostConstruct
    public void init() {
        startProcess();
    }
    
    public void startProcess(){
        try {
            FacesContext fc = FacesContext.getCurrentInstance();
            Integer userId = AuthenticationUtils.getLoggedUser().getId();
            scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.scheduleAtFixedRate(new SensorFileRunner(fc, userId, ejbFacade), 0, 1, TimeUnit.HOURS);
        } catch (Exception e) {
            Logger.getLogger(BackgroundJobManager.class.getName()).log(Level.SEVERE, null, e);
        }        
    }

    @PreDestroy
    public void destroy() {
        if(scheduler != null){
            scheduler.shutdownNow();
        }
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }
}
