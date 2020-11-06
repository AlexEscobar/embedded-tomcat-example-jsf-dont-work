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
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author X-Ray
 */
@Entity
@Table(name = "Studies")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Studies.findAll", query = "SELECT s FROM Studies s ORDER BY s.creationDate DESC"),
    @NamedQuery(name = "Studies.findById", query = "SELECT s FROM Studies s WHERE s.id = :id"),
    @NamedQuery(name = "Studies.findByDescription", query = "SELECT s FROM Studies s WHERE s.description = :description"),
    @NamedQuery(name = "Studies.findByCreationDate", query = "SELECT s FROM Studies s WHERE s.creationDate = :creationDate"),
    @NamedQuery(name = "Studies.findByCloseDate", query = "SELECT s FROM Studies s WHERE s.closeDate = :closeDate"),
    @NamedQuery(name = "Studies.findByStatus", query = "SELECT s FROM Studies s WHERE s.status = :status"),
    @NamedQuery(name = "Studies.findByIsClosed", query = "SELECT s FROM Studies s WHERE s.isClosed = :isClosed"),
    @NamedQuery(name = "Studies.findByResultComment", query = "SELECT s FROM Studies s WHERE s.resultComment = :resultComment"),
    @NamedQuery(name = "Studies.findByCreationDateAndPatientId", query = "SELECT s FROM Studies s "
            + "WHERE s.creationDate BETWEEN :today AND :tomorrow AND s.patientId.id = :patientId AND  s.status = true"),
    @NamedQuery(name = "Studies.findByStatusAndPatientId", query = "SELECT s FROM Studies s"
            + " WHERE s.status = true AND s.patientId.id = :patientId ORDER BY s.creationDate DESC"),
    @NamedQuery(name = "Studies.findByStatusAndClient", query = "SELECT s FROM Studies s"
            + " WHERE s.status = :status AND s.userId.clinicId.id = :clinicId ORDER BY s.externalDate DESC"),// used only by the cloud viewer
    @NamedQuery(name = "Studies.findByStatusAndClientLocal", query = "SELECT s FROM Studies s"
            + " WHERE s.status = :status AND s.userId.clinicId.id = :clinicId ORDER BY s.creationDate DESC")})// used only by the local server


public class Studies implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "sqlite_Studies")
    @TableGenerator(name = "sqlite_Studies", table = "sqlite_sequence",
            pkColumnName = "name", valueColumnName = "seq",
            pkColumnValue = "Studies",
            initialValue = 1, allocationSize = 1)
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;
    @Size(max = 12)
    @Column(name = "ExternalDate")
    private String externalDate;
    @Size(max = 20)
    @Column(name = "ExternalTime")
    private String externalTime;

    @Size(max = 60)
    @Column(name = "ExternalId")
    private String externalId;
    @OneToMany(mappedBy = "studyId", fetch = FetchType.LAZY)
    private List<Images> imagesList;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "studies", fetch = FetchType.LAZY)
    private StudyAnnotationState studyAnnotationState;

    @Column(name = "Status")
    private Boolean status;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "studyId", fetch = FetchType.LAZY)
    private List<Annotations> annotationsList;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 500)
    @Column(name = "Description")
    private String description;
    
    @Column(name = "CreationDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;
    @Column(name = "CloseDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date closeDate;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "IsClosed")
    private boolean isClosed;
    @Size(max = 500)
    @Column(name = "ResultComment")
    private String resultComment;
    @Size(max = 64)
    @Column(name = "StudyInstanceUID")
    private String studyInstanceUID;
//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "studyId", fetch = FetchType.LAZY)
//    private List<Images> imagesList;
    @JoinColumn(name = "ModaliyId", referencedColumnName = "Id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Modalities modaliyId;
    @JoinColumn(name = "PatientId", referencedColumnName = "Id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private PetPatients patientId;
    @JoinColumn(name = "UserId", referencedColumnName = "Id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Users userId;

    public Studies() {
    }

    public Studies(Integer id) {
        this.id = id;
    }

    public Studies(Integer id, String description, Date creationDate, boolean status, boolean isClosed) {
        this.id = id;
        this.description = description;
        this.creationDate = creationDate;
        this.status = status;
        this.isClosed = isClosed;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(Date closeDate) {
        this.closeDate = closeDate;
    }


    public boolean getIsClosed() {
        return isClosed;
    }

    public void setIsClosed(boolean isClosed) {
        this.isClosed = isClosed;
    }

    public String getResultComment() {
        return resultComment;
    }

    public void setResultComment(String resultComment) {
        this.resultComment = resultComment;
    }
//
//    @XmlTransient
//    public List<Images> getImagesList() {
//        return imagesList;
//    }
//
//    public void setImagesList(List<Images> imagesList) {
//        this.imagesList = imagesList;
//    }

    public Modalities getModaliyId() {
        return modaliyId;
    }

    public void setModaliyId(Modalities modaliyId) {
        this.modaliyId = modaliyId;
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
        if (!(object instanceof Studies)) {
            return false;
        }
        Studies other = (Studies) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.innotech.xraymanagerapp.model.Studies[ id=" + id + " ]";
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    @XmlTransient
    public List<Annotations> getAnnotationsList() {
        return annotationsList;
    }

    public void setAnnotationsList(List<Annotations> annotationsList) {
        this.annotationsList = annotationsList;
    }

    public StudyAnnotationState getStudyAnnotationState() {
        return studyAnnotationState;
    }

    public void setStudyAnnotationState(StudyAnnotationState studyAnnotationState) {
        this.studyAnnotationState = studyAnnotationState;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    @XmlTransient
    public List<Images> getImagesList() {
        return imagesList;
    }

    public void setImagesList(List<Images> imagesList) {
        this.imagesList = imagesList;
    }

    public String getExternalDate() {
        return externalDate;
    }

    public void setExternalDate(String externalDate) {
        this.externalDate = externalDate;
    }

    public String getExternalTime() {
        return externalTime;
    }

    public void setExternalTime(String externalTime) {
        this.externalTime = externalTime;
    }

    public String getStudyInstanceUID() {
        return studyInstanceUID;
    }

    public void setStudyInstanceUID(String studyInstanceUID) {
        this.studyInstanceUID = studyInstanceUID;
    }
}
