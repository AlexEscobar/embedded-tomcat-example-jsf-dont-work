/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.business;

import com.innotech.xraymanagerapp.controller.util.constants.ConfigurationProperties;
import com.innotech.xraymanagerapp.dto.StudiesFacade;
import com.innotech.xraymanagerapp.model.Users;
import java.io.Serializable;
import java.util.Objects;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 *
 * @author Alexander Escobar L.
 */
@Stateless
public class StudiesBusinessControllerImpl extends com.innotech.xraymanagerapp.business.StudiesBusinessController implements Serializable {

    @EJB
    private com.innotech.xraymanagerapp.dto.StudiesFacade ejbFacade;
    @EJB
    private ConfigurationBusinessController ejbConfigurationController;  
    
    public StudiesBusinessControllerImpl() {
        if(Objects.nonNull(ejbConfigurationController))
            serverUrl = ejbConfigurationController.getConfigurationMap().get(ConfigurationProperties.APP_SERVER_NAME);
        System.out.println("constructor of StudiesBusinessControllerImpl;");
    }
    
    
    
    @Override
    public Integer getSelectedPatient() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Users getLoggedUser() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setLoggedUser(Users user) {
        this.loggedUser = user;
    }

    @Override
    public void setSelectedPatient(Integer patientId) {
        this.patientId = patientId;
    }

    @Override
    public StudiesFacade getEjbFacade() {
        return ejbFacade;
    }

    @Override
    public ConfigurationBusinessController getEjbConfigurationController() {
        return ejbConfigurationController;
    }

}
