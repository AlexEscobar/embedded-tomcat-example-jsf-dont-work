/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.business.generalxray;

/**
 *
 * @author Hi
 */
public class FlatPanelCommunicationMessagesModel {

    private boolean calibrationFiles = false;
    private boolean detectorConnected = false;
    private boolean continueRad = false;
    private boolean isPanelWaiting = false;
    private int battery = 50000;
    private int connection = 50000;
    private int temperature = 50000;
    private int tempStatus = 50000;
    private int currentProcessState = 50000;
    private int currentCode = 50000;
    public String error = "";
    public String info = "";

 
    public boolean isCalibrationFiles() {
        return calibrationFiles;
    }

    public void setCalibrationFiles(boolean calibrationFiles) {
        this.calibrationFiles = calibrationFiles;
    }

    public boolean isDetectorConnected() {
        return detectorConnected;
    }

    public void setDetectorConnected(boolean detectorConnected) {
        this.detectorConnected = detectorConnected;
    }

    public boolean isContinueRad() {
        return continueRad;
    }

    public void setContinueRad(boolean continueRad) {
        this.continueRad = continueRad;
    }

    public boolean isIsPanelWaiting() {
        return isPanelWaiting;
    }

    public void setIsPanelWaiting(boolean isPanelWaiting) {
        this.isPanelWaiting = isPanelWaiting;
    }

    public int getBattery() {
        return battery;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }

    public int getConnection() {
        return connection;
    }

    public void setConnection(int connection) {
        this.connection = connection;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public int getTempStatus() {
        return tempStatus;
    }

    public void setTempStatus(int tempStatus) {
        this.tempStatus = tempStatus;
    }

    public int getCurrentProcessState() {
        return currentProcessState;
    }

    public void setCurrentProcessState(int currentProcessState) {
        this.currentProcessState = currentProcessState;
    }

    public int getCurrentCode() {
        return currentCode;
    }

    public void setCurrentCode(int currentCode) {
        this.currentCode = currentCode;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
    
    
}
