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
public class FlatPanel implements Serializable {

    public static boolean calibrationFiles = false;
    public static boolean detectorConnected = false;
    public static boolean continueRad = false;
    public static boolean isPanelWaiting = false;
    public static int battery = 50000;
    public static int connection = 50000;
    public static int temperature = 50000;
    public static int tempStatus = 50000;
    public static int currentProcessState = 50000;
    public static int currentCode = 50000;
    public static String error = "";
    public static String info = "";

    public FlatPanel(String incoming) {
//        String incoming = "<CPS>1<INFO>Waiting for Radiation<CPS>2<INFO>Radiation Detected";
        String delim = "<";
        String[] tokens = incoming.split(delim);
        String currentLine = "";

        for (String token : tokens) {
            currentLine = "<" + token;
            setCurrentValues(currentLine);            
        }
    }

    public static void initializeProperties() {
//        System.out.println("FlatPanel.initializeProperties() called....");
        calibrationFiles = false;
        detectorConnected = false;
        continueRad = false;
        currentProcessState = 50000;
        battery = 50000;
        isPanelWaiting = false;
        connection = 50000;
        error = "";
        info = "";
    }

    public static void setCurrentValues(String serverMessage) {
        if (serverMessage.startsWith(SocketCommunicationCodes.CURRENT_PROCESS_STATE)) {
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
        if (currentProcessState < 500) {
            info = getPanelStringInfoResponse(SocketCommunicationCodes.INFO, serverMessage);
            error = "";
        } else {
            if (serverMessage.contains(SocketCommunicationCodes.ERROR)) {
                error = getPanelStringErrorResponse(SocketCommunicationCodes.ERROR, serverMessage);
            }
        }
    }

    public static int getPanelResponse(String code, String response) {
        int responseCode = 50000;
//        System.out.println("code: " + code + " - response to see the cps that is becoming 50000: " + response);
        try {
            String toReplace = response.replace(code, "").trim();
//            System.out.println("toReplace: " + toReplace);
            if (!toReplace.contains("...")) {
                responseCode = Integer.parseInt(toReplace);
            } else {
                responseCode = 0;
            }
            if (responseCode == 81) {
                responseCode = 1;
            }
//            System.out.println("responseCode: " + responseCode);
        } catch (NullPointerException | NumberFormatException ex) {
            Logger.getLogger(CRInterfaceBySocketsImpl.class.getName()).log(Level.SEVERE, ex.getMessage());

        }
        currentCode = responseCode;
        return responseCode;
    }

    public static boolean getPanelStringResponse(String code, String response) {
//        System.out.println("code: " + code + " - response: " + response);
        try {
            String toReplace = response.replace(code, "").trim();
//            System.out.println("toReplace: " + toReplace);
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

    public static String getPanelStringErrorResponse(String code, String response) {
        String responseMessage = "";
        try {
            if (response.contains(SocketCommunicationCodes.ERROR)) {
                responseMessage = response.replace(code, "Error").trim();
            }
        } catch (NullPointerException | NumberFormatException ex) {
            Logger.getLogger(CRInterfaceBySocketsImpl.class.getName()).log(Level.SEVERE, ex.getMessage());

        }
        return responseMessage;
    }

    public static String getPanelStringInfoResponse(String code, String response) {
        String responseMessage = "";
        try {
            if (response.contains(SocketCommunicationCodes.INFO)) {
                responseMessage = response.replace(code, "").trim();
            }
        } catch (NullPointerException | NumberFormatException ex) {
            Logger.getLogger(CRInterfaceBySocketsImpl.class.getName()).log(Level.SEVERE, ex.getMessage());

        }
        return responseMessage;
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
                + "currentCode: " + currentCode + "\n"
                + "Error: " + error;
    }

    public static String print() {
        return "CalibrationFiles: " + calibrationFiles + "\n"
                + "detectorConnected: " + detectorConnected + "\n"
                + "continueRad: " + continueRad + "\n"
                + "isPanelWaiting: " + isPanelWaiting + "\n"
                + "battery: " + battery + "\n"
                + "connection: " + connection + "\n"
                + "temperature: " + temperature + "\n"
                + "tempStatus: " + tempStatus + "\n"
                + "currentProcessState: " + currentProcessState + "\n"
                + "currentCode: " + currentCode + "\n"
                + "Error: " + error;
    }
//    
//    public static void socketMessagesEventListener(@Observes SocketClient event) {
//        System.out.println("The event received from the c++ server On the FlatPanel Class: "+event.getMsg());
//        setCurrentValues(event.getMsg());
//    }
}
