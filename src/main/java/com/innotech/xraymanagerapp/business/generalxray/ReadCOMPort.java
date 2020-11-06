/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.business.generalxray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alexander Escobar L.
 */
public class ReadCOMPort {
    
    
//  Alternative command:  private static final String CHECK_COM_PORT_COMMAND = "(Get-WmiObject -query \"SELECT * FROM Win32_PnPEntity\" | Where {$_.Name -Match \"COM\\d+\"}).name";
    private static final String CHECK_COM_PORT_COMMAND = "wmic path Win32_PnPEntity where \"Name like '%COM%'\" get NAME";
    private static final String expectedResponse = "Communications Port ";

   public static boolean checkConnection(String comPort){
       String response = new StringBuilder(expectedResponse).append("(").append(comPort).append(")").toString();
       return isGeneratorConnected(CHECK_COM_PORT_COMMAND, response);
   }
    
    /**
     * Uses the windows CMD to check whether the High Voltage Generator is physically
     * connected to the USB port or not
     *
     * @param shellCommand The command that will be sent to the windows CMD
     * @param shellResponse The CMD response
     * @return true if connected false otherwise
     */
    public static boolean isGeneratorConnected(String shellCommand, String shellResponse) {
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
//                    System.out.println("The GENERATOR is connected... TRUE");
//                    process.destroy();
                    isConnected = true;
                    break;
                }
            }

            int exitCode = process.waitFor();
//            System.out.println("\nExited with error code : " + exitCode);

        } catch (IOException | InterruptedException e) {            
            Logger.getLogger(ReadCOMPort.class.getName()).log(Level.SEVERE, "Error checking the COM Port: Error: {0}", new Object[]{e.getMessage()});
        }
        return isConnected;
    }
}
