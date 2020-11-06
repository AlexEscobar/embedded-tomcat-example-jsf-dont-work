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
public class SeriesTag {

    @SerializedName("00020000")
    @Expose
    private StudiesEndpointModel _00020000;

    @SerializedName("00020002")
    @Expose
    private StudiesEndpointModel mediaStorageSOPClassUID;

    @SerializedName("00020003")
    @Expose
    private StudiesEndpointModel mediaStorageSOPInstanceUID;

    @SerializedName("00020010")
    @Expose
    private StudiesEndpointModel transferSyntaxUID;

    @SerializedName("00020012")
    @Expose
    private StudiesEndpointModel implementationClassUID;

    @SerializedName("00020013")
    @Expose
    private StudiesEndpointModel implementationVersionName;

    @SerializedName("00020016")
    @Expose
    private StudiesEndpointModel sourceApplicationEntityTitle;

    @SerializedName("00080008")
    @Expose
    private StudiesEndpointModel imageType;

    @SerializedName("00080014")
    @Expose
    private StudiesEndpointModel instanceCreatorUID;

    @SerializedName("00080016")
    @Expose
    private StudiesEndpointModel SOPClassUID;

    @SerializedName("00080018")
    @Expose
    private StudiesEndpointModel SOPInstanceUID;

    @SerializedName("00080020")
    @Expose
    private StudiesEndpointModel studyDate;

    @SerializedName("00080021")
    @Expose
    private StudiesEndpointModel seriesDate;

    @SerializedName("00080030")
    @Expose
    private StudiesEndpointModel studyTime;

    @SerializedName("00080031")
    @Expose
    private StudiesEndpointModel seriesTime;

    @SerializedName("00080050")
    @Expose
    private StudiesEndpointModel accessionNumber;

    @SerializedName("00080060")
    @Expose
    private StudiesEndpointModel modality;

    @SerializedName("00080070")
    @Expose
    private StudiesEndpointModel manufacturer;

    @SerializedName("00080080")
    @Expose
    private StudiesEndpointModel institutionName;

    @SerializedName("00081030")
    @Expose
    private StudiesEndpointModel studyDescription;

    @SerializedName("0008103E")
    @Expose
    private StudiesEndpointModel seriesDescription;

    @SerializedName("00100010")
    @Expose
    private StudiesEndpointModel patientName;

    @SerializedName("00100020")
    @Expose
    private StudiesEndpointModel patientId;

    @SerializedName("00100040")
    @Expose
    private StudiesEndpointModel patientSex;

    @SerializedName("00102201")
    @Expose
    private StudiesEndpointModel patientSpeciesDescription;

    @SerializedName("00102297")
    @Expose
    private StudiesEndpointModel responsiblePerson;

    @SerializedName("00102298")
    @Expose
    private StudiesEndpointModel responsiblePersonRole;

    @SerializedName("0020000D")
    @Expose
    private StudiesEndpointModel studyInstanceUID;

    @SerializedName("0020000E")
    @Expose
    private StudiesEndpointModel seriesInstanceUID;

    @SerializedName("00200010")
    @Expose
    private StudiesEndpointModel studyId;

    @SerializedName("00200011")
    @Expose
    private StudiesEndpointModel seriesNumber;

    @SerializedName("00280002")
    @Expose
    private StudiesEndpointModel samplesPerPixel;

    @SerializedName("00280004")
    @Expose
    private StudiesEndpointModel photometricInterpretation;

    @SerializedName("00280006")
    @Expose
    private StudiesEndpointModel planarConfiguration;

    @SerializedName("00280010")
    @Expose
    private StudiesEndpointModel rows;

    @SerializedName("00280011")
    @Expose
    private StudiesEndpointModel columns;

    @SerializedName("00280030")
    @Expose
    private StudiesEndpointModel pixelSpacing;

    @SerializedName("00280100")
    @Expose
    private StudiesEndpointModel bitsAllocated;

    @SerializedName("00280101")
    @Expose
    private StudiesEndpointModel bitsStored;

    @SerializedName("00280102")
    @Expose
    private StudiesEndpointModel highBit;

    @SerializedName("00280103")
    @Expose
    private StudiesEndpointModel pixelRepresentation;

    @SerializedName("00280301")
    @Expose
    private StudiesEndpointModel burnedInAnnotation;

    @SerializedName("00120071")
    @Expose
    private StudiesEndpointModel clinicalTrialSeriesID;

    public StudiesEndpointModel getClinicalTrialSeriesID() {
        return clinicalTrialSeriesID;
    }

    public void setClinicalTrialSeriesID(StudiesEndpointModel clinicalTrialSeriesID) {
        this.clinicalTrialSeriesID = clinicalTrialSeriesID;
    }

    public StudiesEndpointModel get00020000() {
        return _00020000;
    }

    public void set00020000(StudiesEndpointModel _00020000) {
        this._00020000 = _00020000;
    }

    public StudiesEndpointModel getMediaStorageSOPClassUID() {
        return mediaStorageSOPClassUID;
    }

    public void setMediaStorageSOPClassUID(StudiesEndpointModel mediaStorageSOPClassUID) {
        this.mediaStorageSOPClassUID = mediaStorageSOPClassUID;
    }

    public StudiesEndpointModel getMediaStorageSOPInstanceUID() {
        return mediaStorageSOPInstanceUID;
    }

    public void setMediaStorageSOPInstanceUID(StudiesEndpointModel mediaStorageSOPInstanceUID) {
        this.mediaStorageSOPInstanceUID = mediaStorageSOPInstanceUID;
    }

    public StudiesEndpointModel getTransferSyntaxUID() {
        return transferSyntaxUID;
    }

    public void setTransferSyntaxUID(StudiesEndpointModel transferSyntaxUID) {
        this.transferSyntaxUID = transferSyntaxUID;
    }

    public StudiesEndpointModel getImplementationClassUID() {
        return implementationClassUID;
    }

    public void setImplementationClassUID(StudiesEndpointModel implementationClassUID) {
        this.implementationClassUID = implementationClassUID;
    }

    public StudiesEndpointModel getImplementationVersionName() {
        return implementationVersionName;
    }

    public void setImplementationVersionName(StudiesEndpointModel implementationVersionName) {
        this.implementationVersionName = implementationVersionName;
    }

    public StudiesEndpointModel getSourceApplicationEntityTitle() {
        return sourceApplicationEntityTitle;
    }

    public void setSourceApplicationEntityTitle(StudiesEndpointModel sourceApplicationEntityTitle) {
        this.sourceApplicationEntityTitle = sourceApplicationEntityTitle;
    }

    public StudiesEndpointModel getImageType() {
        return imageType;
    }

    public void setImageType(StudiesEndpointModel imageType) {
        this.imageType = imageType;
    }

    public StudiesEndpointModel getInstanceCreatorUID() {
        return instanceCreatorUID;
    }

    public void setInstanceCreatorUID(StudiesEndpointModel instanceCreatorUID) {
        this.instanceCreatorUID = instanceCreatorUID;
    }

    public StudiesEndpointModel getSOPClassUID() {
        return SOPClassUID;
    }

    public void setSOPClassUID(StudiesEndpointModel SOPClassUID) {
        this.SOPClassUID = SOPClassUID;
    }

    public StudiesEndpointModel getSOPInstanceUID() {
        return SOPInstanceUID;
    }

    public void setSOPInstanceUID(StudiesEndpointModel SOPInstanceUID) {
        this.SOPInstanceUID = SOPInstanceUID;
    }

    public StudiesEndpointModel getStudyDate() {
        return studyDate;
    }

    public void setStudyDate(StudiesEndpointModel studyDate) {
        this.studyDate = studyDate;
    }

    public StudiesEndpointModel getSeriesDate() {
        return seriesDate;
    }

    public void setSeriesDate(StudiesEndpointModel seriesDate) {
        this.seriesDate = seriesDate;
    }

    public StudiesEndpointModel getStudyTime() {
        return studyTime;
    }

    public void setStudyTime(StudiesEndpointModel studyTime) {
        this.studyTime = studyTime;
    }

    public StudiesEndpointModel getSeriesTime() {
        return seriesTime;
    }

    public void setSeriesTime(StudiesEndpointModel seriesTime) {
        this.seriesTime = seriesTime;
    }

    public StudiesEndpointModel getAccessionNumber() {
        return accessionNumber;
    }

    public void setAccessionNumber(StudiesEndpointModel accessionNumber) {
        this.accessionNumber = accessionNumber;
    }

    public StudiesEndpointModel getModality() {
        return modality;
    }

    public void setModality(StudiesEndpointModel modality) {
        this.modality = modality;
    }

    public StudiesEndpointModel getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(StudiesEndpointModel manufacturer) {
        this.manufacturer = manufacturer;
    }

    public StudiesEndpointModel getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(StudiesEndpointModel institutionName) {
        this.institutionName = institutionName;
    }

    public StudiesEndpointModel getStudyDescription() {
        return studyDescription;
    }

    public void setStudyDescription(StudiesEndpointModel studyDescription) {
        this.studyDescription = studyDescription;
    }

    public StudiesEndpointModel getSeriesDescription() {
        return seriesDescription;
    }

    public void setSeriesDescription(StudiesEndpointModel seriesDescription) {
        this.seriesDescription = seriesDescription;
    }

    public StudiesEndpointModel getPatientName() {
        return patientName;
    }

    public void setPatientName(StudiesEndpointModel patientName) {
        this.patientName = patientName;
    }

    public StudiesEndpointModel getPatientId() {
        return patientId;
    }

    public void setPatientId(StudiesEndpointModel patientId) {
        this.patientId = patientId;
    }

    public StudiesEndpointModel getPatientSex() {
        return patientSex;
    }

    public void setPatientSex(StudiesEndpointModel patientSex) {
        this.patientSex = patientSex;
    }

    public StudiesEndpointModel getPatientSpeciesDescription() {
        return patientSpeciesDescription;
    }

    public void setPatientSpeciesDescription(StudiesEndpointModel patientSpeciesDescription) {
        this.patientSpeciesDescription = patientSpeciesDescription;
    }

    public StudiesEndpointModel getResponsiblePerson() {
        return responsiblePerson;
    }

    public void setResponsiblePerson(StudiesEndpointModel responsiblePerson) {
        this.responsiblePerson = responsiblePerson;
    }

    public StudiesEndpointModel getResponsiblePersonRole() {
        return responsiblePersonRole;
    }

    public void setResponsiblePersonRole(StudiesEndpointModel responsiblePersonRole) {
        this.responsiblePersonRole = responsiblePersonRole;
    }

    public StudiesEndpointModel getStudyInstanceUID() {
        return studyInstanceUID;
    }

    public void setStudyInstanceUID(StudiesEndpointModel studyInstanceUID) {
        this.studyInstanceUID = studyInstanceUID;
    }

    public StudiesEndpointModel getSeriesInstanceUID() {
        return seriesInstanceUID;
    }

    public void setSeriesInstanceUID(StudiesEndpointModel seriesInstanceUID) {
        this.seriesInstanceUID = seriesInstanceUID;
    }

    public StudiesEndpointModel getStudyId() {
        return studyId;
    }

    public void setStudyId(StudiesEndpointModel studyId) {
        this.studyId = studyId;
    }

    public StudiesEndpointModel getSamplesPerPixel() {
        return samplesPerPixel;
    }

    public void setSamplesPerPixel(StudiesEndpointModel samplesPerPixel) {
        this.samplesPerPixel = samplesPerPixel;
    }

    public StudiesEndpointModel getPhotometricInterpretation() {
        return photometricInterpretation;
    }

    public void setPhotometricInterpretation(StudiesEndpointModel photometricInterpretation) {
        this.photometricInterpretation = photometricInterpretation;
    }

    public StudiesEndpointModel getPlanarConfiguration() {
        return planarConfiguration;
    }

    public void setPlanarConfiguration(StudiesEndpointModel planarConfiguration) {
        this.planarConfiguration = planarConfiguration;
    }

    public StudiesEndpointModel getRows() {
        return rows;
    }

    public void setRows(StudiesEndpointModel rows) {
        this.rows = rows;
    }

    public StudiesEndpointModel getColumns() {
        return columns;
    }

    public void setColumns(StudiesEndpointModel columns) {
        this.columns = columns;
    }

    public StudiesEndpointModel getPixelSpacing() {
        return pixelSpacing;
    }

    public void setPixelSpacing(StudiesEndpointModel pixelSpacing) {
        this.pixelSpacing = pixelSpacing;
    }

    public StudiesEndpointModel getBitsAllocated() {
        return bitsAllocated;
    }

    public void setBitsAllocated(StudiesEndpointModel bitsAllocated) {
        this.bitsAllocated = bitsAllocated;
    }

    public StudiesEndpointModel getBitsStored() {
        return bitsStored;
    }

    public void setBitsStored(StudiesEndpointModel bitsStored) {
        this.bitsStored = bitsStored;
    }

    public StudiesEndpointModel getHighBit() {
        return highBit;
    }

    public void setHighBit(StudiesEndpointModel highBit) {
        this.highBit = highBit;
    }

    public StudiesEndpointModel getPixelRepresentation() {
        return pixelRepresentation;
    }

    public void setPixelRepresentation(StudiesEndpointModel pixelRepresentation) {
        this.pixelRepresentation = pixelRepresentation;
    }

    public StudiesEndpointModel getBurnedInAnnotation() {
        return burnedInAnnotation;
    }

    public void setBurnedInAnnotation(StudiesEndpointModel burnedInAnnotation) {
        this.burnedInAnnotation = burnedInAnnotation;
    }

    public StudiesEndpointModel getSeriesNumber() {
        return seriesNumber;
    }

    public void setSeriesNumber(StudiesEndpointModel seriesNumber) {
        this.seriesNumber = seriesNumber;
    }

}
