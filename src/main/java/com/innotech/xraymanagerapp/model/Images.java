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
@Table(name = "Images")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Images.findAll", query = "SELECT i FROM Images i"),
    @NamedQuery(name = "Images.findById", query = "SELECT i FROM Images i WHERE i.id = :id"),
    @NamedQuery(name = "Images.findByPattern", query = "SELECT i FROM Images i WHERE i.pattern = :pattern"),
    @NamedQuery(name = "Images.findByMimeType", query = "SELECT i FROM Images i WHERE i.mimeType = :mimeType"),
    @NamedQuery(name = "Images.findByCreationDate", query = "SELECT i FROM Images i WHERE i.creationDate = :creationDate"),
    @NamedQuery(name = "Images.findByStudyId", query = "SELECT i FROM Images i WHERE i.status = true AND i.studyId.id = :studyId ORDER BY i.id ASC"),
    @NamedQuery(name = "Images.findByStudyIdAnnotation", query = "SELECT i FROM Images i JOIN Annotations a "
            + "WHERE i.annotationsId.id = a.id AND i.status = true AND a.status = true AND a.studyId.id = :studyId  ORDER BY i.id ASC"),
    @NamedQuery(name = "Images.findByStatus", query = "SELECT i FROM Images i WHERE i.status = :status"),
    @NamedQuery(name = "Images.deleteById", query = "UPDATE Images i SET i.status = false WHERE i.id = :imageId"),
    @NamedQuery(name = "Images.activateById", query = "UPDATE Images i SET i.status = true WHERE i.id = :imageId")})
public class Images implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "sqlite_Images")
    @TableGenerator(name = "sqlite_Images", table = "sqlite_sequence",
            pkColumnName = "name", valueColumnName = "seq",
            pkColumnValue = "Images",
            initialValue = 1, allocationSize = 1)
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Column(name = "ImageRows")
    private short imageRows;

    @Column(name = "ImageColumns")
    private short ImageColumns;

    @Column(name = "IsRejected")
    private Boolean isRejected;

    @Size(max = 50)
    @Column(name = "OwnerName")
    private String ownerName;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "imageId", fetch = FetchType.LAZY)
    private List<ImageUNRejection> imageUNRejectionList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "imageId", fetch = FetchType.LAZY)
    private List<ImageRejection> imageRejectionList;

    @Size(max = 16)
    @Column(name = "ColumnPixelSpacing")
    private String columnPixelSpacing;
    @Size(max = 16)
    @Column(name = "RowPixelSpacing")
    private String rowPixelSpacing;

    @Size(max = 30)
    @Column(name = "SeriesDescription")
    private String seriesDescription;
    @Size(max = 6)
    @Column(name = "DicomTagsFormat")
    private String dicomTagsFormat;
    @Size(max = 2147483647)
    @Column(name = "DicomTags")
    private String dicomTags;
    @Size(max = 64)
    @Column(name = "SOPInstanceUID")
    private String sOPInstanceUID;
    @Size(max = 64)
    @Column(name = "SeriesInstanceUID")
    private String seriesInstanceUID;
    @Size(max = 64)
    @Column(name = "ImagePath")
    private String imagePath;
    @Size(max = 1)
    @Column(name = "ImageFile")
    private String imageFile;
    @JoinColumn(name = "StudyId", referencedColumnName = "Id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Studies studyId;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "imageId", fetch = FetchType.LAZY)
    private List<DicomSent> dicomSentList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "imageId", fetch = FetchType.LAZY)
    private List<ImageAnnotationState> imageAnnotationStateList;

    @Column(name = "Status")
    private Boolean status;
    @JoinColumn(name = "AnnotationsId", referencedColumnName = "Id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Annotations annotationsId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "Pattern")
    private String pattern;
    @Column(name = "MimeType")
    private String mimeType;

    @Column(name = "CreationDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

    @JoinColumn(name = "ModalityId", referencedColumnName = "Id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Modalities modalityId;
//    @JoinColumn(name = "StudyId", referencedColumnName = "Id")
//    @ManyToOne(optional = false, fetch = FetchType.LAZY)
//    private Studies studyId;
    @JoinColumn(name = "UserId", referencedColumnName = "Id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Users userId;
//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "imageId", fetch = FetchType.LAZY)
//    private List<Annotations> annotationsList;

    public Images() {
    }

    public Images(Integer id) {
        this.id = id;
    }

    public Images(Integer id, String pattern, Date creationDate, boolean status) {
        this.id = id;
        this.pattern = pattern;
        this.creationDate = creationDate;
        this.status = status;
    }

    public Images(Integer id, String pattern, Date creationDate, boolean status, Annotations annotationsId, Users userId) {
        this.id = id;
        this.pattern = pattern;
        this.creationDate = creationDate;
        this.status = status;
        this.annotationsId = annotationsId;
        this.userId = userId;
    }

    public Images(Integer id, String pattern, Date creationDate, boolean status, Annotations annotationsId, Users userId, Studies studyId) {
        this.id = id;
        this.pattern = pattern;
        this.creationDate = creationDate;
        this.status = status;
        this.annotationsId = annotationsId;
        this.userId = userId;
        this.studyId = studyId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Modalities getModalityId() {
        return modalityId;
    }

    public void setModalityId(Modalities modalityId) {
        this.modalityId = modalityId;
    }
//
//    public Studies getStudyId() {
//        return studyId;
//    }
//
//    public void setStudyId(Studies studyId) {
//        this.studyId = studyId;
//    }

    public Users getUserId() {
        return userId;
    }

    public void setUserId(Users userId) {
        this.userId = userId;
    }
//
//    @XmlTransient
//    public List<Annotations> getAnnotationsList() {
//        return annotationsList;
//    }
//
//    public void setAnnotationsList(List<Annotations> annotationsList) {
//        this.annotationsList = annotationsList;
//    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Images)) {
            return false;
        }
        Images other = (Images) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return pattern;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Annotations getAnnotationsId() {
        return annotationsId;
    }

    public void setAnnotationsId(Annotations annotationsId) {
        this.annotationsId = annotationsId;
    }

    @XmlTransient
    public List<ImageAnnotationState> getImageAnnotationStateList() {
        return imageAnnotationStateList;
    }

    public void setImageAnnotationStateList(List<ImageAnnotationState> imageAnnotationStateList) {
        this.imageAnnotationStateList = imageAnnotationStateList;
    }

    @XmlTransient
    public List<DicomSent> getDicomSentList() {
        return dicomSentList;
    }

    public void setDicomSentList(List<DicomSent> dicomSentList) {
        this.dicomSentList = dicomSentList;
    }

    public String getSeriesDescription() {
        return seriesDescription;
    }

    public void setSeriesDescription(String seriesDescription) {
        this.seriesDescription = seriesDescription;
    }

    public String getDicomTagsFormat() {
        return dicomTagsFormat;
    }

    public void setDicomTagsFormat(String dicomTagsFormat) {
        this.dicomTagsFormat = dicomTagsFormat;
    }

    public String getDicomTags() {
        return dicomTags;
    }

    public void setDicomTags(String dicomTags) {
        this.dicomTags = dicomTags;
    }

    public String getSOPInstanceUID() {
        return sOPInstanceUID;
    }

    public void setSOPInstanceUID(String sOPInstanceUID) {
        this.sOPInstanceUID = sOPInstanceUID;
    }

    public String getImageFile() {
        return imageFile;
    }

    public void setImageFile(String imageFile) {
        this.imageFile = imageFile;
    }

    public Studies getStudyId() {
        return studyId;
    }

    public void setStudyId(Studies studyId) {
        this.studyId = studyId;
    }

    public String getsOPInstanceUID() {
        return sOPInstanceUID;
    }

    public void setsOPInstanceUID(String sOPInstanceUID) {
        this.sOPInstanceUID = sOPInstanceUID;
    }

    public String getSeriesInstanceUID() {
        return seriesInstanceUID;
    }

    public void setSeriesInstanceUID(String seriesInstanceUID) {
        this.seriesInstanceUID = seriesInstanceUID;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getColumnPixelSpacing() {
        return columnPixelSpacing;
    }

    public void setColumnPixelSpacing(String columnPixelSpacing) {
        this.columnPixelSpacing = columnPixelSpacing;
    }

    public String getRowPixelSpacing() {
        return rowPixelSpacing;
    }

    public void setRowPixelSpacing(String rowPixelSpacing) {
        this.rowPixelSpacing = rowPixelSpacing;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    @XmlTransient
    public List<ImageUNRejection> getImageUNRejectionList() {
        return imageUNRejectionList;
    }

    public void setImageUNRejectionList(List<ImageUNRejection> imageUNRejectionList) {
        this.imageUNRejectionList = imageUNRejectionList;
    }

    @XmlTransient
    public List<ImageRejection> getImageRejectionList() {
        return imageRejectionList;
    }

    public void setImageRejectionList(List<ImageRejection> imageRejectionList) {
        this.imageRejectionList = imageRejectionList;
    }

    public Boolean getIsRejected() {
        return isRejected;
    }

    public void setIsRejected(Boolean isRejected) {
        this.isRejected = isRejected;
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
