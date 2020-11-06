/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.careray;

import com.innotech.xraymanagerapp.business.generalxray.FlatPanel;
import com.innotech.xraymanagerapp.controller.socket.SocketClient;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.innotech.xraymanagerapp.business.generalxray.ImageAcquisitionDeviceInterface;

/**
 *
 * @author Alexander Escobar L.
 */
//@Singleton
//@Default
public final class CRInterfaceBySocketsImpl implements ImageAcquisitionDeviceInterface, Serializable {

    private final SocketClient client;
    private final int socketClientPort;
    
    public CRInterfaceBySocketsImpl(SocketClient client, int socketClientPort) {
        this.client = client;
        this.socketClientPort = socketClientPort;
        startCommunication();
    }

    @Override
    public boolean startCommunication() {
        boolean communicationStarted = false;
        return communicationStarted;
    }

    @Override
    public boolean stopCommunication() {
        client.sendMessages(SocketCommunicationCodes.KILL, socketClientPort);
        return client.closeConnection();
    }

    @Override
    public int startRad() {
        client.sendMessages(SocketCommunicationCodes.RAD, socketClientPort);
        return FlatPanel.continueRad ? 0 : FlatPanel.currentCode;
    }

    @Override
    public boolean getExecuteNextProc() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean getSoftSyncAcqEnd() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean getResumeProcEnd() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void simulateRadiation() {
        client.sendMessages(SocketCommunicationCodes.SIMULATE, socketClientPort);
    }

    @Override
    public void setContinueRad(boolean arg) {
        if (arg) {
            client.sendMessages(SocketCommunicationCodes.RAD, socketClientPort);
        } else {
            client.sendMessages(SocketCommunicationCodes.STOP, socketClientPort);
        }
    }

    @Override
    public boolean getContinueRad() {
        client.sendMessages(SocketCommunicationCodes.GET_RAD, socketClientPort);
        return FlatPanel.isPanelWaiting;
    }

    /**
     * return true if current process state = 1, false otherwise
     *
     * @return true if current process state = 1, false otherwise
     */
    @Override
    public boolean getWaitingStatus() {
        return FlatPanel.isPanelWaiting;
    }

    @Override
    public int connectDetector() {
        client.sendMessages(SocketCommunicationCodes.START, socketClientPort);
        return FlatPanel.detectorConnected ? 0 : FlatPanel.currentCode;
    }

    @Override
    public int disconnectDetector() {
        client.sendMessages(SocketCommunicationCodes.STOP, socketClientPort);
        return getPanelResponse(SocketCommunicationCodes.STOP, client.getMsg());
    }

    @Override
    public int showDetectorInfo() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int showStatusInfo() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int checkCalibration() {
        client.sendMessages(SocketCommunicationCodes.CALIB, socketClientPort);
        return FlatPanel.calibrationFiles ? 0 : FlatPanel.currentCode;
    }

    @Override
    public void testInterface() {
        System.out.println("TestInterface called on the CRInterfaceBySocketsImpl class");
    }

    @Override
    public int getCurrentProcessState() {
        client.sendMessages(SocketCommunicationCodes.CURRENT_PROCESS_STATE, socketClientPort);
        return FlatPanel.currentProcessState;
    }

    public int getPanelResponse(String code, String response) {
        int responseCode = 50000;
        try {
            String toReplace = response.replace(code, "").trim();
            if (!toReplace.contains("...")) {
                responseCode = Integer.parseInt(toReplace);
            }
            System.out.println("responseCode: " + responseCode);
        } catch (NullPointerException | NumberFormatException ex) {
            Logger.getLogger(CRInterfaceBySocketsImpl.class.getName()).log(Level.SEVERE, ex.getMessage());

        }
        return responseCode;
    }

}
