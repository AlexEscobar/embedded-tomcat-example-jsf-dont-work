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
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author X-Ray
 */
@Entity
@Table(name = "TeethNumbers")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TeethNumbers.findAll", query = "SELECT t FROM TeethNumbers t"),
    @NamedQuery(name = "TeethNumbers.findById", query = "SELECT t FROM TeethNumbers t WHERE t.id = :id"),
    @NamedQuery(name = "TeethNumbers.findByNumber", query = "SELECT t FROM TeethNumbers t WHERE t.number = :number"),
    @NamedQuery(name = "TeethNumbers.findByName", query = "SELECT t FROM TeethNumbers t WHERE t.name = :name"),
    @NamedQuery(name = "TeethNumbers.findBySide", query = "SELECT t FROM TeethNumbers t WHERE t.side = :side"),
    @NamedQuery(name = "TeethNumbers.findByStatus", query = "SELECT t FROM TeethNumbers t WHERE t.status = :status"),
    @NamedQuery(name = "TeethNumbers.findBySpeciesId", query = "SELECT t FROM TeethNumbers t WHERE t.status = :status AND t.specieId.id IN (:speciesId, 0)")})
public class TeethNumbers implements Serializable {

    @Column(name = "Status")
    private Boolean status;

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "Id")
    private String id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Number")
    private String number;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 15)
    @Column(name = "Name")
    private String name;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 5)
    @Column(name = "Side")
    private String side;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "teethNumberId", fetch = FetchType.LAZY)
    private List<Annotations> annotationsList;
    @JoinColumn(name = "SpecieId", referencedColumnName = "Id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Species specieId;

    public TeethNumbers() {
    }

    public TeethNumbers(String id) {
        this.id = id;
    }

    public TeethNumbers(String id, String number, String name, String side, boolean status) {
        this.id = id;
        this.number = number;
        this.name = name;
        this.side = side;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    @XmlTransient
    public List<Annotations> getAnnotationsList() {
        return annotationsList;
    }

    public void setAnnotationsList(List<Annotations> annotationsList) {
        this.annotationsList = annotationsList;
    }

    public Species getSpecieId() {
        return specieId;
    }

    public void setSpecieId(Species specieId) {
        this.specieId = specieId;
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
        if (!(object instanceof TeethNumbers)) {
            return false;
        }
        TeethNumbers other = (TeethNumbers) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return id + "| " + name + " " + number;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

}
