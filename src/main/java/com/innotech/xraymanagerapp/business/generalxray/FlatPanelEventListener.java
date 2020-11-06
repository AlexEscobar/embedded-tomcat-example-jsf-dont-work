/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.business.generalxray;

import com.innotech.xraymanagerapp.careray.CRInterfaceBySocketsImpl;
import com.innotech.xraymanagerapp.careray.SocketCommunicationCodes;
import com.innotech.xraymanagerapp.controller.socket.SocketClient;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.event.Observes;

/**
 *
 * @author PC
 */
public final class FlatPanelEventListener implements Serializable {

    public boolean calibrationFiles = false;
    public boolean detectorConnected = false;
    public boolean continueRad = false;
    public boolean isPanelWaiting = false;
    public int battery = 50000;
    public int connection = 50000;
    public int temperature = 50000;
    public int tempStatus = 50000;
    public int currentProcessState = 50000;
    public int currentCode = 50000;

    public FlatPanelEventListener(String message) {
        setCurrentValues(message);
    }

    public void setCurrentValues(String serverMessage) {
        if (serverMessage.contains(SocketCommunicationCodes.CURRENT_PROCESS_STATE)) {
            currentProcessState = getPanelResponse(SocketCommunicationCodes.CURRENT_PROCESS_STATE, serverMessage);
        } else if (serverMessage.contains(SocketCommunicationCodes.CALIB)) {
            calibrationFiles = getPanelResponse(SocketCommunicationCodes.CALIB, serverMessage) == 0;// 0 = calibration files found
        } else if (serverMessage.contains(SocketCommunicationCodes.START)) {
            detectorConnected = getPanelResponse(SocketCommunicationCodes.START, serverMessage) == 0;// 0 = calibration files found
        } else if (serverMessage.contains(SocketCommunicationCodes.RAD)) {
            continueRad = getPanelStringResponse(SocketCommunicationCodes.RAD, serverMessage);
        } else if (serverMessage.contains(SocketCommunicationCodes.BATTERY)) {
            battery = getPanelResponse(SocketCommunicationCodes.BATTERY, serverMessage);
        } else if (serverMessage.contains(SocketCommunicationCodes.BATTERY)) {
            battery = getPanelResponse(SocketCommunicationCodes.BATTERY, serverMessage);
        } else if (serverMessage.contains(SocketCommunicationCodes.CONNECTION)) {
            connection = getPanelResponse(SocketCommunicationCodes.CONNECTION, serverMessage);
        } else if (serverMessage.contains(SocketCommunicationCodes.TEMPC)) {
            temperature = getPanelResponse(SocketCommunicationCodes.TEMPC, serverMessage);
        } else if (serverMessage.contains(SocketCommunicationCodes.TEMPS)) {
            tempStatus = getPanelResponse(SocketCommunicationCodes.TEMPS, serverMessage);
        } else if (serverMessage.contains(SocketCommunicationCodes.GET_RAD)) {
            isPanelWaiting = getPanelResponse(SocketCommunicationCodes.GET_RAD, serverMessage) == 1;
        }
    }

    public int getPanelResponse(String code, String response) {
        int responseCode = 50000;
        System.out.println("code: " + code + " - response: " + response);
        try {
            String toReplace = response.replace(code, "").trim();
            System.out.println("toReplace: " + toReplace);
            if (!toReplace.contains("...")) {
                responseCode = Integer.parseInt(toReplace);
            }
            System.out.println("responseCode: " + responseCode);
        } catch (NullPointerException | NumberFormatException ex) {
            Logger.getLogger(CRInterfaceBySocketsImpl.class.getName()).log(Level.SEVERE, ex.getMessage());

        }
        currentCode = responseCode;
        return responseCode;
    }

    public static boolean getPanelStringResponse(String code, String response) {
        System.out.println("code: " + code + " - response: " + response);
        try {
            String toReplace = response.replace(code, "").trim();
            System.out.println("toReplace: " + toReplace);
            if (toReplace.contains(SocketCommunicationCodes.RAD_RESPONSE)) {
                return true;
            } else if (toReplace.contains(SocketCommunicationCodes.STOP_RESPONSE)) {
                return true;
            }
        } catch (NullPointerException | NumberFormatException ex) {
            Logger.getLogger(CRInterfaceBySocketsImpl.class.getName()).log(Level.SEVERE, ex.getMessage());

        }
        return false;
    }

//    
//    public static void socketMessagesEventListener(@Observes SocketClient event) {
//        System.out.println("The event received from the c++ server On the FlatPanel Class: "+event.getMsg());
//        setCurrentValues(event.getMsg());
//    }
    public boolean isCalibrationFiles() {
        return calibrationFiles;
    }

    public boolean isDetectorConnected() {
        return detectorConnected;
    }

    public boolean isContinueRad() {
        return continueRad;
    }

    public boolean isIsPanelWaiting() {
        return isPanelWaiting;
    }

    public int getBattery() {
        return battery;
    }

    public int getConnection() {
        return connection;
    }

    public int getTemperature() {
        return temperature;
    }

    public int getTempStatus() {
        return tempStatus;
    }

    public int getCurrentProcessState() {
        return currentProcessState;
    }

    public int getCurrentCode() {
        return currentCode;
    }

    @Override
    public String toString() {
        return "CalibrationFiles: " + calibrationFiles + "\n"
                + "detectorConnected: " + detectorConnected + "\n"
                + "continueRad: " + continueRad + "\n"
                + "isPanelWaiting: " + isPanelWaiting + "\n"
                + "battery: " + battery + "\n"
                + "connection: " + connection + "\n"
                + "temperature: " + temperature + "\n"
                + "tempStatus: " + tempStatus + "\n"
                + "currentProcessState: " + currentProcessState + "\n"
                + "currentCode: " + currentCode;
    }
}
