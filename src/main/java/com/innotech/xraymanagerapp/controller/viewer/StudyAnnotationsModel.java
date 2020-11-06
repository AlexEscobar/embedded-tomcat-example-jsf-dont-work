/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.controller.viewer;

/**
 *
 * @author Alexander Escobar L.
 */
public class StudyAnnotationsModel {
    
    private int studyId;
    private Object viewportAnnotations;
    private Object dataAnnotations;

    public int getStudyId() {
        return studyId;
    }

    public void setStudyId(int studyId) {
        this.studyId = studyId;
    }

    public Object getViewportAnnotations() {
        return viewportAnnotations;
    }

    public void setViewportAnnotations(Object viewportAnnotations) {
        this.viewportAnnotations = viewportAnnotations;
    }

    public Object getDataAnnotations() {
        return dataAnnotations;
    }

    public void setDataAnnotations(Object dataAnnotations) {
        this.dataAnnotations = dataAnnotations;
    }
    
    
}
