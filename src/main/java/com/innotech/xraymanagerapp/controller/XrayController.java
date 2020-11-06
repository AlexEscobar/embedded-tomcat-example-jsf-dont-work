/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.controller;

import com.innotech.xraymanagerapp.controller.util.constants.Urls;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 *
 * @author Alexander Escobar L.
 */
@Named
@RequestScoped
public class XrayController {
    
    private boolean isHDRSensor;// if true means current sensor is HDR otherwise, the current one is EZ

    public String redirectToWorklist() {
        return new StringBuilder("pretty:").append(Urls.WORKLIST_PAGE).toString();
    }

    public String redirectToStudyList() {
        return new StringBuilder("pretty:").append(Urls.STUDYLIST_PAGE).toString();
    }

    public String redirectToDentalXrayView() {
        return new StringBuilder("pretty:").append(Urls.XRAY_PAGE).toString();
    }

    public String redirectToGeneralXrayView() {
        return new StringBuilder("pretty:").append(Urls.GENERAL_XRAY_PAGE).toString();
    }
}
