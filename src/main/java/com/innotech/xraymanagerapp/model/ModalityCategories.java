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
@Table(name = "ModalityCategories")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ModalityCategories.findAll", query = "SELECT m FROM ModalityCategories m"),
    @NamedQuery(name = "ModalityCategories.findById", query = "SELECT m FROM ModalityCategories m WHERE m.id = :id"),
    @NamedQuery(name = "ModalityCategories.findByName", query = "SELECT m FROM ModalityCategories m WHERE m.name = :name"),
    @NamedQuery(name = "ModalityCategories.findByEntryDate", query = "SELECT m FROM ModalityCategories m WHERE m.entryDate = :entryDate"),
    @NamedQuery(name = "ModalityCategories.findByStatus", query = "SELECT m FROM ModalityCategories m WHERE m.status = :status")})
public class ModalityCategories implements Serializable {

    @Column(name = "Status")
    private Boolean status;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "sqlite_ModalityCategories")
    @TableGenerator(name = "sqlite_ModalityCategories", table = "sqlite_sequence",
            pkColumnName = "name", valueColumnName = "seq",
            pkColumnValue = "ModalityCategories",
            initialValue = 1, allocationSize = 1)
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "Name")
    private String name;
    
    @Column(name = "EntryDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date entryDate;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "categoryId", fetch = FetchType.LAZY)
    private List<Modalities> modalitiesList;

    public ModalityCategories() {
    }

    public ModalityCategories(Integer id) {
        this.id = id;
    }

    public ModalityCategories(Integer id, String name, Date entryDate, boolean status) {
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


    @XmlTransient
    public List<Modalities> getModalitiesList() {
        return modalitiesList;
    }

    public void setModalitiesList(List<Modalities> modalitiesList) {
        this.modalitiesList = modalitiesList;
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
        if (!(object instanceof ModalityCategories)) {
            return false;
        }
        ModalityCategories other = (ModalityCategories) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.innotech.xraymanagerapp.model.ModalityCategories[ id=" + id + " ]";
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
    
}
