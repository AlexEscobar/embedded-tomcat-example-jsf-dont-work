/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author X-Ray
 */
@Entity
@Table(name = "Annotations")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Annotations.findAll", query = "SELECT a FROM Annotations a"),
    @NamedQuery(name = "Annotations.findById", query = "SELECT a FROM Annotations a WHERE a.id = :id"),
    @NamedQuery(name = "Annotations.findByCreationDate", query = "SELECT a FROM Annotations a WHERE a.creationDate = :creationDate"),
    @NamedQuery(name = "Annotations.updateCurrent", query = "UPDATE Annotations a SET a.isCurrent = 0 WHERE  a.isCurrent = 1"),
    @NamedQuery(name = "Annotations.findByStatus", query = "SELECT a FROM Annotations a WHERE a.status = :status"),
    @NamedQuery(name = "Annotations.findByStudy", query = "SELECT a FROM Annotations a WHERE a.studyId.id = :studyId AND a.status = true")})
public class Annotations implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "sqlite_Annotations")
    @TableGenerator(name = "sqlite_Annotations", table = "sqlite_sequence",
            pkColumnName = "name", valueColumnName = "seq",
            pkColumnValue = "Annotations",
            initialValue = 1, allocationSize = 1)
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;
    
    @JoinColumn(name = "AnimalSizeId", referencedColumnName = "Id")
    @ManyToOne
    private AnimalSizes animalSizeId;

    @JoinColumn(name = "BodyPartsId", referencedColumnName = "Id")
    @ManyToOne(fetch = FetchType.LAZY)
    private BodyParts bodyPartsId;
    @JoinColumn(name = "BodyPartViewId", referencedColumnName = "Id")
    @ManyToOne(fetch = FetchType.LAZY)
    private BodyPartViews bodyPartViewId;

    @Column(name = "IsDone")
    private Boolean isDone;

    @JoinColumn(name = "StudyId", referencedColumnName = "Id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Studies studyId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "annotationsId", fetch = FetchType.LAZY)
    private List<Images> imagesList;

    
    @Column(name = "CreationDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;
    
    @Column(name = "Status")
    private boolean status;
    @Column(name = "IsCurrent")
    private boolean isCurrent;
//    @JoinColumn(name = "ImageId", referencedColumnName = "Id")
//    @ManyToOne(optional = false, fetch = FetchType.LAZY)
//    private Images imageId;
    @JoinColumn(name = "TeethNumberId", referencedColumnName = "Id")
    @ManyToOne(fetch = FetchType.LAZY)
    private TeethNumbers teethNumberId;
    @JoinColumn(name = "UserId", referencedColumnName = "Id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Users userId;

    public Annotations() {
    }

    public Annotations(Integer id) {
        this.id = id;
    }

    public Annotations(Integer id, Date creationDate, boolean status) {
        this.id = id;
        this.creationDate = creationDate;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public boolean getIsCurrent() {
        return isCurrent;
    }

    public void setIsCurrent(boolean isCurrent) {
        this.isCurrent = isCurrent;
    }


    public TeethNumbers getTeethNumberId() {
        return teethNumberId;
    }

    public void setTeethNumberId(TeethNumbers teethNumberId) {
        this.teethNumberId = teethNumberId;
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
        if (!(object instanceof Annotations)) {
            return false;
        }
        Annotations other = (Annotations) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.innotech.xraymanagerapp.model.Annotations[ id=" + id + " ]";
    }

    public Studies getStudyId() {
        return studyId;
    }

    public void setStudyId(Studies studyId) {
        this.studyId = studyId;
    }

    @XmlTransient
    public List<Images> getImagesList() {
        return imagesList;
    }

    public void setImagesList(List<Images> imagesList) {
        this.imagesList = imagesList;
    }

    public Boolean getIsDone() {
        return isDone;
    }

    public void setIsDone(Boolean isDone) {
        this.isDone = isDone;
    }

    public BodyParts getBodyPartsId() {
        return bodyPartsId;
    }

    public void setBodyPartsId(BodyParts bodyPartsId) {
        this.bodyPartsId = bodyPartsId;
    }

    public BodyPartViews getBodyPartViewId() {
        return bodyPartViewId;
    }

    public void setBodyPartViewId(BodyPartViews bodyPartViewId) {
        this.bodyPartViewId = bodyPartViewId;
    }

    public AnimalSizes getAnimalSizeId() {
        return animalSizeId;
    }

    public void setAnimalSizeId(AnimalSizes animalSizeId) {
        this.animalSizeId = animalSizeId;
    }

}
