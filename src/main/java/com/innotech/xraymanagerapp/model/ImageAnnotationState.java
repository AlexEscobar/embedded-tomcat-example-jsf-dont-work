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
@Table(name = "ImageAnnotationState")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ImageAnnotationState.findAll", query = "SELECT i FROM ImageAnnotationState i")
    , @NamedQuery(name = "ImageAnnotationState.findById", query = "SELECT i FROM ImageAnnotationState i WHERE i.id = :id")
    , @NamedQuery(name = "ImageAnnotationState.findByState", query = "SELECT i FROM ImageAnnotationState i WHERE i.state = :state")
    , @NamedQuery(name = "ImageAnnotationState.findByCreationDate", query = "SELECT i FROM ImageAnnotationState i WHERE i.creationDate = :creationDate")
    , @NamedQuery(name = "ImageAnnotationState.findByImageId", query = "SELECT i FROM ImageAnnotationState i WHERE i.imageId = :imageId AND i.status = true")
    , @NamedQuery(name = "ImageAnnotationState.findByStudyId", query = "SELECT i FROM ImageAnnotationState i WHERE i.imageId.annotationsId.studyId.id = :studyId AND i.status = true")
    , @NamedQuery(name = "ImageAnnotationState.findByStatus", query = "SELECT i FROM ImageAnnotationState i WHERE i.status = :status")})
public class ImageAnnotationState implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "sqlite_ImageAnnotationState")
    @TableGenerator(name = "sqlite_ImageAnnotationState", table = "sqlite_sequence",
            pkColumnName = "name", valueColumnName = "seq",
            pkColumnValue = "ImageAnnotationState",
            initialValue = 1, allocationSize = 1)
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2147483647)
    @Column(name = "State")
    private String state;
    @Basic(optional = false)
    @NotNull
    @Column(name = "CreationDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Status")
    private boolean status;
    @JoinColumn(name = "ImageId", referencedColumnName = "Id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Images imageId;
    @JoinColumn(name = "UserId", referencedColumnName = "Id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Users userId;

    public ImageAnnotationState() {
    }

    public ImageAnnotationState(Integer id) {
        this.id = id;
    }

    public ImageAnnotationState(Integer id, String state, Date creationDate, boolean status) {
        this.id = id;
        this.state = state;
        this.creationDate = creationDate;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Images getImageId() {
        return imageId;
    }

    public void setImageId(Images imageId) {
        this.imageId = imageId;
    }

    public Users getUserId() {
        return userId;
    }

    public void setUserId(Users userId) {
        this.userId = userId;
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
        if (!(object instanceof ImageAnnotationState)) {
            return false;
        }
        ImageAnnotationState other = (ImageAnnotationState) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.innotech.xraymanagerapp.bean.ImageAnnotationState[ id=" + id + " ]";
    }
    
}
