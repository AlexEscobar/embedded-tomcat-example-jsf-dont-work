/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.model.dicom;

import com.innotech.xraymanagerapp.model.Annotations;
import com.innotech.xraymanagerapp.model.Images;
import com.innotech.xraymanagerapp.model.PetPatients;
import com.innotech.xraymanagerapp.model.Studies;
import com.innotech.xraymanagerapp.model.Users;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Hi
 */
public class LoaderObject implements Serializable{
    private static final long serialVersionUID = 1L;
   
    
    private Users user;
    private PetPatients patient;
    private Studies study;
    private List<Annotations> annotationsList;
    private Images image;
    private boolean isLocal;

    public LoaderObject() {
    }    

    public LoaderObject(PetPatients patient, Studies study) {
        this.patient = patient;
        this.study = study;
    }

    public PetPatients getPatient() {
        return patient;
    }

    public void setPatient(PetPatients patient) {
        this.patient = patient;
    }

    public Studies getStudy() {
        return study;
    }

    public void setStudy(Studies study) {
        this.study = study;
    }  

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public List<Annotations> getAnnotationsList() {
        return annotationsList;
    }

    public void setAnnotationsList(List<Annotations> annotationsList) {
        this.annotationsList = annotationsList;
    }

    public Images getImage() {
        return image;
    }

    public void setImage(Images imagesList) {
        this.image = imagesList;
    }

    public boolean isIsLocal() {
        return isLocal;
    }

    public void setIsLocal(boolean isLocal) {
        this.isLocal = isLocal;
    }
}
