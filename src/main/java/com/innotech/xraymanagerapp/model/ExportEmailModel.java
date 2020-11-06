/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.model;

import java.io.File;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Hi
 */
public class ExportEmailModel implements Serializable {

    private File file;
    private Annotations annotations;
    private Integer patientId;
    private String patientName;
    private String patientAge;
    private String patientSex;
    private String toothName;
    private ZonedDateTime imageDate;
    private String patientGender;
    private Integer imageId;
    private String serverIp;
    private ViewDicomTags imageTags;
    private List<ImageAnnotationState> imageAnnotationsList;
    private String imageAsBase64;
    private String imagePath;
    private String studyDate;

    public ExportEmailModel() {
    }

    public ExportEmailModel(File file, Annotations annotations, ZonedDateTime imageDate) {
        this.file = file;
        this.annotations = annotations;
        this.imageDate = imageDate;
    }

    public ExportEmailModel(Integer imageId, String toothName, String imageAsBase64, String imagePath,
            List<ImageAnnotationState> imageAnnotationsList, ViewDicomTags imageTags) {
        this.toothName = toothName;
        this.imageId = imageId;
        this.imageAnnotationsList = imageAnnotationsList;
        this.imageAsBase64 = imageAsBase64;
        this.imagePath = imagePath;
        this.imageTags = imageTags;
    }

    public ExportEmailModel(Integer imageId, String toothName, String imageAsBase64, String imagePath,
            List<ImageAnnotationState> imageAnnotationsList, ViewDicomTags imageTags,
            String patientName, String patientAge,
            String studyDate, String patientGender, File file) {
        this.imageId = imageId;
        this.toothName = toothName;
        this.imageAsBase64 = imageAsBase64;
        this.imagePath = imagePath;
        this.imageAnnotationsList = imageAnnotationsList;
        this.imageTags = imageTags;
        this.file = file;
        this.patientName = patientName;
        this.patientAge = patientAge;
        this.studyDate = studyDate;
        this.patientGender = patientGender;
    }

    public ExportEmailModel(Integer imageId, String toothName, String imageAsBase64, String imagePath,
            List<ImageAnnotationState> imageAnnotationsList, ViewDicomTags imageTags,
            String patientName, String patientAge,
            String studyDate, String patientGender) {
        this.imageId = imageId;
        this.toothName = toothName;
        this.imageAsBase64 = imageAsBase64;
        this.imagePath = imagePath;
        this.imageAnnotationsList = imageAnnotationsList;
        this.imageTags = imageTags;
        this.patientName = patientName;
        this.patientAge = patientAge;
        this.studyDate = studyDate;
        this.patientGender = patientGender;
    }

    public ExportEmailModel(File file, String patientName, String patientAge, String toothName, ZonedDateTime imageDate, String patientGender) {
        this.file = file;
        this.patientName = patientName;
        this.patientAge = patientAge;
        this.toothName = toothName;
        this.imageDate = imageDate;
        this.patientGender = patientGender;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientAge() {
        return patientAge;
    }

    public void setPatientAge(String patientAge) {
        this.patientAge = patientAge;
    }

    public String getToothName() {
        return toothName;
    }

    public void setToothName(String toothName) {
        this.toothName = toothName;
    }

    public ZonedDateTime getImageDate() {
        return imageDate;
    }

    public void setImageDate(ZonedDateTime imageDate) {
        this.imageDate = imageDate;
    }

    public String getPatientGender() {
        return patientGender;
    }

    public void setPatientGender(String patientGender) {
        this.patientGender = patientGender;
    }

    public String getImageAsBase64() {
        return imageAsBase64;
    }

    public void setImageAsBase64(String imageAsBase64) {
        this.imageAsBase64 = imageAsBase64;
    }

    public Integer getImageId() {
        return imageId;
    }

    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public List<ImageAnnotationState> getImageAnnotationsList() {
        return imageAnnotationsList;
    }

    public void setImageAnnotationsList(List<ImageAnnotationState> imageAnnotationsList) {
        this.imageAnnotationsList = imageAnnotationsList;
    }

    public Annotations getAnnotations() {
        return annotations;
    }

    public void setAnnotations(Annotations annotations) {
        this.annotations = annotations;
    }

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    public String getPatientSex() {
        return patientSex;
    }

    public void setPatientSex(String patientSex) {
        this.patientSex = patientSex;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public ViewDicomTags getImageTags() {
        return imageTags;
    }

    public void setImageTags(ViewDicomTags imageTags) {
        this.imageTags = imageTags;
    }

    public String getStudyDate() {
        return studyDate;
    }

    public void setStudyDate(String studyDate) {
        this.studyDate = studyDate;
    }

}
