/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.controller.dicom;

/**
 *
 * @author Hi
 */
import com.innotech.xraymanagerapp.controller.dicom.test.TestResult;
import org.dcm4che3.data.Attributes;

import java.util.ArrayList;

/**
 * @author Umberto Cappellini <umberto.cappellini@agfa.com>
 * @author Hesham elbadawi <bsdreko@gmail.com>
 */
public class StoreResult implements TestResult {

    private String testDescription;
    private String[] fileNames;
    private long size;
    private long time;
    private int filesSent;
    private int warnings;    
    private int failures;
    private ArrayList<Attributes> cStoreRSPAttributes;

    public  StoreResult(String testDescription, String[] fileNames, long size,
            long time, int filesSent, int warnings, int failures, ArrayList<Attributes> cmdRSP) {
        super();
        this.testDescription = testDescription;
        this.fileNames = fileNames;
        this.size = size;
        this.time = time;
        this.filesSent = filesSent;
        this.warnings = warnings;
        this.failures = failures;
        this.cStoreRSPAttributes = cmdRSP;
    }
    
    public  String getTestDescription() {
        return testDescription;
    }
    public  String[] getFileNames() {
        return fileNames;
    }
    public  long getSize() {
        return size;
    }
    public  long getTime() {
        return time;
    }
    public  int getFilesSent() {
        return filesSent;
    }
    public  int getWarnings() {
        return warnings;
    }
    public  int getFailures() {
        return failures;
    }

    public  ArrayList<Attributes> getcStoreRSPAttributes() {
        return cStoreRSPAttributes;
    }

}
