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
 * @author X-Ray
 */
@Entity
@Table(name = "WorkList")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "WorkList.findAll", query = "SELECT w FROM WorkList w ORDER BY w.creationDate DESC"),
    @NamedQuery(name = "WorkList.findById", query = "SELECT w FROM WorkList w WHERE w.id = :id"),
    @NamedQuery(name = "WorkList.findByComments", query = "SELECT w FROM WorkList w WHERE w.comments = :comments"),
    @NamedQuery(name = "WorkList.findByCreationDate", query = "SELECT w FROM WorkList w WHERE w.creationDate = :creationDate"),
    @NamedQuery(name = "WorkList.findByOnprocessDate", query = "SELECT w FROM WorkList w WHERE w.onprocessDate = :onprocessDate"),
    @NamedQuery(name = "WorkList.findByDoneDate", query = "SELECT w FROM WorkList w WHERE w.doneDate = :doneDate"),
    @NamedQuery(name = "WorkList.findByCurrentProcess", query = "SELECT w FROM WorkList w WHERE w.currentProcess = :currentProcess"),
    @NamedQuery(name = "WorkList.findByStatus", query = "SELECT w FROM WorkList w WHERE w.status = :status"),
    @NamedQuery(name = "WorkList.findByClinicId", query = "SELECT w FROM WorkList w WHERE w.status = :status AND w.userId.clinicId.id = :clinicId"),
    @NamedQuery(name = "WorkList.findByStudyId", query = "SELECT w FROM WorkList w WHERE w.studyId = :studyId")})
public class WorkList implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "sqlite_WorkList")
    @TableGenerator(name = "sqlite_WorkList", table = "sqlite_sequence",
            pkColumnName = "name", valueColumnName = "seq",
            pkColumnValue = "WorkList",
            initialValue = 1, allocationSize = 1)
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;
    @Size(max = 500)
    @Column(name = "Comments")
    private String comments;
    
    @Column(name = "CreationDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;
    @Column(name = "OnprocessDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date onprocessDate;
    @Column(name = "DoneDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date doneDate;
    @Basic(optional = false)
    @NotNull
    @Column(name = "CurrentProcess")
    private short currentProcess;
    
    @Column(name = "Status")
    private boolean status;
    @Column(name = "StudyId")
    private Integer studyId;
    @JoinColumn(name = "PatientId", referencedColumnName = "Id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private PetPatients patientId;
    @JoinColumn(name = "UserId", referencedColumnName = "Id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Users userId;

    public WorkList() {
    }

    public WorkList(Integer id) {
        this.id = id;
    }

    public WorkList(Integer id, Date creationDate, short currentProcess, boolean status) {
        this.id = id;
        this.creationDate = creationDate;
        this.currentProcess = currentProcess;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getOnprocessDate() {
        return onprocessDate;
    }

    public void setOnprocessDate(Date onprocessDate) {
        this.onprocessDate = onprocessDate;
    }

    public Date getDoneDate() {
        return doneDate;
    }

    public void setDoneDate(Date doneDate) {
        this.doneDate = doneDate;
    }

    public short getCurrentProcess() {
        return currentProcess;
    }

    public void setCurrentProcess(short currentProcess) {
        this.currentProcess = currentProcess;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Integer getStudyId() {
        return studyId;
    }

    public void setStudyId(Integer studyId) {
        this.studyId = studyId;
    }

    public PetPatients getPatientId() {
        return patientId;
    }

    public void setPatientId(PetPatients patientId) {
        this.patientId = patientId;
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
        if (!(object instanceof WorkList)) {
            return false;
        }
        WorkList other = (WorkList) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.innotech.xraymanagerapp.model.WorkList[ id=" + id + " ]";
    }
    
}
