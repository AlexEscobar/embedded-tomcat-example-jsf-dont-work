/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Alexander Escobar L.
 */
@Entity
@Table(name = "View_DicomTags")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ViewDicomTags.findAll", query = "SELECT v FROM ViewDicomTags v"),
    @NamedQuery(name = "ViewDicomTags.findById", query = "SELECT v FROM ViewDicomTags v WHERE v.id = :id"),
    @NamedQuery(name = "ViewDicomTags.findByPatientName", query = "SELECT v FROM ViewDicomTags v WHERE v.patientName = :patientName"),
    @NamedQuery(name = "ViewDicomTags.findByPatientId", query = "SELECT v FROM ViewDicomTags v WHERE v.patientId = :patientId"),
    @NamedQuery(name = "ViewDicomTags.findByPatientAccessionCode", query = "SELECT v FROM ViewDicomTags v WHERE v.patientAccessionCode = :patientAccessionCode"),
    @NamedQuery(name = "ViewDicomTags.findByPatientBirthdate", query = "SELECT v FROM ViewDicomTags v WHERE v.patientBirthdate = :patientBirthdate"),
    @NamedQuery(name = "ViewDicomTags.findByPatientSex", query = "SELECT v FROM ViewDicomTags v WHERE v.patientSex = :patientSex"),
    @NamedQuery(name = "ViewDicomTags.findByStudyDate", query = "SELECT v FROM ViewDicomTags v WHERE v.studyDate = :studyDate"),
    @NamedQuery(name = "ViewDicomTags.findByStudyId", query = "SELECT v FROM ViewDicomTags v WHERE v.studyId = :studyId"),
    @NamedQuery(name = "ViewDicomTags.findByStudyUID", query = "SELECT v FROM ViewDicomTags v WHERE v.studyInstanceUID = :studyUID"),
    @NamedQuery(name = "ViewDicomTags.findByStudyDescription", query = "SELECT v FROM ViewDicomTags v WHERE v.studyDescription = :studyDescription"),
    @NamedQuery(name = "ViewDicomTags.findBySeriesDescription", query = "SELECT v FROM ViewDicomTags v WHERE v.seriesDescription = :seriesDescription"),
    @NamedQuery(name = "ViewDicomTags.findBySeriesDate", query = "SELECT v FROM ViewDicomTags v WHERE v.seriesDate = :seriesDate"),
    @NamedQuery(name = "ViewDicomTags.findByAcquisitionDate", query = "SELECT v FROM ViewDicomTags v WHERE v.acquisitionDate = :acquisitionDate"),
    @NamedQuery(name = "ViewDicomTags.findByPatientSpecie", query = "SELECT v FROM ViewDicomTags v WHERE v.patientSpecie = :patientSpecie"),
    @NamedQuery(name = "ViewDicomTags.findByPatientBreed", query = "SELECT v FROM ViewDicomTags v WHERE v.patientBreed = :patientBreed"),
    @NamedQuery(name = "ViewDicomTags.findByOwnerName", query = "SELECT v FROM ViewDicomTags v WHERE v.ownerName = :ownerName")})
public class ViewDicomTags implements Serializable {

//    @Size(max = 16)
//    @Column(name = "ColumnPixelSpacing")
//    private String columnPixelSpacing;
//    @Size(max = 16)
//    @Column(name = "RowPixelSpacing")
//    private String rowPixelSpacing;
    @Column(name = "ImageRows")
    private short imageRows;

    @Column(name = "ImageColumns")
    private short ImageColumns;

    @Column(name = "PixelSpacing")
    private String PixelSpacing;

    @Column(name = "ClinicAddress")
    private String clinicAddress;

    @Column(name = "ClinicPhoneNumber")
    private String clinicPhoneNumber;

    @Column(name = "ClinicFaxNumber")
    private String clinicFaxNumber;

    @Column(name = "ImagePattern")
    private String imagePattern;
    
    @Column(name = "ImagePath")
    private String imagePath;

    @Column(name = "SeriesInstanceUID")
    private String seriesInstanceUID;
    
    @Column(name = "SOPInstanceUID")
    private String sOPInstanceUID;
    
    @Column(name = "StudyInstanceUID")
    private String studyInstanceUID;
    
    @Column(name = "DicomTags")
    private String dicomTags;

    private static final long serialVersionUID = 1L;
    @Column(name = "Id")
    @Id
    private int id;
    
    @Column(name = "PatientName")
    private String patientName;
    
    @Column(name = "PatientId")
    private String patientId;
    
    @Column(name = "PatientAccessionCode")
    private String patientAccessionCode;
    
    @Column(name = "PatientBirthdate")
    @Temporal(TemporalType.DATE)
    private Date patientBirthdate;
    
    @Column(name = "PatientSex")
    private String patientSex;
    
    @Column(name = "StudyDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date studyDate;
    
    @Column(name = "StudyId")
    private int studyId;
    
    @Column(name = "StudyDescription")
    private String studyDescription;
    
    @Column(name = "SeriesDescription")
    private String seriesDescription;
    
    @Column(name = "SeriesDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date seriesDate;
    
    @Column(name = "AcquisitionDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date acquisitionDate;
    
    @Column(name = "PatientSpecie")
    private String patientSpecie;
    
    @Column(name = "PatientBreed")
    private String patientBreed;
    
    @Column(name = "OwnerName")
    private String ownerName;
    

    @Column(name = "InstitutionName")
    private String institutionName;
    
    @Column(name = "FullJsonString")
    private String fullJsonString;

    public ViewDicomTags() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getPatientAccessionCode() {
        return patientAccessionCode;
    }

    public void setPatientAccessionCode(String patientAccessionCode) {
        this.patientAccessionCode = patientAccessionCode;
    }

    public Date getPatientBirthdate() {
        return patientBirthdate;
    }

    public void setPatientBirthdate(Date patientBirthdate) {
        this.patientBirthdate = patientBirthdate;
    }

    public String getPatientSex() {
        return patientSex;
    }

    public void setPatientSex(String patientSex) {
        this.patientSex = patientSex;
    }

    public Date getStudyDate() {
        return studyDate;
    }

    public void setStudyDate(Date studyDate) {
        this.studyDate = studyDate;
    }

    public int getStudyId() {
        return studyId;
    }

    public void setStudyId(int studyId) {
        this.studyId = studyId;
    }

    public String getStudyDescription() {
        return studyDescription;
    }

    public void setStudyDescription(String studyDescription) {
        this.studyDescription = studyDescription;
    }

    public String getSeriesDescription() {
        return seriesDescription;
    }

    public void setSeriesDescription(String seriesDescription) {
        this.seriesDescription = seriesDescription;
    }

    public Date getSeriesDate() {
        return seriesDate;
    }

    public void setSeriesDate(Date seriesDate) {
        this.seriesDate = seriesDate;
    }

    public Date getAcquisitionDate() {
        return acquisitionDate;
    }

    public void setAcquisitionDate(Date acquisitionDate) {
        this.acquisitionDate = acquisitionDate;
    }

    public String getPatientSpecie() {
        return patientSpecie;
    }

    public void setPatientSpecie(String patientSpecie) {
        this.patientSpecie = patientSpecie;
    }

    public String getPatientBreed() {
        return patientBreed;
    }

    public void setPatientBreed(String patientBreed) {
        this.patientBreed = patientBreed;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public String getFullJsonString() {
        return fullJsonString;
    }

    public void setFullJsonString(String FullJsonString) {
        this.fullJsonString = FullJsonString;
    }

    @Override
    public String toString() {
        return patientId + " - "
                + patientAccessionCode + " - "
                +//null
                patientName + " - "
                + //patientAge+" - "+
                patientSex + " - "
                + patientSpecie + " - "
                +//null
                patientBirthdate + " - "
                +//NULL METRON
                ownerName + " - "
                +// null
                studyId + " - "
                +//0
                studyInstanceUID + " - "
                +//null //NULL METRON
                id + " - "
                +// 0
                sOPInstanceUID + " - "
                +//null //NULL METRON
                studyDate + " - "
                +//NULL METRON
                seriesDate + " - "
                +//NULL METRON
                seriesDescription + " - "
                +//null
                studyDescription + " - "
                +//NULL METRON
                seriesInstanceUID + " - "
                +//null
                institutionName + " - "
                + fullJsonString;
    }

    public String getSeriesInstanceUID() {
        return seriesInstanceUID;
    }

    public void setSeriesInstanceUID(String seriesInstanceUID) {
        this.seriesInstanceUID = seriesInstanceUID;
    }

    public String getSOPInstanceUID() {
        return sOPInstanceUID;
    }

    public void setSOPInstanceUID(String sOPInstanceUID) {
        this.sOPInstanceUID = sOPInstanceUID;
    }

    public String getStudyInstanceUID() {
        return studyInstanceUID;
    }

    public void setStudyInstanceUID(String studyInstanceUID) {
        this.studyInstanceUID = studyInstanceUID;
    }

    public String getImagePattern() {
        return imagePattern;
    }

    public void setImagePattern(String imagePattern) {
        this.imagePattern = imagePattern;
    }

    public String getsOPInstanceUID() {
        return sOPInstanceUID;
    }

    public void setsOPInstanceUID(String sOPInstanceUID) {
        this.sOPInstanceUID = sOPInstanceUID;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getDicomTags() {
        return dicomTags;
    }

    public void setDicomTags(String dicomTags) {
        this.dicomTags = dicomTags;
    }

    public String getClinicPhoneNumber() {
        return clinicPhoneNumber;
    }

    public void setClinicPhoneNumber(String clinicPhoneNumber) {
        this.clinicPhoneNumber = clinicPhoneNumber;
    }

    public String getClinicFaxNumber() {
        return clinicFaxNumber;
    }

    public void setClinicFaxNumber(String clinicFaxNumber) {
        this.clinicFaxNumber = clinicFaxNumber;
    }

    public String getClinicAddress() {
        return clinicAddress;
    }

    public void setClinicAddress(String clinicAddress) {
        this.clinicAddress = clinicAddress;
    }

    public String getPixelSpacing() {
        return PixelSpacing;
    }

    public void setPixelSpacing(String PixelSpacing) {
        this.PixelSpacing = PixelSpacing;
    }

    public short getImageRows() {
        return imageRows;
    }

    public void setImageRows(short imageRows) {
        this.imageRows = imageRows;
    }

    public short getImageColumns() {
        return ImageColumns;
    }

    public void setImageColumns(short ImageColumns) {
        this.ImageColumns = ImageColumns;
    }

}
