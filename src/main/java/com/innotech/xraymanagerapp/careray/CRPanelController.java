/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.careray;

import java.io.Serializable;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.inject.Inject;
import com.innotech.xraymanagerapp.business.generalxray.ImageAcquisitionDeviceInterface;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alexander Escobar L.
 */
//@Singleton
public class CRPanelController implements Serializable {

//    private static CRPanelController singletonInstance;

    private ImageAcquisitionDeviceInterface panelInterface;

    public CRPanelController() {
    }

    private CRPanelController(ImageAcquisitionDeviceInterface panelInterface) {
        // Dependency injection through contructor overload
        this.panelInterface = panelInterface;
    }

    public static CRPanelController getSingletonInstance(ImageAcquisitionDeviceInterface panelInterface) {
        return new CRPanelController(panelInterface);
    }

    public String translateError(int errorCode) {
        String errorCodeName = "ERROR_" + errorCode;
        String reply = "";

        if (errorCode == 0) {
            return reply;
        }
        return ErrorCodes.CRPanelError.valueOf(errorCodeName).getErrorDescription();
    }

    public boolean startCommunicationWithpanel() {
        return true;
    }

    //Check interface communication, connect to detector, ask for system info,
    //check for calibration files, and return string of all errors encountered.
    public boolean triage() {
        //Can only reveal one problem at a time
        boolean securityChecksPassed = true;
        String triageList = "";
        int test;

        //Void function prints Hello World
//        panelInterface.testInterface();
        //Try to connect to the detector
        test = panelInterface.connectDetector();/// 0 = no problem
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
            Logger.getLogger(CRPanelController.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
        if (test != 0) {
            securityChecksPassed = false;
            triageList += translateError(test) + "\n";
        }
        //Check for calibration files
        test = panelInterface.checkCalibration();// 0 = no problem
        if (test != 0) {
            securityChecksPassed = false;
            triageList += translateError(test) + "\n";
        }
        return securityChecksPassed;
    }

    /**
     * Starts by connecting to the detector, and starts the acquisition loop.
     * startRad sets ContinueRad = TRUE. startRad will fail if ContinueRad was
     * true before executing Assuming that there is not issues it starts with
     * the waiting for radiation process
     *
     * @return
     */
    public int startRad() {
        return panelInterface.startRad();// 0 = no problem
    }

    /**
     * Defines whether to stop the waiting for radiation loop or continue
     *
     * @param arg0 if false the loop will break and the system finishes.
     */
    public void setContinueRad(boolean arg0) {
        panelInterface.setContinueRad(arg0);// 0 = no problem
    }

    /**
     * Getter for boolean continueRad variable.
     *
     * @return status of continueRad
     */
    public boolean getContinueRad() {
        return panelInterface.getContinueRad();
    }

    /**
     * Global bool WaitingStatus initialized as False. WaitingStatus is
     * temporarily set TRUE when system actively waits for radiation
     * WaitingStatus is set false after sensor detects radiation before image
     * processing
     *
     * @return true if actively waiting for radiation, else false
     */
    public boolean getWaitingStatus() {
        return panelInterface.getWaitingStatus();
    }

    public int getCurrentProcessState() {
//        triage();
        return panelInterface.getCurrentProcessState();// 0 = no problem
    }

    public int connectDetector() {
        return panelInterface.connectDetector();//1002 = already connected, 0 = no problem = connected sucessfully
    }

    public int disconnectDetector() {
        return panelInterface.disconnectDetector();// 0 = no problem = disconnected successfully
    }

    public void stopCommunication() {
        panelInterface.stopCommunication();// 0 = no problem = disconnected successfully
    }

    /**
     * Shows working path of the system -> CareRay Configuration directory
     * should be here
     *
     * @return
     */
    public int showDetectorInfo() {
        return panelInterface.showDetectorInfo();
    }

    public int showStatusInfo() {
        return panelInterface.showStatusInfo();
    }

    /**
     * Calls CrApi function which checks the detector serial number Next, it
     * checks if calibration files exist with the same number in
     * C:\CareRayCalImgs\ Example Calibration file path
     * C:\CareRayCalImgs\C08170801-025
     *
     * @return 0 if no problem
     */
    public int checkCalibration() {
        return panelInterface.checkCalibration();
    }

    public boolean getExecuteNextProc() {
        return panelInterface.getExecuteNextProc();
    }

    public boolean getSoftSyncAcqEnd() {
        return panelInterface.getSoftSyncAcqEnd();
    }

    public boolean getResumeProcEnd() {
        return panelInterface.getResumeProcEnd();
    }

    public void simulateRadiation() {
        panelInterface.simulateRadiation();
    }

    public int test() {
        System.out.println("!!!! In CR Test...");
        return 0;
    }
}
