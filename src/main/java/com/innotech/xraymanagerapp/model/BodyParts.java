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
 * @author Hi
 */
@Entity
@Table(name = "BodyParts")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "BodyParts.findAll", query = "SELECT b FROM BodyParts b"),
    @NamedQuery(name = "BodyParts.findById", query = "SELECT b FROM BodyParts b WHERE b.id = :id"),
    @NamedQuery(name = "BodyParts.findByDescription", query = "SELECT b FROM BodyParts b WHERE b.description = :description"),
    @NamedQuery(name = "BodyParts.findByEntryDate", query = "SELECT b FROM BodyParts b WHERE b.entryDate = :entryDate"),
    @NamedQuery(name = "BodyParts.findByStatus", query = "SELECT b FROM BodyParts b WHERE b.status = :status")})
public class BodyParts implements Serializable {

    @OneToMany(mappedBy = "bodyPartsId", fetch = FetchType.LAZY)
    private List<Annotations> annotationsList;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "sqlite_BodyParts")
    @TableGenerator(name = "sqlite_BodyParts", table = "sqlite_sequence",
            pkColumnName = "name", valueColumnName = "seq",
            pkColumnValue = "BodyParts",
            initialValue = 1, allocationSize = 1)
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "Description")
    private String description;
    @Basic(optional = false)
    @NotNull
    @Column(name = "EntryDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date entryDate;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Status")
    private boolean status;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "bodyPartId", fetch = FetchType.LAZY)
    private List<GeneratorSpeciesBySizeConfig> generatorSpeciesBySizeConfigList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "bodyPartId", fetch = FetchType.LAZY)
    private List<GeneratorSpeciesByThicknessConfig> generatorSpeciesByThicknessConfigList;

    public BodyParts() {
    }

    public BodyParts(Integer id) {
        this.id = id;
    }

    public BodyParts(Integer id, String description, Date entryDate, boolean status) {
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

    @XmlTransient
    public List<GeneratorSpeciesByThicknessConfig> getGeneratorSpeciesByThicknessConfigList() {
        return generatorSpeciesByThicknessConfigList;
    }

    public void setGeneratorSpeciesByThicknessConfigList(List<GeneratorSpeciesByThicknessConfig> generatorSpeciesByThicknessConfigList) {
        this.generatorSpeciesByThicknessConfigList = generatorSpeciesByThicknessConfigList;
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
        if (!(object instanceof BodyParts)) {
            return false;
        }
        BodyParts other = (BodyParts) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.innotech.xraymanagerapp.model.BodyParts[ id=" + id + " ]";
    }

    @XmlTransient
    public List<Annotations> getAnnotationsList() {
        return annotationsList;
    }

    public void setAnnotationsList(List<Annotations> annotationsList) {
        this.annotationsList = annotationsList;
    }
    
}
