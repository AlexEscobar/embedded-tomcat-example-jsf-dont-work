/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.controller;

import com.innotech.xraymanagerapp.business.ViewerBusinessController;
import com.innotech.xraymanagerapp.controller.util.JsfUtil;
import com.innotech.xraymanagerapp.model.DicomServers;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

/**
 *
 * @author Alexander Escobar Note that the bean #{sensorImageController} is
 * @ApplicationScoped as it basically represents a stateless service. It could
 * be @RequestScoped, but then the bean would be recreated on every single
 * request, for nothing. It cannot be @ViewScoped, because at the moment the
 * browser needs to download the image, the server doesn't create a JSF page. It
 * can be
 * @SessionScoped, but then it's saved in memory, for nothing.
 */
@Named("viewerController")
@ViewScoped
public class ViewerController extends ViewerBusinessController implements Serializable {

    private StudiesController sc;
//

    @PostConstruct
    @Override
    public void init() {
        initializeLists();
        isLoadingImagesFromPageload = true;
        selectedDicomServer = new DicomServers();
        initializePathVariables();
        setCurrentStudyFromScopedBean();

        // clears the temporary path where images that shown in the dicom viewer are located
//        JsfUtil.cleanDirectory(tempPath, "jpg");
    }

    public void setCurrentStudyFromScopedBean() {

        if (sc == null) {
            sc = (StudiesController) JsfUtil.getSessionScopeBean("studiesController");
        }
        setCurrentStudy(sc.getSelected());
    }

}
