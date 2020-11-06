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
 * @author PC
 */
@Entity
@Table(name = "ImageUNRejection")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ImageUNRejection.findAll", query = "SELECT i FROM ImageUNRejection i"),
    @NamedQuery(name = "ImageUNRejection.findById", query = "SELECT i FROM ImageUNRejection i WHERE i.id = :id"),
    @NamedQuery(name = "ImageUNRejection.findByDescription", query = "SELECT i FROM ImageUNRejection i WHERE i.description = :description"),
    @NamedQuery(name = "ImageUNRejection.findByEntryDate", query = "SELECT i FROM ImageUNRejection i WHERE i.entryDate = :entryDate")})
public class ImageUNRejection implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "sqlite_ImageUNRejection")
    @TableGenerator(name = "sqlite_ImageUNRejection", table = "sqlite_sequence",
            pkColumnName = "name", valueColumnName = "seq",
            pkColumnValue = "ImageUNRejection",
            initialValue = 1, allocationSize = 1)
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;
    @Size(max = 256)
    @Column(name = "Description")
    private String description;
    @Basic(optional = false)
    @NotNull
    @Column(name = "EntryDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date entryDate;
    @JoinColumn(name = "ImageId", referencedColumnName = "Id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Images imageId;

    public ImageUNRejection() {
    }

    public ImageUNRejection(Integer id) {
        this.id = id;
    }

    public ImageUNRejection(Integer id, Date entryDate) {
        this.id = id;
        this.entryDate = entryDate;
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

    public Date getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(Date entryDate) {
        this.entryDate = entryDate;
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
        if (!(object instanceof ImageUNRejection)) {
            return false;
        }
        ImageUNRejection other = (ImageUNRejection) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return imageId.getId() + " - " + description;
    }
    
}
