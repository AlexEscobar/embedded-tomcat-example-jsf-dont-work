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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Hi
 */
@Entity
@Table(name = "DicomSent")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DicomSent.findAll", query = "SELECT d FROM DicomSent d")
    , @NamedQuery(name = "DicomSent.findById", query = "SELECT d FROM DicomSent d WHERE d.id = :id")
    , @NamedQuery(name = "DicomSent.findByDicomTags", query = "SELECT d FROM DicomSent d WHERE d.dicomTags = :dicomTags")
    , @NamedQuery(name = "DicomSent.findByStartingDate", query = "SELECT d FROM DicomSent d WHERE d.startingDate = :startingDate")
    , @NamedQuery(name = "DicomSent.findBySuccessDate", query = "SELECT d FROM DicomSent d WHERE d.successDate = :successDate")
    , @NamedQuery(name = "DicomSent.findByState", query = "SELECT d FROM DicomSent d WHERE d.state = :state")
    , @NamedQuery(name = "DicomSent.findByStateDescription", query = "SELECT d FROM DicomSent d WHERE d.stateDescription = :stateDescription")
    , @NamedQuery(name = "DicomSent.findByStatus", query = "SELECT d FROM DicomSent d WHERE d.status = :status")})
public class DicomSent implements Serializable {

    private static final long serialVersionUID = 1L;
   
    @Id
    @GeneratedValue(generator = "sqlite_DicomSent")
    @TableGenerator(name = "sqlite_DicomSent", table = "sqlite_sequence",
            pkColumnName = "name", valueColumnName = "seq",
            pkColumnValue = "DicomSent",
            initialValue = 1, allocationSize = 1)
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;
    @Size(max = 4000)
    @Column(name = "DicomTags")
    private String dicomTags;
    @Basic(optional = false)
    @NotNull
    @Column(name = "StartingDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startingDate;
    @Column(name = "SuccessDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date successDate;
    @Basic(optional = false)
    @NotNull
    @Column(name = "State")
    private int state;
    @Size(max = 256)
    @Column(name = "StateDescription")
    private String stateDescription;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Status")
    private boolean status;
    @JoinColumn(name = "DicomServerId", referencedColumnName = "ServerId")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private DicomServers dicomServerId;
    @JoinColumn(name = "ImageId", referencedColumnName = "Id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Images imageId;

    public DicomSent() {
    }

    public DicomSent(Integer id) {
        this.id = id;
    }

    public DicomSent(Integer id, Date startingDate, int state, boolean status) {
        this.id = id;
        this.startingDate = startingDate;
        this.state = state;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDicomTags() {
        return dicomTags;
    }

    public void setDicomTags(String dicomTags) {
        this.dicomTags = dicomTags;
    }

    public Date getStartingDate() {
        return startingDate;
    }

    public void setStartingDate(Date startingDate) {
        this.startingDate = startingDate;
    }

    public Date getSuccessDate() {
        return successDate;
    }

    public void setSuccessDate(Date successDate) {
        this.successDate = successDate;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getStateDescription() {
        return stateDescription;
    }

    public void setStateDescription(String stateDescription) {
        this.stateDescription = stateDescription;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public DicomServers getDicomServerId() {
        return dicomServerId;
    }

    public void setDicomServerId(DicomServers dicomServerId) {
        this.dicomServerId = dicomServerId;
    }

    public Images getImageId() {
        return imageId;
    }

    public void setImageId(Images imageId) {
        this.imageId = imageId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DicomSent)) {
            return false;
        }
        DicomSent other = (DicomSent) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.innotech.xraymanagerapp.model.DicomSent[ id=" + id + " ]";
    }
    
}
