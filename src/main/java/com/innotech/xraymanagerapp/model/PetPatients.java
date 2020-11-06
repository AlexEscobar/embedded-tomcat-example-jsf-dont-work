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
@Table(name = "PetPatients")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PetPatients.findAll", query = "SELECT p FROM PetPatients p"),
    @NamedQuery(name = "PetPatients.findById", query = "SELECT p FROM PetPatients p WHERE p.id = :id"),
    @NamedQuery(name = "PetPatients.findByName", query = "SELECT p FROM PetPatients p WHERE p.name = :name"),
    @NamedQuery(name = "PetPatients.findBySex", query = "SELECT p FROM PetPatients p WHERE p.sex = :sex"),
    @NamedQuery(name = "PetPatients.findByBirthDate", query = "SELECT p FROM PetPatients p WHERE p.birthDate = :birthDate"),
    @NamedQuery(name = "PetPatients.findByEntryDate", query = "SELECT p FROM PetPatients p WHERE p.entryDate = :entryDate"),
    @NamedQuery(name = "PetPatients.findByOwnerId", query = "SELECT p FROM PetPatients p WHERE p.ownerId.id = :ownerId"),
    @NamedQuery(name = "PetPatients.findByStatus", query = "SELECT p FROM PetPatients p WHERE p.status = :status"),
    @NamedQuery(name = "PetPatients.findByStatusAndClient", query = "SELECT p FROM PetPatients p"
            + " WHERE p.status = :status AND p.userId.clinicId.id = :clinicId")})
public class PetPatients implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "sqlite_PetPatients")
    @TableGenerator(name = "sqlite_PetPatients", table = "sqlite_sequence",
            pkColumnName = "name", valueColumnName = "seq",
            pkColumnValue = "PetPatients",
            initialValue = 1, allocationSize = 1)
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;
    
    @Column(name = "Sex")
    private Boolean sex;
    @Column(name = "Neutered")
    private Boolean neutered;
    @Size(max = 50)
    @Column(name = "PatientId")
    private String patientId;

    @Size(max = 50)
    @Column(name = "OwnerName")
    private String ownerName;

    @Column(name = "Status")
    private Boolean status;
    @Size(max = 50)
    @Column(name = "AccessionCode")
    private String accessionCode;
    @Size(max = 500)
    @Column(name = "Description")
    private String description;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "Name")
    private String name;
    @Column(name = "BirthDate")
    @Temporal(TemporalType.DATE)
    private Date birthDate;
    
    @Column(name = "EntryDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date entryDate;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "patientId", fetch = FetchType.LAZY)
    private List<WorkList> workListList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "patientId", fetch = FetchType.LAZY)
    private List<Studies> studiesList;
    @JoinColumn(name = "BreedId", referencedColumnName = "Id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Breeds breedId;
    @JoinColumn(name = "SpecieId", referencedColumnName = "Id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Species specieId;
    @JoinColumn(name = "OwnerId", referencedColumnName = "Id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Persons ownerId;
    @JoinColumn(name = "UserId", referencedColumnName = "Id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Users userId;

    public PetPatients() {
    }

    public PetPatients(Integer id) {
        this.id = id;
    }

    public PetPatients(Integer id, String name, boolean sex, Date entryDate, boolean status) {
        this.id = id;
        this.name = name;
        this.sex = sex;
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


    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public Date getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(Date entryDate) {
        this.entryDate = entryDate;
    }


    @XmlTransient
    public List<WorkList> getWorkListList() {
        return workListList;
    }

    public void setWorkListList(List<WorkList> workListList) {
        this.workListList = workListList;
    }

    @XmlTransient
    public List<Studies> getStudiesList() {
        return studiesList;
    }

    public void setStudiesList(List<Studies> studiesList) {
        this.studiesList = studiesList;
    }

    public Breeds getBreedId() {
        return breedId;
    }

    public void setBreedId(Breeds breedId) {
        this.breedId = breedId;
    }

    public Persons getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Persons ownerId) {
        this.ownerId = ownerId;
    }

    public Users getUserId() {
        return userId;
    }

    public void setUserId(Users userId) {
        this.userId = userId;
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
        if (!(object instanceof PetPatients)) {
            return false;
        }
        PetPatients other = (PetPatients) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.innotech.xraymanagerapp.model.PetPatients[ id=" + id + " ]";
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }


    public String getAccessionCode() {
        return accessionCode;
    }

    public void setAccessionCode(String accessionCode) {
        this.accessionCode = accessionCode;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public Boolean getSex() {
        return sex;
    }

    public void setSex(Boolean sex) {
        this.sex = sex;
    }

    public Boolean getNeutered() {
        return neutered;
    }

    public void setNeutered(Boolean neutered) {
        this.neutered = neutered;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public Species getSpecieId() {
        return specieId;
    }

    public void setSpecieId(Species specieId) {
        this.specieId = specieId;
    }
}
