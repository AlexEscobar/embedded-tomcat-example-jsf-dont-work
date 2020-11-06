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
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
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
@Table(name = "StudyAnnotationState")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "StudyAnnotationState.findAll", query = "SELECT s FROM StudyAnnotationState s")
    , @NamedQuery(name = "StudyAnnotationState.findByStudyId", query = "SELECT s FROM StudyAnnotationState s WHERE s.studyId = :studyId")
    , @NamedQuery(name = "StudyAnnotationState.findByState", query = "SELECT s FROM StudyAnnotationState s WHERE s.state = :state")
    , @NamedQuery(name = "StudyAnnotationState.findByCreationDate", query = "SELECT s FROM StudyAnnotationState s WHERE s.creationDate = :creationDate")
    , @NamedQuery(name = "StudyAnnotationState.findByStatus", query = "SELECT s FROM StudyAnnotationState s WHERE s.status = :status")})
public class StudyAnnotationState implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "StudyId")
    private Integer studyId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2147483647)
    @Column(name = "State")
    private String state;
    @Size(max = 2147483647)
    @Column(name = "ViewportAnnotations")
    private String ViewportAnnotations;
    @Basic(optional = false)
    @NotNull
    @Column(name = "CreationDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Status")
    private boolean status;
    @JoinColumn(name = "StudyId", referencedColumnName = "Id", insertable = false, updatable = false)
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    private Studies studies;
    @JoinColumn(name = "UserId", referencedColumnName = "Id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Users userId;

    public StudyAnnotationState() {
    }

    public StudyAnnotationState(Integer studyId) {
        this.studyId = studyId;
    }

    public StudyAnnotationState(Integer studyId, String state, Date creationDate, boolean status) {
        this.studyId = studyId;
        this.state = state;
        this.creationDate = creationDate;
        this.status = status;
    }

    public Integer getStudyId() {
        return studyId;
    }

    public void setStudyId(Integer studyId) {
        this.studyId = studyId;
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

    public Studies getStudies() {
        return studies;
    }

    public void setStudies(Studies studies) {
        this.studies = studies;
    }

    public Users getUserId() {
        return userId;
    }

    public void setUserId(Users userId) {
        this.userId = userId;
    }

    public String getViewportAnnotations() {
        return ViewportAnnotations;
    }

    public void setViewportAnnotations(String ViewportAnnotations) {
        this.ViewportAnnotations = ViewportAnnotations;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (studyId != null ? studyId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof StudyAnnotationState)) {
            return false;
        }
        StudyAnnotationState other = (StudyAnnotationState) object;
        if ((this.studyId == null && other.studyId != null) || (this.studyId != null && !this.studyId.equals(other.studyId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.innotech.xraymanagerapp.model.StudyAnnotationState[ studyId=" + studyId + " ]";
    }
    
}
