/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.model.dicom;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 *
 * @author PC
 */
public class StudyTag {

    @SerializedName("00080005")
    @Expose
    private StudiesEndpointModel _00080005;

    @SerializedName("00080054")
    @Expose
    private StudiesEndpointModel _00080054;

    @SerializedName("00080056")
    @Expose
    private StudiesEndpointModel _00080056;

    @SerializedName("00080060")
    @Expose
    private StudiesEndpointModel _00080060;

    @SerializedName("0008103E")
    @Expose
    private StudiesEndpointModel _0008103E;

    @SerializedName("00081190")
    @Expose
    private StudiesEndpointModel _00081190;

    @SerializedName("0020000D")
    @Expose
    private StudiesEndpointModel _0020000D;

    @SerializedName("0020000E")
    @Expose
    private StudiesEndpointModel _0020000E;

    @SerializedName("00200011")
    @Expose
    private StudiesEndpointModel _00200011;

    @SerializedName("00201209")
    @Expose
    private StudiesEndpointModel _00201209;

    public StudiesEndpointModel get00080005() {
        return _00080005;
    }

    public void set00080005(StudiesEndpointModel _00080005) {
        this._00080005 = _00080005;
    }

    public StudiesEndpointModel get00080054() {
        return _00080054;
    }

    public void set00080054(StudiesEndpointModel _00080054) {
        this._00080054 = _00080054;
    }

    public StudiesEndpointModel get00080056() {
        return _00080056;
    }

    public void set00080056(StudiesEndpointModel _00080056) {
        this._00080056 = _00080056;
    }

    public StudiesEndpointModel get00080060() {
        return _00080060;
    }

    public void set00080060(StudiesEndpointModel _00080060) {
        this._00080060 = _00080060;
    }

    public StudiesEndpointModel get0008103E() {
        return _0008103E;
    }

    public void set0008103E(StudiesEndpointModel _0008103E) {
        this._0008103E = _0008103E;
    }

    public StudiesEndpointModel get00081190() {
        return _00081190;
    }

    public void set00081190(StudiesEndpointModel _00081190) {
        this._00081190 = _00081190;
    }

    public StudiesEndpointModel get0020000D() {
        return _0020000D;
    }

    public void set0020000D(StudiesEndpointModel _0020000D) {
        this._0020000D = _0020000D;
    }

    public StudiesEndpointModel get0020000E() {
        return _0020000E;
    }

    public void set0020000E(StudiesEndpointModel _0020000E) {
        this._0020000E = _0020000E;
    }

    public StudiesEndpointModel get00200011() {
        return _00200011;
    }

    public void set00200011(StudiesEndpointModel _00200011) {
        this._00200011 = _00200011;
    }

    public StudiesEndpointModel get00201209() {
        return _00201209;
    }

    public void set00201209(StudiesEndpointModel _00201209) {
        this._00201209 = _00201209;
    }
    
    
    
}
