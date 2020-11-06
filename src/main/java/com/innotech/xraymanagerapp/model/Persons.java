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
@Table(name = "Persons")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Persons.findAll", query = "SELECT p FROM Persons p"),
    @NamedQuery(name = "Persons.findById", query = "SELECT p FROM Persons p WHERE p.id = :id"),
    @NamedQuery(name = "Persons.findByFName", query = "SELECT p FROM Persons p WHERE p.fName = :fName"),
    @NamedQuery(name = "Persons.findByLName", query = "SELECT p FROM Persons p WHERE p.lName = :lName"),
    @NamedQuery(name = "Persons.findBySex", query = "SELECT p FROM Persons p WHERE p.sex = :sex"),
    @NamedQuery(name = "Persons.findByPhone", query = "SELECT p FROM Persons p WHERE p.phone = :phone"),
    @NamedQuery(name = "Persons.findByEmail", query = "SELECT p FROM Persons p WHERE p.email = :email"),
    @NamedQuery(name = "Persons.findByBirthDate", query = "SELECT p FROM Persons p WHERE p.birthDate = :birthDate"),
    @NamedQuery(name = "Persons.findByEntryDate", query = "SELECT p FROM Persons p WHERE p.entryDate = :entryDate"),
    @NamedQuery(name = "Persons.findByStatus", query = "SELECT p FROM Persons p WHERE p.status = :status"),
    @NamedQuery(name = "Persons.findByStatusAndClinic", query = "SELECT s FROM Persons s"
            + " WHERE s.status = :status AND s.userId.clinicId.id = :clinicId ORDER BY s.entryDate DESC")})
public class Persons implements Serializable {

    @Column(name = "Status")
    private Boolean status;

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "sqlite_person")
    @TableGenerator(name = "sqlite_person", table = "sqlite_sequence",
            pkColumnName = "name", valueColumnName = "seq",
            pkColumnValue = "Persons",
            initialValue = 1, allocationSize = 1)
//    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "Id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "FName")
    private String fName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "LName")
    private String lName;
    @Size(max = 200)
    @Column(name = "Address")
    private String address;
    @Column(name = "Sex")
    private Boolean sex;
    // @Pattern(regexp="^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{4})$", message="Invalid phone/fax format, should be as xxx-xxx-xxxx")//if the field contains phone or fax number consider using this annotation to enforce field validation

    @Size(max = 150)
    @Column(name = "Phone")
    private String phone;
    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
    @Size(max = 150)
    @Column(name = "Email")
    private String email;
    @Column(name = "BirthDate")
    @Temporal(TemporalType.DATE)
    private Date birthDate;

    @Column(name = "EntryDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date entryDate;

    @JoinColumn(name = "TypeId", referencedColumnName = "Id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private PersonTypes typeId;
    @JoinColumn(name = "UserId", referencedColumnName = "Id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Users userId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "ownerId", fetch = FetchType.LAZY)
    private List<PetPatients> petPatientsList;

    public Persons() {
    }

    public Persons(Integer id) {
        this.id = id;
    }

    public Persons(Integer id, String fName, String lName, Date entryDate, boolean status) {
        this.id = id;
        this.fName = fName;
        this.lName = lName;
        this.entryDate = entryDate;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFName() {
        return fName;
    }

    public void setFName(String fName) {
        this.fName = fName;
    }

    public String getLName() {
        return lName;
    }

    public void setLName(String lName) {
        this.lName = lName;
    }

    public Boolean getSex() {
        return sex;
    }

    public void setSex(Boolean sex) {
        this.sex = sex;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public PersonTypes getTypeId() {
        return typeId;
    }

    public void setTypeId(PersonTypes typeId) {
        this.typeId = typeId;
    }

    public Users getUserId() {
        return userId;
    }

    public void setUserId(Users userId) {
        this.userId = userId;
    }

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
        if (!(object instanceof Persons)) {
            return false;
        }
        Persons other = (Persons) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.innotech.xraymanagerapp.model.Persons[ id=" + id + " ]";
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
