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
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author PC
 */
@Entity
@Table(name = "ImageRejection")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ImageRejection.findAll", query = "SELECT i FROM ImageRejection i"),
    @NamedQuery(name = "ImageRejection.findById", query = "SELECT i FROM ImageRejection i WHERE i.id = :id"),
    @NamedQuery(name = "ImageRejection.findByEntryDate", query = "SELECT i FROM ImageRejection i WHERE i.entryDate = :entryDate")})
public class ImageRejection implements Serializable {

    @JoinColumn(name = "UserId", referencedColumnName = "Id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Users userId;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "sqlite_ImageRejection")
    @TableGenerator(name = "sqlite_ImageRejection", table = "sqlite_sequence",
            pkColumnName = "name", valueColumnName = "seq",
            pkColumnValue = "ImageRejection",
            initialValue = 1, allocationSize = 1)
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "EntryDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date entryDate;
    @JoinColumn(name = "ImageId", referencedColumnName = "Id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Images imageId;
    @JoinColumn(name = "RejectionReasonId", referencedColumnName = "Id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private RejectionReasons rejectionReasonId;

    public ImageRejection() {
    }

    public ImageRejection(Integer id) {
        this.id = id;
    }

    public ImageRejection(Integer id, Date entryDate) {
        this.id = id;
        this.entryDate = entryDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public RejectionReasons getRejectionReasonId() {
        return rejectionReasonId;
    }

    public void setRejectionReasonId(RejectionReasons rejectionReasonId) {
        this.rejectionReasonId = rejectionReasonId;
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
        if (!(object instanceof ImageRejection)) {
            return false;
        }
        ImageRejection other = (ImageRejection) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return imageId.getId() + " - " + rejectionReasonId.getReason();
    }

    public Users getUserId() {
        return userId;
    }

    public void setUserId(Users userId) {
        this.userId = userId;
    }

}
