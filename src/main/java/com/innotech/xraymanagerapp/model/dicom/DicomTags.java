/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.model.dicom;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Alexander Escobar L.
 */
public class DicomTags  implements Serializable {
    
        private String patientName;//, VR.AE, "Pet FMS name");
        private String patientSex;//;, VR.CS, "M");
        private String patientID;// VR.CS, "10");
        private Date patientBirthDate;//, VR.AS, "19861010");
        private Date studyDate;//, VR.AS, "20140126");
        private Date seriesDate;//, VR.AS, "20140126");
        private Integer studyTime;//, VR.AS, "101010");
        private Integer seriesTime;//, VR.AS, "101010");
        private String studyDescription;//, VR.AS, "Study description for Pet FMS name");
        private String seriesDescription;//, VR.AS, "Series description for Pet FMS name");
        private String modality;//, VR.CS, "MODALITY MR");
        private Integer columns;//, VR.US, vf.getWidth());
        private Integer rows;//, VpR.US, vf.getHeight());
        private Integer instanceNumber;//, VR.US, 1);
        private Integer samplesPerPixel;//, VR.IS, 3);
        private String photometricInterpretation;//, VR.CS, "MONOCHROME2");
        private Integer bitsAllocated;//, VR.IS, 8);
        private Integer bitsStored;//, VR.IS, 8);
        private Integer numberOfFrames;//, VR.IS, 10);
        private Integer seriesNumber;//, VR.IS, 2);

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientSex() {
        return patientSex;
    }

    public void setPatientSex(String patientSex) {
        this.patientSex = patientSex;
    }

    public String getPatientID() {
        return patientID;
    }

    public void setPatientID(String patientID) {
        this.patientID = patientID;
    }

    public Date getPatientBirthDate() {
        return patientBirthDate;
    }

    public void setPatientBirthDate(Date patientBirthDate) {
        this.patientBirthDate = patientBirthDate;
    }

    public Date getStudyDate() {
        return studyDate;
    }

    public void setStudyDate(Date studyDate) {
        this.studyDate = studyDate;
    }

    public Date getSeriesDate() {
        return seriesDate;
    }

    public void setSeriesDate(Date seriesDate) {
        this.seriesDate = seriesDate;
    }

    public Integer getStudyTime() {
        return studyTime;
    }

    public void setStudyTime(Integer studyTime) {
        this.studyTime = studyTime;
    }

    public Integer getSeriesTime() {
        return seriesTime;
    }

    public void setSeriesTime(Integer seriesTime) {
        this.seriesTime = seriesTime;
    }

    public String getStudyDescription() {
        return studyDescription;
    }

    public void setStudyDescription(String studyDescription) {
        this.studyDescription = studyDescription;
    }

    public String getSeriesDescription() {
        return seriesDescription;
    }

    public void setSeriesDescription(String seriesDescription) {
        this.seriesDescription = seriesDescription;
    }

    public String getModality() {
        return modality;
    }

    public void setModality(String modality) {
        this.modality = modality;
    }

    public Integer getColumnsp() {
        return columns;
    }

    public void setColumnsp(Integer columnsp) {
        this.columns = columnsp;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public Integer getInstancpeNumber() {
        return instanceNumber;
    }

    public void setInstancpeNumber(Integer instancpeNumber) {
        this.instanceNumber = instancpeNumber;
    }

    public Integer getSamplesPerPixel() {
        return samplesPerPixel;
    }

    public void setSamplesPerPixel(Integer samplesPerPixel) {
        this.samplesPerPixel = samplesPerPixel;
    }

    public String getPhotometricInterpretation() {
        return photometricInterpretation;
    }

    public void setPhotometricInterpretation(String photometricInterpretation) {
        this.photometricInterpretation = photometricInterpretation;
    }

    public Integer getColumns() {
        return columns;
    }

    public void setColumns(Integer columns) {
        this.columns = columns;
    }

    public Integer getInstanceNumber() {
        return instanceNumber;
    }

    public void setInstanceNumber(Integer instanceNumber) {
        this.instanceNumber = instanceNumber;
    }

    public Integer getBitsAllocated() {
        return bitsAllocated;
    }

    public void setBitsAllocated(Integer bitsAllocated) {
        this.bitsAllocated = bitsAllocated;
    }

    public Integer getBitsStored() {
        return bitsStored;
    }

    public void setBitsStored(Integer bitsStored) {
        this.bitsStored = bitsStored;
    }

    public Integer getNumberOfFrames() {
        return numberOfFrames;
    }

    public void setNumberOfFrames(Integer numberOfFrames) {
        this.numberOfFrames = numberOfFrames;
    }

    public Integer getSeriesNumber() {
        return seriesNumber;
    }

    public void setSeriesNumber(Integer seriesNumber) {
        this.seriesNumber = seriesNumber;
    }
    
}
