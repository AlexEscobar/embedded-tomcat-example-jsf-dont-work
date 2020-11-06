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
public interface GeneratorCommandsInterface {

    public String getGeneratorModel();

    public String getPowerOnCommand();

    public String getPowerOffCommand();

    public String getPowerStateCommand();

    public String getGeneratorCurrentStateCommand();

    public String getKV_PlusCommand();

    public String getKV_MinusCommand();

    public String getKV_Command(String data);

    public String getMA_PlusCommand();

    public String getMA_MinusCommand();

    public String getMA_Command(String data);

    public String getMS_PlusCommand();

    public String getMS_MinusCommand();

    public String getMS_Command(String data);

    public String getMX_PlusCommand();

    public String getMX_MinusCommand();

    public String getMX_Command(String data);

    public String getFO_Command(String data);

    public String getFS_Command(String data);

    public String getFI_Command(String data);

    public String getFN_Command(String data);

    public String getET_Command(String data);

    public String getSA_Command();

    public String getHEAT_TUBE_COMMAND(String data);

    public String getBUS_VOLTAGE_COMMAND(String data);

    public String getECHO_COMMAND(String data);

    public String getPHASE_STATUS_COMMAND(String data);
}
