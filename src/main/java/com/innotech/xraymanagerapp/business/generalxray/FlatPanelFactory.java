/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.business.generalxray;

import com.innotech.xraymanagerapp.careray.CRInterfaceBySocketsImpl;
import com.innotech.xraymanagerapp.controller.socket.SocketClient;
import java.io.Serializable;

/**
 *
 * @author Alexander Escobar L.
 */
public class FlatPanelFactory  implements Serializable{

    private static ImageAcquisitionDeviceInterface currentPanelSingletonInstance;

    private static ImageAcquisitionDeviceInterface getCurrentPanel(int communicationType, SocketClient client, int socketClientPort) {
        ImageAcquisitionDeviceInterface currentPanelInstance = null;
        if (communicationType == 2) {
            currentPanelInstance = new CRInterfaceBySocketsImpl(client, socketClientPort);
        }
        return currentPanelInstance;
    }

        public static ImageAcquisitionDeviceInterface getCurrentPanelSingletonInstance(int communicationType, SocketClient client, int socketClientPort) {
        return getCurrentPanel(communicationType, client, socketClientPort);
    }

}
