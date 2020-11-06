/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.controller.export;

/**
 *
 * @author Alexander Escobar Luna.
 */
public class XrayExport {
    
    private String[] imageIdList;
    private String imageFormat;// DICOM or JPEG

    public String[] getImageIdList() {
        return imageIdList;
    }

    public void setImageIdList(String[] imageIdList) {
        this.imageIdList = imageIdList;
    }

    public String getImageFormat() {
        return imageFormat;
    }

    public void setImageFormat(String imageFormat) {
        this.imageFormat = imageFormat;
    }
    
}
