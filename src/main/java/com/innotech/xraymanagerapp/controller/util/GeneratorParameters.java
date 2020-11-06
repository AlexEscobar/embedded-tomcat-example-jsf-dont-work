/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.controller.util;

import java.util.Objects;

/**
 *
 * @author Alexander Escobar L.
 */
public class GeneratorParameters {

    private boolean pw;
    private Integer kv;
    private Double ma;
    private Double ms;
    private Double mx;
    private Integer fo;
    private String errorER;
    private String errorEL;
    private String errorEI;
    private String st;
    private String he;
    private String bv;

    public static GeneratorParameters singletonInstance;

    public GeneratorParameters() {
    }

    public GeneratorParameters(Integer kv) {
        this.kv = kv;
    }

    public GeneratorParameters(String he) {
        this.he = he;
    }

    public GeneratorParameters(boolean pw, Integer kv, Double ma, Double ms, Double mx, Integer fo, String he) {
        this.pw = pw;
        this.kv = kv;
        this.ma = ma;
        this.ms = ms;
        this.mx = mx;
        this.fo = fo;
        this.he = he;
    }

    public static GeneratorParameters getSingletonInstance() {
        if (Objects.isNull(singletonInstance)) {
            singletonInstance = new GeneratorParameters();
        }
        return singletonInstance;
    }

    public boolean isPw() {
        return pw;
    }

    public void setPw(boolean pw) {
        this.pw = pw;
    }

    public Integer getKv() {
        return kv;
    }

    public void setKv(Integer kv) {
        this.kv = kv;
    }

    public Double getMa() {
        return ma;
    }

    public void setMa(Double ma) {
        this.ma = ma;
    }

    public Double getMs() {
        return ms;
    }

    public void setMs(Double ms) {
        this.ms = ms;
    }

    public Double getMx() {
        return mx;
    }

    public void setMx(Double mx) {
        this.mx = mx;
    }

    public Integer getFo() {
        return fo;
    }

    public void setFo(Integer fo) {
        this.fo = fo;
    }

    public String getErrorER() {
        return errorER;
    }

    public void setErrorER(String errorER) {
        this.errorER = errorER;
    }

    public String getErrorEL() {
        return errorEL;
    }

    public void setErrorEL(String errorEL) {
        this.errorEL = errorEL;
    }

    public String getErrorEI() {
        return errorEI;
    }

    public void setErrorEI(String errorEI) {
        this.errorEI = errorEI;
    }

    public String getSt() {
        return st;
    }

    public void setSt(String st) {
        this.st = st;
    }

    public String getHe() {
        return he;
    }

    public void setHe(String he) {
        this.he = he;
    }

    public String getBv() {
        return bv;
    }

    public void setBv(String bv) {
        this.bv = bv;
    }

    @Override
    public String toString() {
        return kv + " " + ma + " " + ms + " " + mx;
    }
}
