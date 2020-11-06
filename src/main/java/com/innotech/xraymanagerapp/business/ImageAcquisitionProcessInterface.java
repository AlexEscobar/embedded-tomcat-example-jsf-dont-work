/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.business;

import com.innotech.xraymanagerapp.model.Images;
import java.util.List;

/**
 *
 * @author Alexander Escobar L.
 */
public interface ImageAcquisitionProcessInterface {
    public void initializeSensors();
    public void initializeProperties();
    public void initializeHDRSensorCommunication();
    public void initializeCurrentImage();
    public boolean clearOutputFolder();
    public void calculateProgressBarPercentage(int currentValue);
    public void onHDRSensorError(String message);
    public boolean verifyAnnotationList();
    public boolean setCurrent();
    public void updateCurrent();
    public void startNextCurrent();
    public String getCurrentXrayImage();
    public List<Images> checkImageListChanges();
    public void launchStopSensorProgramm();
    public boolean checkEZSensorConnection();
    public boolean checkHDRSensorConnection();
    public void showSensorDisconnectedError();
    public boolean sensorLauncher(String sensorName, String threadName);
    public void sensorKiller();
    public void selectDeselectTeeth();
}
