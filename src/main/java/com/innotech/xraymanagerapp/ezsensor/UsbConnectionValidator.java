/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.ezsensor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the necessary methods to check whether the sensor is
 * physically connected to the USB port or not
 *
 * @author Alexander Escobar
 */
public class UsbConnectionValidator {

    private static final String EZ_SENSOR_COMMAND = "wmic path Win32_PnPEntity where \"PNPDeviceID like '%0547%'\" get PNPDeviceID";
    private static final String HDR_SENSOR_COMMAND = "wmic path Win32_PnPEntity where \"PNPDeviceID like '%04B4%'\" get PNPDeviceID";
    private static final String EZ_SENSOR_COMMAND_RESPONSE = "USB\\VID_0547";
    private static final String HDR_SENSOR_COMMAND_RESPONSE = "USB\\VID_04B4";

    /**
     * Uses the windows CMD to check whether the EZ sensor is physically
     * connected to the USB port or not
     *
     * @return true if connected false otherwise
     */
    public static boolean checkEZSensorUsbConnection() {
        return checkUsbConnection(EZ_SENSOR_COMMAND, EZ_SENSOR_COMMAND_RESPONSE);
    }

    /**
     * Uses the windows CMD to check whether the EZ sensor is physically
     * connected to the USB port or not
     *
     * @return true if connected false otherwise
     */
    public static boolean checkHDRSensorUsbConnection() {
        return checkUsbConnection(HDR_SENSOR_COMMAND, HDR_SENSOR_COMMAND_RESPONSE);
    }

    /**
     * Uses the windows CMD to check whether the EZ sensor is physically
     * connected to the USB port or not
     *
     * @param shellCommand The command that will be sent to the windows CMD
     * @param shellResponse The CMD response
     * @return true if connected false otherwise
     */
    public static boolean checkUsbConnection(String shellCommand, String shellResponse) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        // Windows
        String cmd = shellCommand;
        processBuilder.command("cmd.exe", "/c", cmd);
        boolean isConnected = false;
        try {
//            System.out.println("wait; SHELL COMMAND: "+shellCommand);
            Process process = processBuilder.start();

            BufferedReader reader
                    = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
//                System.out.println(line);
                if (line.contains(shellResponse)) {
//                    System.out.println("The sensor is connected... TRUE");
//                    process.destroy();
                    isConnected = true;
                    break;
                }
            }

            int exitCode = process.waitFor();
//            System.out.println("\nExited with error code : " + exitCode);

        } catch (IOException | InterruptedException e) {            
            Logger.getLogger(UsbConnectionValidator.class.getName()).log(Level.SEVERE, "Error checking the usb connection: {0}", e.getMessage());
        }
        return isConnected;
    }
    
    public static void main(String args[]){
        checkHDRSensorUsbConnection();
        checkEZSensorUsbConnection();
    }

}
