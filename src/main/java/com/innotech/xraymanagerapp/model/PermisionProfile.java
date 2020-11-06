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
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author X-Ray
 */
@Entity
@Table(name = "Permision_Profile")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PermisionProfile.findAll", query = "SELECT p FROM PermisionProfile p"),
    @NamedQuery(name = "PermisionProfile.findByProfileId", query = "SELECT p FROM PermisionProfile p WHERE p.permisionProfilePK.profileId = :profileId"),
    @NamedQuery(name = "PermisionProfile.findByPermisionId", query = "SELECT p FROM PermisionProfile p WHERE p.permisionProfilePK.permisionId = :permisionId"),
    @NamedQuery(name = "PermisionProfile.findByEntryDate", query = "SELECT p FROM PermisionProfile p WHERE p.entryDate = :entryDate"),
    @NamedQuery(name = "PermisionProfile.findByStatus", query = "SELECT p FROM PermisionProfile p WHERE p.status = :status")})
public class PermisionProfile implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected PermisionProfilePK permisionProfilePK;
    
    @Column(name = "EntryDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date entryDate;
    
    @Column(name = "Status")
    private boolean status;
    @JoinColumn(name = "PermisionId", referencedColumnName = "Id", insertable = false, updatable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Permisions permisions;
    @JoinColumn(name = "ProfileId", referencedColumnName = "Id", insertable = false, updatable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Profiles profiles;

    public PermisionProfile() {
    }

    public PermisionProfile(PermisionProfilePK permisionProfilePK) {
        this.permisionProfilePK = permisionProfilePK;
    }

    public PermisionProfile(PermisionProfilePK permisionProfilePK, Date entryDate, boolean status) {
        this.permisionProfilePK = permisionProfilePK;
        this.entryDate = entryDate;
        this.status = status;
    }

    public PermisionProfile(int profileId, int permisionId) {
        this.permisionProfilePK = new PermisionProfilePK(profileId, permisionId);
    }

    public PermisionProfilePK getPermisionProfilePK() {
        return permisionProfilePK;
    }

    public void setPermisionProfilePK(PermisionProfilePK permisionProfilePK) {
        this.permisionProfilePK = permisionProfilePK;
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

    public Permisions getPermisions() {
        return permisions;
    }

    public void setPermisions(Permisions permisions) {
        this.permisions = permisions;
    }

    public Profiles getProfiles() {
        return profiles;
    }

    public void setProfiles(Profiles profiles) {
        this.profiles = profiles;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (permisionProfilePK != null ? permisionProfilePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PermisionProfile)) {
            return false;
        }
        PermisionProfile other = (PermisionProfile) object;
        if ((this.permisionProfilePK == null && other.permisionProfilePK != null) || (this.permisionProfilePK != null && !this.permisionProfilePK.equals(other.permisionProfilePK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.innotech.xraymanagerapp.model.PermisionProfile[ permisionProfilePK=" + permisionProfilePK + " ]";
    }
    
}
