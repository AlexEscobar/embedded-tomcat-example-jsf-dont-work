/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.business.generalxray;

import javax.ejb.Local;


/**
 *
 * @author Alexander Escobar L.
 */
//@Local
public interface ImageAcquisitionDeviceInterface {

    public boolean startCommunication();
    public boolean stopCommunication();
    
    /**
     * Starts by connecting to the detector, and starts the acquisition loop.
     * startRad sets ContinueRad = TRUE. startRad will fail if ContinueRad was
     * true before executing Assuming that there is not issues it starts with
     * the waiting for radiation process
     *
     * @return
     */
    public int startRad();

    public boolean getExecuteNextProc();

    public boolean getSoftSyncAcqEnd();

    public boolean getResumeProcEnd();

    public void simulateRadiation();

    /**
     * Defines whether to stop the waiting for radiation loop or continue
     *
     * @param arg0 if false the loop will break and the system finishes.
     */
    public void setContinueRad(boolean arg0);

    /**
     * Getter for boolean continueRad variable.
     *
     * @return status of continueRad
     */
    public boolean getContinueRad();

    /**
     * Global boolean WaitingStatus initialized as False. WaitingStatus is
     * temporarily set TRUE when system actively waits for radiation
     * WaitingStatus is set false after sensor detects radiation before image
     * processing
     *
     * @return true if actively waiting for radiation, else false
     */
    public boolean getWaitingStatus();

    public int connectDetector();

    public int disconnectDetector();

    /**
     * Shows working path of the system -> CareRay Configuration directory
     * should be here
     *
     * @return
     */
    public int showDetectorInfo();

    public int showStatusInfo();

    /**
     * Calls CrApi function which checks the detector serial number Next, it
     * checks if calibration files exist with the same number in
     * C:\CareRayCalImgs\ Example Calibration file path
     * C:\CareRayCalImgs\C08170801-025
     *
     * @return 0 if no problem
     */
    public int checkCalibration();

    /**
     * Prints "Hello World!" from the CRInterfaceImpl
     */
    public void testInterface();

    /**
     * Returns the current image acquisition process state: CPS Line Function
     * Notes 0 56 Global variable initialized to 0 1 796 performRadAcquisition()
     * Waiting for radiation 2 682&703 getImage() Image grabbed, processImage()
     * called next 3 221 writeImageToDisk() Finished saving RAW 4 515
     * processImage() Raw data loaded for further processing 5 526
     * processImage() Temp vectors and arrays created 6 533 processImage()
     * Populated vectors and inverted image 7 543 processImage() Image
     * normalized, saving bmp next 8 545 processImage() Finished saving image 0
     * 881 performRadAcquisition() 3.1 second delay while state = 8 at end of Fn
     *
     * @return CPS state
     */
    public int getCurrentProcessState();
}
