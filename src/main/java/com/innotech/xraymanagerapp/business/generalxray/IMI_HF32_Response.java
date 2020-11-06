/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.business.generalxray;

/**
 * Holds the last responses of the generator
 *
 * @author Alexander Escobar L.
 */
public class IMI_HF32_Response implements GeneratorResponseInterface {

    public static String powerResponse = "";
    public static String kvResponse = "";
    public static String maResponse = "";
    public static String msResponse = "";
    public static String mxResponse = "";
    public static String techniqueResponse = "";//ET - Exposure Technique Selection
    public static String focusResponse = "";// FO - Focus Selection
    public static String fnResponse = "";// AEC Density
    public static String fsResponse = "";// AEC Film Screen Selection
    public static String fiResponse = "";// AEC Field Selection

    @Override
    public String getKvResponse() {
        return kvResponse;
    }

    public static void setKvResponse(String aKvResponse) {
        kvResponse = aKvResponse;
    }

    @Override
    public String getMaResponse() {
        return maResponse;
    }

    public static void setMaResponse(String aMaResponse) {
        maResponse = aMaResponse;
    }

    @Override
    public String getMsResponse() {
        return msResponse;
    }

    public static void setMsResponse(String aMsResponse) {
        msResponse = aMsResponse;
    }

    @Override
    public String getMxResponse() {
        return mxResponse;
    }

    public static void setMxResponse(String aMxResponse) {
        mxResponse = aMxResponse;
    }

    @Override
    public String getPowerResponse() {
        return powerResponse;
    }

    public static void setPowerResponse(String aPowerResponse) {
        powerResponse = aPowerResponse;
    }

    @Override
    public String getTechniqueResponse() {
        return techniqueResponse;
    }

    public static void setTechniqueResponse(String aTechniqueResponse) {
        techniqueResponse = aTechniqueResponse;
    }

    @Override
    public String getFocusResponse() {
        return focusResponse;
    }

    public static void setFocusResponse(String aFocusResponse) {
        focusResponse = aFocusResponse;
    }

    @Override
    public String getFnResponse() {
        return fnResponse;
    }

    public static void setFnResponse(String aFnResponse) {
        fnResponse = aFnResponse;
    }

    @Override
    public String getFsResponse() {
        return fsResponse;
    }

    public static void setFsResponse(String aFsResponse) {
        fsResponse = aFsResponse;
    }

    @Override
    public String getFiResponse() {
        return fiResponse;
    }

    public static void setFiResponse(String aFiResponse) {
        fiResponse = aFiResponse;
    }

    public static void print() {
        System.out.println(
                new StringBuilder("[PW?: ")
                .append(powerResponse)
                .append("] - [KV?: ")
                .append(kvResponse)
                .append("] - [MA?: ")
                .append(maResponse)
                .append("] - [MS?: ")
                .append(msResponse)
                .append("] - [MX?: ")
                .append(mxResponse)
                .append("] - [FO?: ")
                .append(focusResponse)
                .append("]")
                .toString()
        );
    }
}
