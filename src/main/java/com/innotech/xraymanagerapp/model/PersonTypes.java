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
 * @author X-Ray
 */
@Entity
@Table(name = "PersonTypes")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PersonTypes.findAll", query = "SELECT p FROM PersonTypes p"),
    @NamedQuery(name = "PersonTypes.findById", query = "SELECT p FROM PersonTypes p WHERE p.id = :id"),
    @NamedQuery(name = "PersonTypes.findByType", query = "SELECT p FROM PersonTypes p WHERE p.type = :type"),
    @NamedQuery(name = "PersonTypes.findByStatus", query = "SELECT p FROM PersonTypes p WHERE p.status = :status")})
public class PersonTypes implements Serializable {

    @Column(name = "Status")
    private Boolean status;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "sqlite_PersonTypes")
    @TableGenerator(name = "sqlite_PersonTypes", table = "sqlite_sequence",
            pkColumnName = "name", valueColumnName = "seq",
            pkColumnValue = "PersonTypes",
            initialValue = 1, allocationSize = 1)
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "Type")
    private String type;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "typeId", fetch = FetchType.LAZY)
    private List<Persons> personsList;

    public PersonTypes() {
    }

    public PersonTypes(Integer id) {
        this.id = id;
    }

    public PersonTypes(Integer id, String type, boolean status) {
        this.id = id;
        this.type = type;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    @XmlTransient
    public List<Persons> getPersonsList() {
        return personsList;
    }

    public void setPersonsList(List<Persons> personsList) {
        this.personsList = personsList;
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
        if (!(object instanceof PersonTypes)) {
            return false;
        }
        PersonTypes other = (PersonTypes) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.innotech.xraymanagerapp.model.PersonTypes[ id=" + id + " ]";
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
    
}
