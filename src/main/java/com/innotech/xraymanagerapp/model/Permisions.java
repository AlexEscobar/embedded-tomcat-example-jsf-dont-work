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
@Table(name = "Permisions")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Permisions.findAll", query = "SELECT p FROM Permisions p"),
    @NamedQuery(name = "Permisions.findById", query = "SELECT p FROM Permisions p WHERE p.id = :id"),
    @NamedQuery(name = "Permisions.findByDescription", query = "SELECT p FROM Permisions p WHERE p.description = :description"),
    @NamedQuery(name = "Permisions.findByEntryDate", query = "SELECT p FROM Permisions p WHERE p.entryDate = :entryDate"),
    @NamedQuery(name = "Permisions.findByStatus", query = "SELECT p FROM Permisions p WHERE p.status = :status")})
public class Permisions implements Serializable {

    @Column(name = "Status")
    private Boolean status;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "sqlite_Permisions")
    @TableGenerator(name = "sqlite_Permisions", table = "sqlite_sequence",
            pkColumnName = "name", valueColumnName = "seq",
            pkColumnValue = "Permisions",
            initialValue = 1, allocationSize = 1)
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 120)
    @Column(name = "Description")
    private String description;
    
    @Size(max = 30)
    @Column(name = "AccessName")
    private String accessName;
    
    @Column(name = "EntryDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date entryDate;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "permisions", fetch = FetchType.LAZY)
    private List<PermisionProfile> permisionProfileList;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "lPageId", fetch = FetchType.LAZY)// this (one) permission landing page is used for many users
    private List<Users> usersList;

    public Permisions() {
    }

    public Permisions(Integer id) {
        this.id = id;
    }

    public Permisions(Integer id, String description, Date entryDate, boolean status) {
        this.id = id;
        this.description = description;
        this.entryDate = entryDate;
        this.status = status;
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


    @XmlTransient
    public List<PermisionProfile> getPermisionProfileList() {
        return permisionProfileList;
    }

    public void setPermisionProfileList(List<PermisionProfile> permisionProfileList) {
        this.permisionProfileList = permisionProfileList;
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
        if (!(object instanceof Permisions)) {
            return false;
        }
        Permisions other = (Permisions) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.innotech.xraymanagerapp.model.Permisions[ id=" + id + " ]";
    }

    public String getAccessName() {
        return accessName;
    }

    public void setAccessName(String accessName) {
        this.accessName = accessName;
    }

    @XmlTransient
    public List<Users> getUsersList() {
        return usersList;
    }

    public void setUsersList(List<Users> usersList) {
        this.usersList = usersList;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
    
    

}
