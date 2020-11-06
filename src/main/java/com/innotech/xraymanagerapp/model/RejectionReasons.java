/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.model;

import java.io.Serializable;
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author PC
 */
@Entity
@Table(name = "RejectionReasons")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RejectionReasons.findAll", query = "SELECT r FROM RejectionReasons r"),
    @NamedQuery(name = "RejectionReasons.findById", query = "SELECT r FROM RejectionReasons r WHERE r.id = :id"),
    @NamedQuery(name = "RejectionReasons.findByReason", query = "SELECT r FROM RejectionReasons r WHERE r.reason = :reason"),
    @NamedQuery(name = "RejectionReasons.findByDescription", query = "SELECT r FROM RejectionReasons r WHERE r.description = :description")})
public class RejectionReasons implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "sqlite_RejectionReasons")
    @TableGenerator(name = "sqlite_RejectionReasons", table = "sqlite_sequence",
            pkColumnName = "name", valueColumnName = "seq",
            pkColumnValue = "RejectionReasons",
            initialValue = 1, allocationSize = 1)
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "Reason")
    private String reason;
    @Size(max = 256)
    @Column(name = "Description")
    private String description;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "rejectionReasonId", fetch = FetchType.LAZY)
    private List<ImageRejection> imageRejectionList;

    public RejectionReasons() {
    }

    public RejectionReasons(Integer id) {
        this.id = id;
    }

    public RejectionReasons(Integer id, String reason) {
        this.id = id;
        this.reason = reason;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlTransient
    public List<ImageRejection> getImageRejectionList() {
        return imageRejectionList;
    }

    public void setImageRejectionList(List<ImageRejection> imageRejectionList) {
        this.imageRejectionList = imageRejectionList;
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
        if (!(object instanceof RejectionReasons)) {
            return false;
        }
        RejectionReasons other = (RejectionReasons) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return reason;
    }
    
}
