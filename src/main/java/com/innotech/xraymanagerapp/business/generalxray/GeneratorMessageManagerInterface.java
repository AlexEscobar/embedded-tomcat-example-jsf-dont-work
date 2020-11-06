/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.business.generalxray;

import jssc.SerialPortException;

/**
 * Stablish communication with a generator and sends/receives messages from/to the generator.
 * @author Alexander Escobar L.
 */
public interface GeneratorMessageManagerInterface {
    
    public boolean openPort() throws SerialPortException;
    public boolean closePort() throws SerialPortException;
    public void sendCommandToGenerator(String command) throws SerialPortException;
    public void sendSetupCommandToGenerator(int et, int kv, double ma, double ms, double mx, int fo, int fs, int fi, int fn) throws SerialPortException;
    public void addEventListener() throws SerialPortException;
    public boolean openConnectionWithGenerator();
    public String getGeneratorResponse();
    public String buildCommand(String command);
    public GeneratorCommandsInterface getCommands();
    public GeneratorResponseInterface getResponse();
    public GeneratorMessageManagerInterface getInstance(AbstractGeneratorMessageListener generatorMessageListener, String comPortNumber) throws SerialPortException ;
//    public Queue<String> getGeneratorResponseList();
}
