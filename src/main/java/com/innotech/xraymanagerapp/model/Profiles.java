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
@Table(name = "Profiles")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Profiles.findAll", query = "SELECT p FROM Profiles p"),
    @NamedQuery(name = "Profiles.findById", query = "SELECT p FROM Profiles p WHERE p.id = :id"),
    @NamedQuery(name = "Profiles.findByName", query = "SELECT p FROM Profiles p WHERE p.name = :name"),
    @NamedQuery(name = "Profiles.findByEntryDate", query = "SELECT p FROM Profiles p WHERE p.entryDate = :entryDate"),
    @NamedQuery(name = "Profiles.findByStatus", query = "SELECT p FROM Profiles p WHERE p.status = :status")})
public class Profiles implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "sqlite_Profiles")
    @TableGenerator(name = "sqlite_Profiles", table = "sqlite_sequence",
            pkColumnName = "name", valueColumnName = "seq",
            pkColumnValue = "Profiles",
            initialValue = 1, allocationSize = 1)
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "Name")
    private String name;
    
    @Column(name = "EntryDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date entryDate;
    
    @Column(name = "Status")
    private boolean status;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "profiles", fetch = FetchType.LAZY)
    private List<PermisionProfile> permisionProfileList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "profiles", fetch = FetchType.LAZY)
    private List<UserProfile> userProfileList;

    public Profiles() {
    }

    public Profiles(Integer id) {
        this.id = id;
    }

    public Profiles(Integer id, String name, Date entryDate, boolean status) {
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

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @XmlTransient
    public List<PermisionProfile> getPermisionProfileList() {
        return permisionProfileList;
    }

    public void setPermisionProfileList(List<PermisionProfile> permisionProfileList) {
        this.permisionProfileList = permisionProfileList;
    }

    @XmlTransient
    public List<UserProfile> getUserProfileList() {
        return userProfileList;
    }

    public void setUserProfileList(List<UserProfile> userProfileList) {
        this.userProfileList = userProfileList;
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
        if (!(object instanceof Profiles)) {
            return false;
        }
        Profiles other = (Profiles) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return name;
    }
    
}
