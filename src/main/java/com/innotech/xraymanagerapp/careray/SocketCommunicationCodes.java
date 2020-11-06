/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.careray;

import java.io.Serializable;

/**
 *
 * @author Alexander Escobar L.
 */
public class SocketCommunicationCodes  implements Serializable{

    public static final String START = "<START>";//connectDetector() -> 0 = good.
    public static final String RAD = "<RAD>";//setContinueRad(true) -> 0 = good.
    public static final String RAD_RESPONSE = "started";
    public static final String GET_RAD = "<GETCONTINUERAD>";//getContinueRad() -> 1 = TRUE.
    public static final String KILL = "<KILL>";//setContinueRad(false) and disconnect panel -> 0 = good.
    public static final String STOP = "<STOP>";//setContinueRad(false) -> 0 = good.
    public static final String STOP_RESPONSE = "stopped";
    public static final String BATTERY = "<BATTERY>";//returns 1.0 -> 10% charged -> 0 = good.
    public static final String CONNECTION = "<CONNECTION>";//1 = wired, 2 = wireless, 1003 = panel disconnected. 
    public static final String TEMPC = "<TEMPC>";//celsius 29 degrees
    public static final String TEMPS = "<TEMPS>";//status. 0 = good, 1-4 = bad
    public static final String CALIB = "<CALIB>";//lookForCalibrationFiles() -> 0 = good -> files found.
    public static final String SIMULATE = "<SIM>";//simulateRadiation() -> 0 = good
    public static final String CURRENT_PROCESS_STATE = "<CPS>";//0 TO 8
    public static final String EXIT = "<EXIT>";//bootClient(false) and wait for a new client.
    public static final String ERROR = "<ERROR>";//bootClient(false) and wait for a new client.
    public static final String INFO = "<INFO>";//bootClient(false) and wait for a new client.
}
