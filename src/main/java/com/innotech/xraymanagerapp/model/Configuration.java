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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
 * @author Hi
 */
@Entity
@Table(name = "Configuration")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Configuration.findAll", query = "SELECT c FROM Configuration c"),
    @NamedQuery(name = "Configuration.findById", query = "SELECT c FROM Configuration c WHERE c.id = :id"),
    @NamedQuery(name = "Configuration.findByParameterName", query = "SELECT c FROM Configuration c WHERE c.parameterName = :parameterName"),
    @NamedQuery(name = "Configuration.findByParameterValue", query = "SELECT c FROM Configuration c WHERE c.parameterValue = :parameterValue"),
    @NamedQuery(name = "Configuration.findByCreationDate", query = "SELECT c FROM Configuration c WHERE c.creationDate = :creationDate"),
    @NamedQuery(name = "Configuration.findByStatus", query = "SELECT c FROM Configuration c WHERE c.status = :status")})
public class Configuration implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "sqlite_Configuration")
    @TableGenerator(name = "sqlite_Configuration", table = "sqlite_sequence",
            pkColumnName = "name", valueColumnName = "seq",
            pkColumnValue = "Configuration",
            initialValue = 1, allocationSize = 1)
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "ParameterName")
    private String parameterName;
    @Size(max = 1000)
    @Column(name = "ParameterValue")
    private String parameterValue;
    @Basic(optional = false)
    @NotNull
    @Column(name = "CreationDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Status")
    private boolean status;

    public Configuration() {
    }

    public Configuration(Integer id) {
        this.id = id;
    }

    public Configuration(Integer id, String parameterName, Date creationDate, boolean status) {
        this.id = id;
        this.parameterName = parameterName;
        this.creationDate = creationDate;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public String getParameterValue() {
        return parameterValue;
    }

    public void setParameterValue(String parameterValue) {
        this.parameterValue = parameterValue;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
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
        if (!(object instanceof Configuration)) {
            return false;
        }
        Configuration other = (Configuration) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.innotech.xraymanagerapp.model.Configuration[ id=" + id + " ]";
    }

}
