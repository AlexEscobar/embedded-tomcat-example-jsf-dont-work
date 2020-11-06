/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.model.dicom;

/**
 *
 * @author Hi
 */
public class DicomUtils {
    
    private static final String StudyInstanceUID = "1.2.811.0.2808090.8.6.0.00210.2.4.";
    private static final String SeriesInstanceUID = "1.2.811.0.2808090.8.6.0.00210.2.4.";
    private static final String SopInstanceUID = "1.2.840.10008.5.1.4.1.1.1.3.";
    private static final String SopClassUID = "1.2.840.10008.5.1.4.1.1.1.3";

    public static String getStudyInstanceUID() {
        return StudyInstanceUID;
    }

    public static String getSeriesInstanceUID() {
        return SeriesInstanceUID;
    }

    public static String getSopInstanceUID() {
        return SopInstanceUID;
    }

    public static String getSopClassUID() {
        return SopClassUID;
    }
}
