/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.model;

/**
 *
 * @author Alexander Escobar L.
 */
public class DicomTags {
    private String tagId;//(Group, element) e.g. (0008,0020)
    private String tagDescription;// StudyDate
    private String tagValue;// 20190430
    private String tagVr;// UI, SH, DA, TM, CS,....
    private String xmlAsString;// the full xml file text

    public DicomTags(String tagId, String tagDescription, String tagValue) {
        this.tagId = tagId;
        this.tagDescription = tagDescription;
        this.tagValue = tagValue;
    }

    public DicomTags(String tagId, String tagDescription) {
        this.tagId = tagId;
        this.tagDescription = tagDescription;
    }
    
    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public String getTagDescription() {
        return tagDescription;
    }

    public void setTagDescription(String tagDescription) {
        this.tagDescription = tagDescription;
    }

    public String getTagValue() {
        return tagValue;
    }

    public void setTagValue(String tagValue) {
        this.tagValue = tagValue;
    }

    public String getTagVr() {
        return tagVr;
    }

    public void setTagVr(String tagVr) {
        this.tagVr = tagVr;
    }

    public String getXmlAsString() {
        return xmlAsString;
    }

    public void setXmlAsString(String xmlAsString) {
        this.xmlAsString = xmlAsString;
    }
    
}
