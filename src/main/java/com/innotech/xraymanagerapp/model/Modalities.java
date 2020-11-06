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
@Table(name = "Modalities")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Modalities.findAll", query = "SELECT m FROM Modalities m"),
    @NamedQuery(name = "Modalities.findById", query = "SELECT m FROM Modalities m WHERE m.id = :id"),
    @NamedQuery(name = "Modalities.findByName", query = "SELECT m FROM Modalities m WHERE m.name = :name"),
    @NamedQuery(name = "Modalities.findByEntryDate", query = "SELECT m FROM Modalities m WHERE m.entryDate = :entryDate"),
    @NamedQuery(name = "Modalities.findByStatus", query = "SELECT m FROM Modalities m WHERE m.status = :status")})
public class Modalities implements Serializable {

    @Column(name = "Status")
    private Boolean status;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "sqlite_Modalities")
    @TableGenerator(name = "sqlite_Modalities", table = "sqlite_sequence",
            pkColumnName = "name", valueColumnName = "seq",
            pkColumnValue = "Modalities",
            initialValue = 1, allocationSize = 1)
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 150)
    @Column(name = "Name")
    private String name;
    
    @Column(name = "EntryDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date entryDate;
    
    @JoinColumn(name = "CategoryId", referencedColumnName = "Id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private ModalityCategories categoryId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "modalityId", fetch = FetchType.LAZY)
    private List<Images> imagesList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "modaliyId", fetch = FetchType.LAZY)
    private List<Studies> studiesList;

    public Modalities() {
    }

    public Modalities(Integer id) {
        this.id = id;
    }

    public Modalities(Integer id, String name, Date entryDate, boolean status) {
        this.id = id;
        this.name = name;
        this.entryDate = entryDate;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(Date entryDate) {
        this.entryDate = entryDate;
    }


    public ModalityCategories getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(ModalityCategories categoryId) {
        this.categoryId = categoryId;
    }

    @XmlTransient
    public List<Images> getImagesList() {
        return imagesList;
    }

    public void setImagesList(List<Images> imagesList) {
        this.imagesList = imagesList;
    }

    @XmlTransient
    public List<Studies> getStudiesList() {
        return studiesList;
    }

    public void setStudiesList(List<Studies> studiesList) {
        this.studiesList = studiesList;
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
        if (!(object instanceof Modalities)) {
            return false;
        }
        Modalities other = (Modalities) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.innotech.xraymanagerapp.model.Modalities[ id=" + id + " ]";
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
    
}
