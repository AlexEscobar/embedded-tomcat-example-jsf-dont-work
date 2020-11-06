/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.business.generalxray;

/**
 *
 * @author Alexander Escobar L.
 */
public class IMI_HF32_Commands implements GeneratorCommandsInterface {

    public static final String GENERATOR_MODEL = "IMI HF 32";
    public static final String POWER_ON_COMMAND = "PW1";
    public static final String POWER_OFF_COMMAND = "PW0";
    public static final String POWER_STATE_COMMAND = "PW?";
    public static final String CURRENT_STATE_COMMAND = "GS?";
    public static final String KV_PLUS_COMMAND = "KV+";
    public static final String KV_MINUS_COMMAND = "KV-";
    public static final String KV_COMMAND = "KV";
    public static final String MA_PLUS_COMMAND = "MA+";
    public static final String MA_MINUS_COMMAND = "MA-";
    public static final String MA_COMMAND = "MA";
    public static final String MS_PLUS_COMMAND = "MS+";
    public static final String MS_MINUS_COMMAND = "MS-";
    public static final String MS_COMMAND = "MS";
    public static final String MX_PLUS_COMMAND = "MX+";
    public static final String MX_MINUS_COMMAND = "MX-";
    public static final String MX_COMMAND = "MX";

    public static final String SA_COMMAND = "SA";//command to set up the generator
    public static final String FO_COMMAND = "FO";
    public static final String FS_COMMAND = "FS";
    public static final String FI_COMMAND = "FI";
    public static final String FN_COMMAND = "FN";
    public static final String ET_COMMAND = "ET";
    public static final String HEAT_TUBE_COMMAND = "HE";// command to check Anode Tube Heat
    public static final String BUS_VOLTAGE_COMMAND = "BV";// command to check Bus Voltage value
    public static final String ECHO_COMMAND = "EC";// Request Generator to respond with EC Again. this is helpful to check the physical connection
    private static final String PHASE_STATUS_COMMAND = "ST";// Request Generator to respond with EC Again. this is helpful to check the physical connection

    @Override
    public String getGeneratorModel() {
        return GENERATOR_MODEL;
    }

    @Override
    public String getPowerOnCommand() {
        return POWER_ON_COMMAND;
    }

    @Override
    public String getPowerOffCommand() {
        return POWER_OFF_COMMAND;
    }

    @Override
    public String getPowerStateCommand() {
        return POWER_STATE_COMMAND;
    }

    @Override
    public String getGeneratorCurrentStateCommand() {
        return CURRENT_STATE_COMMAND;
    }

    @Override
    public String getKV_PlusCommand() {
        return KV_PLUS_COMMAND;
    }

    @Override
    public String getKV_MinusCommand() {
        return KV_MINUS_COMMAND;
    }

    @Override
    public String getKV_Command(String data) {
        return KV_COMMAND + data;
    }

    @Override
    public String getMA_PlusCommand() {
        return MA_PLUS_COMMAND;
    }

    @Override
    public String getMA_MinusCommand() {
        return MA_MINUS_COMMAND;
    }

    @Override
    public String getMA_Command(String data) {
        return MA_COMMAND + data;
    }

    @Override
    public String getMS_PlusCommand() {
        return MS_PLUS_COMMAND;
    }

    @Override
    public String getMS_MinusCommand() {
        return MS_MINUS_COMMAND;
    }

    @Override
    public String getMS_Command(String data) {
        return MS_COMMAND + data;
    }

    @Override
    public String getMX_PlusCommand() {
        return MX_PLUS_COMMAND;
    }

    @Override
    public String getMX_MinusCommand() {
        return MX_MINUS_COMMAND;
    }

    @Override
    public String getMX_Command(String data) {
        return MX_COMMAND + data;
    }

    @Override
    public String getSA_Command() {
        return SA_COMMAND;
    }

    @Override
    public String getFO_Command(String data) {
        return FO_COMMAND + data;
    }

    @Override
    public String getFS_Command(String data) {
        return FS_COMMAND + data;
    }

    @Override
    public String getFI_Command(String data) {
        return FI_COMMAND + data;
    }

    @Override
    public String getFN_Command(String data) {
        return FN_COMMAND + data;
    }

    @Override
    public String getET_Command(String data) {
        return ET_COMMAND + data;
    }

    @Override
    public String getHEAT_TUBE_COMMAND(String data) {
        return HEAT_TUBE_COMMAND + data;
    }

    @Override
    public String getBUS_VOLTAGE_COMMAND(String data) {
        return BUS_VOLTAGE_COMMAND + data;
    }

    @Override
    public String getECHO_COMMAND(String data) {
        return ECHO_COMMAND + data;
    }
    
    @Override
    public String getPHASE_STATUS_COMMAND(String data) {
        return PHASE_STATUS_COMMAND + data;
    }
}
