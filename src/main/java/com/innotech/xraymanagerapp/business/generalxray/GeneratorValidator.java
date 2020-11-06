/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.business.generalxray;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import javax.ejb.Asynchronous;
import jssc.SerialPortException;

/**
 *
 * @author Alexander Escobar L.
 */
public class GeneratorValidator implements PropertyChangeListener, Serializable {

    private boolean isReady;
    private String kv;
    private String mx;
    private String ma;
    private String ms;
    private String pw;
    private final String comPortNumber;
    private final GeneratorMessageManagerInterface generatorMessageManager;
    public static GeneratorValidator singletonInstance;

    private GeneratorValidator(AbstractGeneratorMessageListener generatorMessageListenerInterface, GeneratorMessageManagerInterface generatorMessageManager, String comPortNumber) throws SerialPortException {
        this.comPortNumber = comPortNumber;
        this.generatorMessageManager = generatorMessageManager.getInstance(generatorMessageListenerInterface, comPortNumber);//call to the singleton instance of the interface
    }

    public static GeneratorValidator getInstance(AbstractGeneratorMessageListener generatorMessageListenerInterface, GeneratorMessageManagerInterface generatorMessageManager, String comPortNumber) throws SerialPortException {
        if (singletonInstance == null) {
            singletonInstance = new GeneratorValidator(generatorMessageListenerInterface, generatorMessageManager, comPortNumber);
        }
        return singletonInstance;
    }

    
    public boolean checkComPortConnection() {
        return ReadCOMPort.checkConnection(comPortNumber);
    }

    @Asynchronous
    public boolean turnOnGeneratorDevice() {
        isReady = generatorMessageManager.openConnectionWithGenerator();
        pw = generatorMessageManager.getResponse().getPowerResponse();
        return isReady;
    }

    @Asynchronous
    public boolean turnOffGeneratorDevice() throws SerialPortException {
        generatorMessageManager.sendCommandToGenerator(generatorMessageManager.getCommands().getPowerOffCommand());
        pw = generatorMessageManager.getResponse().getPowerResponse();
        return pw.contains("PW0");
    }

    @Asynchronous
    public void sendCurrentGeneratorStateCommand() throws SerialPortException {
        generatorMessageManager.sendCommandToGenerator(generatorMessageManager.getCommands().getGeneratorCurrentStateCommand());
    }

    @Asynchronous
    public void sendTubeAnodeHeatCommand() throws SerialPortException {
        generatorMessageManager.sendCommandToGenerator(generatorMessageManager.getCommands().getHEAT_TUBE_COMMAND("?"));
    }

    @Asynchronous
    public void sendBusVoltageCommand() throws SerialPortException {
        generatorMessageManager.sendCommandToGenerator(generatorMessageManager.getCommands().getBUS_VOLTAGE_COMMAND("?"));
    }

    @Asynchronous
    public void sendEchoCommand() throws SerialPortException {
        generatorMessageManager.sendCommandToGenerator(generatorMessageManager.getCommands().getECHO_COMMAND(""));
    }

    @Asynchronous
    public void sendPhaseStatusCommand() throws SerialPortException {
        generatorMessageManager.sendCommandToGenerator(generatorMessageManager.getCommands().getPHASE_STATUS_COMMAND("?"));
    }

    @Asynchronous
    public String getCurrentGeneratorState() throws SerialPortException {
//        Thread.sleep(5000);
        return new StringBuilder(generatorMessageManager.getResponse().getTechniqueResponse())
                .append(" ")
                .append(generatorMessageManager.getCommands().getKV_Command("?"))
                .append(" ")
                .append(kv = generatorMessageManager.getResponse().getKvResponse())
                .append(" ")
                .append(generatorMessageManager.getCommands().getMA_Command("?"))
                .append(" ")
                .append(ma = generatorMessageManager.getResponse().getMaResponse())
                .append(" ")
                .append(generatorMessageManager.getCommands().getMS_Command("?"))
                .append(" ")
                .append(ms = generatorMessageManager.getResponse().getMsResponse())
                .append(" ")
                .append(generatorMessageManager.getCommands().getMX_Command("?"))
                .append(" ")
                .append(mx = generatorMessageManager.getResponse().getMxResponse())
                .append(" ")
                .append(generatorMessageManager.getCommands().getPowerStateCommand())
                .append(" ")
                .append(pw = generatorMessageManager.getResponse().getPowerResponse())
                .toString();
    }

    @Asynchronous
    public void sendPowerStateCommand() throws SerialPortException {
        generatorMessageManager.sendCommandToGenerator(generatorMessageManager.getCommands().getPowerStateCommand());
    }

    @Asynchronous
    public void sendSetupCommand(int et, int kv, double ma, double ms, double mx, int fo, int fs, int fi, int fn) throws SerialPortException {
        generatorMessageManager.sendSetupCommandToGenerator(et, kv, ma, ms, mx, fo, fs, fi, fn);
    }

    @Asynchronous
    public void sendKvPlusCommand() throws SerialPortException {
        generatorMessageManager.sendCommandToGenerator(generatorMessageManager.getCommands().getKV_PlusCommand());
    }

    @Asynchronous
    public void sendKvMinusCommand() throws SerialPortException {
        generatorMessageManager.sendCommandToGenerator(generatorMessageManager.getCommands().getKV_MinusCommand());
    }

    @Asynchronous
    public void sendMAPlusCommand() throws SerialPortException {
        generatorMessageManager.sendCommandToGenerator(generatorMessageManager.getCommands().getMA_PlusCommand());
    }

    @Asynchronous
    public void sendMAMinusCommand() throws SerialPortException {
        generatorMessageManager.sendCommandToGenerator(generatorMessageManager.getCommands().getMA_MinusCommand());
    }

    @Asynchronous
    public void sendMSPlusCommand() throws SerialPortException {
        generatorMessageManager.sendCommandToGenerator(generatorMessageManager.getCommands().getMS_PlusCommand());
    }

    @Asynchronous
    public void sendMSMinusCommand() throws SerialPortException {
        generatorMessageManager.sendCommandToGenerator(generatorMessageManager.getCommands().getMS_MinusCommand());
    }

    @Asynchronous
    public void sendMXPlusCommand() throws SerialPortException {
        generatorMessageManager.sendCommandToGenerator(generatorMessageManager.getCommands().getMX_PlusCommand());
    }

    @Asynchronous
    public void sendMXMinusCommand() throws SerialPortException {
        generatorMessageManager.sendCommandToGenerator(generatorMessageManager.getCommands().getMX_MinusCommand());
    }

    @Asynchronous
    public void sendTechniqueCommand(Integer technique) throws SerialPortException {
        generatorMessageManager.sendCommandToGenerator(generatorMessageManager.getCommands().getET_Command(technique + ""));
    }

    public boolean getIsReady() {
        return isReady;
    }

    public String getKv() {
        return kv;
    }

    public String getMx() {
        return mx;
    }

    public String getMa() {
        return ma;
    }

    public String getMs() {
        return ms;
    }

    public String getPw() {
        pw = generatorMessageManager.getResponse().getPowerResponse();
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public boolean isIsReady() {
        return isReady;
    }

    public GeneratorMessageManagerInterface getGeneratorMessageManager() {
        return generatorMessageManager;
    }

    public void setKv(String kv) {
        this.kv = kv;
    }

    public void setMx(String mx) {
        this.mx = mx;
    }

    public void setMa(String ma) {
        this.ma = ma;
    }

    public void setMs(String ms) {
        this.ms = ms;
    }
}
