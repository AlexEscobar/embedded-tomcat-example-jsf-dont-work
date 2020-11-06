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
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
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
@Table(name = "Licence")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Licence.findAll", query = "SELECT l FROM Licence l")
    , @NamedQuery(name = "Licence.findById", query = "SELECT l FROM Licence l WHERE l.id = :id")
    , @NamedQuery(name = "Licence.findBySeed", query = "SELECT l FROM Licence l WHERE l.seed = :seed")
    , @NamedQuery(name = "Licence.findByLicenceNumber", query = "SELECT l FROM Licence l WHERE l.licenceNumber = :licenceNumber")
    , @NamedQuery(name = "Licence.findByCreationDate", query = "SELECT l FROM Licence l WHERE l.creationDate = :creationDate")
    , @NamedQuery(name = "Licence.findByExpirationDate", query = "SELECT l FROM Licence l WHERE l.expirationDate = :expirationDate")
    , @NamedQuery(name = "Licence.findByStatus", query = "SELECT l FROM Licence l WHERE l.status = :status")})
public class Licence implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "Id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "Seed")
    private String seed;
    @Size(max = 2147483647)
    @Column(name = "LicenceNumber")
    private String licenceNumber;
    @Basic(optional = false)
    @NotNull
    @Column(name = "CreationDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;
    @Column(name = "ExpirationDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date expirationDate;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Status")
    private boolean status;

    public Licence() {
    }

    public Licence(Integer id) {
        this.id = id;
    }

    public Licence(Integer id, String seed, Date creationDate, boolean status) {
        this.id = id;
        this.seed = seed;
        this.creationDate = creationDate;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSeed() {
        return seed;
    }

    public void setSeed(String seed) {
        this.seed = seed;
    }

    public String getLicenceNumber() {
        return licenceNumber;
    }

    public void setLicenceNumber(String licenceNumber) {
        this.licenceNumber = licenceNumber;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
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
        if (!(object instanceof Licence)) {
            return false;
        }
        Licence other = (Licence) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.innotech.xraymanagerapp.model.Licence[ id=" + id + " ]";
    }
    
}
