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
@Table(name = "BranchOffices")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "BranchOffices.findAll", query = "SELECT b FROM BranchOffices b"),
    @NamedQuery(name = "BranchOffices.findById", query = "SELECT b FROM BranchOffices b WHERE b.id = :id"),
    @NamedQuery(name = "BranchOffices.findByName", query = "SELECT b FROM BranchOffices b WHERE b.name = :name"),
    @NamedQuery(name = "BranchOffices.findByAddress", query = "SELECT b FROM BranchOffices b WHERE b.address = :address"),
    @NamedQuery(name = "BranchOffices.findByEntryDate", query = "SELECT b FROM BranchOffices b WHERE b.entryDate = :entryDate"),
    @NamedQuery(name = "BranchOffices.findByStatus", query = "SELECT b FROM BranchOffices b WHERE b.status = :status")})
public class BranchOffices implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "sqlite_BranchOffices")
    @TableGenerator(name = "sqlite_BranchOffices", table = "sqlite_sequence",
            pkColumnName = "name", valueColumnName = "seq",
            pkColumnValue = "BranchOffices",
            initialValue = 1, allocationSize = 1)
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;
    @Size(max = 50)
    @Column(name = "Name")
    private String name;
    @Size(max = 500)
    @Column(name = "Address")
    private String address;
    
    @Column(name = "EntryDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date entryDate;
    
    @Column(name = "Status")
    private boolean status;
    @JoinColumn(name = "ClinicId", referencedColumnName = "Id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Clinics clinicId;

    public BranchOffices() {
    }

    public BranchOffices(Integer id) {
        this.id = id;
    }

    public BranchOffices(Integer id, Date entryDate, boolean status) {
        this.id = id;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(Date entryDate) {
        this.entryDate = entryDate;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Clinics getClinicId() {
        return clinicId;
    }

    public void setClinicId(Clinics clinicId) {
        this.clinicId = clinicId;
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
        if (!(object instanceof BranchOffices)) {
            return false;
        }
        BranchOffices other = (BranchOffices) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.innotech.xraymanagerapp.model.BranchOffices[ id=" + id + " ]";
    }
    
}
