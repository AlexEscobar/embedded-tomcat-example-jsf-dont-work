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
@Table(name = "Species")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Species.findAll", query = "SELECT s FROM Species s"),
    @NamedQuery(name = "Species.findById", query = "SELECT s FROM Species s WHERE s.id = :id"),
    @NamedQuery(name = "Species.findByName", query = "SELECT s FROM Species s WHERE s.name = :name"),
    @NamedQuery(name = "Species.findByEntryDate", query = "SELECT s FROM Species s WHERE s.entryDate = :entryDate"),
    @NamedQuery(name = "Species.findByStatus", query = "SELECT s FROM Species s WHERE s.status = :status")})
public class Species implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "sqlite_Species")
    @TableGenerator(name = "sqlite_Species", table = "sqlite_sequence",
            pkColumnName = "name", valueColumnName = "seq",
            pkColumnValue = "Species",
            initialValue = 1, allocationSize = 1)
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "speciesId", fetch = FetchType.LAZY)
    private List<GeneratorSpeciesBySizeConfig> generatorSpeciesBySizeConfigList;

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
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "specieId", fetch = FetchType.LAZY)
    private List<Breeds> breedsList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "specieId", fetch = FetchType.LAZY)
    private List<TeethNumbers> teethNumbersList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "specieId", fetch = FetchType.LAZY)
    private List<PetPatients> petPatientsList;
//    @JoinColumn(name = "UserId", referencedColumnName = "Id")
//    @ManyToOne(optional = false, fetch = FetchType.LAZY)
//    private Users userId;

    public Species() {
    }

    public Species(Integer id) {
        this.id = id;
    }

    public Species(Integer id, String name, Date entryDate, boolean status) {
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


    @XmlTransient
    public List<Breeds> getBreedsList() {
        return breedsList;
    }

    public void setBreedsList(List<Breeds> breedsList) {
        this.breedsList = breedsList;
    }

    @XmlTransient
    public List<TeethNumbers> getTeethNumbersList() {
        return teethNumbersList;
    }

    public void setTeethNumbersList(List<TeethNumbers> teethNumbersList) {
        this.teethNumbersList = teethNumbersList;
    }
//
//    public Users getUserId() {
//        return userId;
//    }
//
//    public void setUserId(Users userId) {
//        this.userId = userId;
//    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Species)) {
            return false;
        }
        Species other = (Species) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.innotech.xraymanagerapp.model.Species[ id=" + id + " ]";
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

    public List<PetPatients> getPetPatientsList() {
        return petPatientsList;
    }

    public void setPetPatientsList(List<PetPatients> petPatientsList) {
        this.petPatientsList = petPatientsList;
    }

    @XmlTransient
    public List<GeneratorSpeciesBySizeConfig> getGeneratorSpeciesBySizeConfigList() {
        return generatorSpeciesBySizeConfigList;
    }

    public void setGeneratorSpeciesBySizeConfigList(List<GeneratorSpeciesBySizeConfig> generatorSpeciesBySizeConfigList) {
        this.generatorSpeciesBySizeConfigList = generatorSpeciesBySizeConfigList;
    }
    
}
