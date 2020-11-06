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
 * @author Alexander Escobar L.
 */
@Entity
@Table(name = "AnimalSizes")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AnimalSizes.findAll", query = "SELECT a FROM AnimalSizes a"),
    @NamedQuery(name = "AnimalSizes.findById", query = "SELECT a FROM AnimalSizes a WHERE a.id = :id"),
    @NamedQuery(name = "AnimalSizes.findBySize", query = "SELECT a FROM AnimalSizes a WHERE a.animalSize = :size"),
    @NamedQuery(name = "AnimalSizes.findByEntryDate", query = "SELECT a FROM AnimalSizes a WHERE a.entryDate = :entryDate"),
    @NamedQuery(name = "AnimalSizes.findByStatus", query = "SELECT a FROM AnimalSizes a WHERE a.status = :status")})
public class AnimalSizes implements Serializable {

    @OneToMany(mappedBy = "animalSizeId")
    private List<Annotations> annotationsList;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "sqlite_AnimalSizes")
    @TableGenerator(name = "sqlite_AnimalSizes", table = "sqlite_sequence",
            pkColumnName = "name", valueColumnName = "seq",
            pkColumnValue = "AnimalSizes",
            initialValue = 1, allocationSize = 1)
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 16)
    @Column(name = "Size")
    private String animalSize;
    @Basic(optional = false)
    @NotNull
    @Column(name = "EntryDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date entryDate;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Status")
    private boolean status;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "sizeId", fetch = FetchType.LAZY)
    private List<GeneratorSpeciesBySizeConfig> generatorSpeciesBySizeConfigList;

    public AnimalSizes() {
    }

    public AnimalSizes(Integer id) {
        this.id = id;
    }

    public AnimalSizes(Integer id, String size, Date entryDate, boolean status) {
        this.id = id;
        this.animalSize = size;
        this.entryDate = entryDate;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAnimalSize() {
        return animalSize;
    }

    public void setAnimalSize(String animalSize) {
        this.animalSize = animalSize;
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
    public List<GeneratorSpeciesBySizeConfig> getGeneratorSpeciesBySizeConfigList() {
        return generatorSpeciesBySizeConfigList;
    }

    public void setGeneratorSpeciesBySizeConfigList(List<GeneratorSpeciesBySizeConfig> generatorSpeciesBySizeConfigList) {
        this.generatorSpeciesBySizeConfigList = generatorSpeciesBySizeConfigList;
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
        if (!(object instanceof AnimalSizes)) {
            return false;
        }
        AnimalSizes other = (AnimalSizes) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.innotech.xraymanagerapp.model.AnimalSizes[ id=" + id + " ]";
    }

    @XmlTransient
    public List<Annotations> getAnnotationsList() {
        return annotationsList;
    }

    public void setAnnotationsList(List<Annotations> annotationsList) {
        this.annotationsList = annotationsList;
    }

}
