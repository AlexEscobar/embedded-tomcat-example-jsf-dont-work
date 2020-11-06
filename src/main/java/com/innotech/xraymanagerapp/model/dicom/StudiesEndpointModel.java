/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.model.dicom;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author PC
 */
public class StudiesEndpointModel {

    private String vr;
    private List<String> Value;

    public String getVr() {
        return vr;
    }

    public void setVr(String vr) {
        this.vr = vr;
    }

    public List<String> getValue() {
        return Value;
    }

    public void setValue(List<String> Value) {
        this.Value = Value;
    }
    
    public static void main(String args[]){
        List<StudyTag> studyTagList = new ArrayList();
        StudyTag studyTag1 = new StudyTag();        
        
        StudiesEndpointModel st1= new StudiesEndpointModel();
        st1.setVr("UR");
        List<String> valuesList = new ArrayList();
        valuesList.add("http://server.dcmjs.org/dcm4chee-arc/aets/DCM4CHEE/rs/studies/1.2.826.0.13854362241694438965858641723883466450351448/series/1.2.826.0.62320398699870966852341067661756976932585472");
        st1.setValue(valuesList);
        studyTag1.set00080005(st1);
        
        StudiesEndpointModel st2= new StudiesEndpointModel();
        st2.setVr("AE");
        valuesList = new ArrayList();
        valuesList.add("DCM4IMICHEE");
        st2.setValue(valuesList);
        studyTag1.set00080054(st2);
        
        StudyTag studyTag2 = new StudyTag();        
        
        StudiesEndpointModel st3= new StudiesEndpointModel();
        st3.setVr("UR");
        valuesList = new ArrayList();
        valuesList.add("http://server.dcmjs.org/dcm4chee-arc/aets/DCM4CHEE/rs/studies/1.2.826.0.13854362241694438965858641723883466450351448/series/1.2.826.0.62320398699870966852341067661756976932585472");
        st3.setValue(valuesList);
        studyTag2.set00080005(st3);
        
        StudiesEndpointModel st4= new StudiesEndpointModel();
        st4.setVr("AE");
        valuesList = new ArrayList();
        valuesList.add("DCM4IMICHEE");
        st4.setValue(valuesList);
        studyTag2.set00080054(st4);
        
        
        List<StudiesEndpointModel> list = new ArrayList();
        studyTagList.add(studyTag1);
        studyTagList.add(studyTag2);
        String json = new Gson().toJson(studyTagList);
        
        System.out.println(json);
        
    }
}
