/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.controller.dicom;

import java.io.Serializable;

/**
 *
 * @author Alexander Escobar L.
 */
public class DicomSend implements Serializable{

    private String[] imageIdList;
    private int dicomServerId;
    private String studyId;

    public String[] getImageIdList() {
        return imageIdList;
    }

    public void setImageIdList(String[] imageIdList) {
        this.imageIdList = imageIdList;
    }

    public int getDicomServerId() {
        return dicomServerId;
    }

    public void setDicomServerId(int dicomServerId) {
        this.dicomServerId = dicomServerId;
    }

    public String getStudyId() {
        return studyId;
    }

    public void setStudyId(String studyId) {
        this.studyId = studyId;
    }

}
