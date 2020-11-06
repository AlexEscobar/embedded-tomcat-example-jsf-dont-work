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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "Breeds")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Breeds.findAll", query = "SELECT b FROM Breeds b ORDER BY b.name ASC"),
    @NamedQuery(name = "Breeds.findById", query = "SELECT b FROM Breeds b WHERE b.id = :id"),
    @NamedQuery(name = "Breeds.findByName", query = "SELECT b FROM Breeds b WHERE b.name = :name"),
    @NamedQuery(name = "Breeds.findBySpeciesId", query = "SELECT b FROM Breeds b WHERE b.specieId.id = :speciesId AND  b.status = true ORDER BY b.name"),
    @NamedQuery(name = "Breeds.findByEntryDate", query = "SELECT b FROM Breeds b WHERE b.entryDate = :entryDate"),
    @NamedQuery(name = "Breeds.findByStatus", query = "SELECT b FROM Breeds b WHERE b.status = true")})
public class Breeds implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "sqlite_Breeds")
    @TableGenerator(name = "sqlite_Breeds", table = "sqlite_sequence",
            pkColumnName = "name", valueColumnName = "seq",
            pkColumnValue = "Breeds",
            initialValue = 1, allocationSize = 1)
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Column(name = "Status")
    private Boolean status;
    @JoinColumn(name = "UserId", referencedColumnName = "Id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Users userId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "Name")
    private String name;
    
    @Column(name = "EntryDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date entryDate;
    
    @JoinColumn(name = "SpecieId", referencedColumnName = "Id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Species specieId;
//    @JoinColumn(name = "UserId", referencedColumnName = "Id")
//    @ManyToOne(optional = false, fetch = FetchType.LAZY)
//    private Users userId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "breedId", fetch = FetchType.LAZY)
    private List<PetPatients> petPatientsList;

    public Breeds() {
    }

    public Breeds(Integer id) {
        this.id = id;
    }

    public Breeds(Integer id, String name, Date entryDate, boolean status) {
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


    public Species getSpecieId() {
        return specieId;
    }

    public void setSpecieId(Species specieId) {
        this.specieId = specieId;
    }

//    public Users getUserId() {
//        return userId;
//    }
//
//    public void setUserId(Users userId) {
//        this.userId = userId;
//    }

    @XmlTransient
    public List<PetPatients> getPetPatientsList() {
        return petPatientsList;
    }

    public void setPetPatientsList(List<PetPatients> petPatientsList) {
        this.petPatientsList = petPatientsList;
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
        if (!(object instanceof Breeds)) {
            return false;
        }
        Breeds other = (Breeds) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.innotech.xraymanagerapp.model.Breeds[ id=" + id + " ]";
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Users getUserId() {
        return userId;
    }

    public void setUserId(Users userId) {
        this.userId = userId;
    }
    
}
