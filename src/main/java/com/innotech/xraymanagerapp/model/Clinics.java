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
@Table(name = "Clinics")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Clinics.findAll", query = "SELECT c FROM Clinics c"),
    @NamedQuery(name = "Clinics.findById", query = "SELECT c FROM Clinics c WHERE c.id = :id"),
    @NamedQuery(name = "Clinics.findByName", query = "SELECT c FROM Clinics c WHERE c.name = :name"),
    @NamedQuery(name = "Clinics.findByAddress", query = "SELECT c FROM Clinics c WHERE c.address = :address"),
    @NamedQuery(name = "Clinics.findByLogoPath", query = "SELECT c FROM Clinics c WHERE c.logoPath = :logoPath"),
    @NamedQuery(name = "Clinics.findByEntryDate", query = "SELECT c FROM Clinics c WHERE c.entryDate = :entryDate"),
    @NamedQuery(name = "Clinics.findByStatus", query = "SELECT c FROM Clinics c WHERE c.status = :status"),
    @NamedQuery(name = "Clinics.findByUserAndStatus", query = "SELECT c FROM Clinics c, Users u WHERE u.clinicId.id = c.id  AND u.id = :userId"),
    @NamedQuery(name = "Clinics.findByLicence", query = "SELECT c FROM Clinics c WHERE c.licence = :licence"),
    @NamedQuery(name = "Clinics.findByLicenceExpireDate", query = "SELECT c FROM Clinics c WHERE c.licenceExpireDate = :licenceExpireDate")})
public class Clinics implements Serializable {

    // @Pattern(regexp="^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{4})$", message="Invalid phone/fax format, should be as xxx-xxx-xxxx")//if the field contains phone or fax number consider using this annotation to enforce field validation
    @Size(max = 50)
    @Column(name = "Phone")
    private String phone;
    // @Pattern(regexp="^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{4})$", message="Invalid phone/fax format, should be as xxx-xxx-xxxx")//if the field contains phone or fax number consider using this annotation to enforce field validation
    @Size(max = 50)
    @Column(name = "FAX")
    private String fax;

    @OneToMany(mappedBy = "clinicId", fetch = FetchType.LAZY)
    private List<EmailConfig> emailConfigList;

    @Column(name = "Status")
    private Boolean status;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "sqlite_Clinics")
    @TableGenerator(name = "sqlite_Clinics", table = "sqlite_sequence",
            pkColumnName = "name", valueColumnName = "seq",
            pkColumnValue = "Clinics",
            initialValue = 1, allocationSize = 1)
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "Name")
    private String name;
    @Size(max = 500)
    @Column(name = "Address")
    private String address;
    @Size(max = 150)
    @Column(name = "LogoPath")
    private String logoPath;
    
    @Column(name = "ClinicType")
    private Boolean clinicType;// true = vet, false = human
    
    @Column(name = "EntryDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date entryDate;
    
    @Size(max = 2000)
    @Column(name = "Licence")
    private String licence;
    @Column(name = "LicenceExpireDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date licenceExpireDate;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "clinicId", fetch = FetchType.LAZY)
    private List<BranchOffices> branchOfficesList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "clinicId", fetch = FetchType.LAZY)
    private List<Users> usersList;

    public Clinics() {
    }

    public Clinics(Integer id) {
        this.id = id;
    }

    public Clinics(Integer id, String name, Date entryDate, boolean status) {
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLogoPath() {
        return logoPath;
    }

    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }

    public Date getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(Date entryDate) {
        this.entryDate = entryDate;
    }


    public String getLicence() {
        return licence;
    }

    public void setLicence(String licence) {
        this.licence = licence;
    }

    public Date getLicenceExpireDate() {
        return licenceExpireDate;
    }

    public void setLicenceExpireDate(Date licenceExpireDate) {
        this.licenceExpireDate = licenceExpireDate;
    }

    @XmlTransient
    public List<BranchOffices> getBranchOfficesList() {
        return branchOfficesList;
    }

    public void setBranchOfficesList(List<BranchOffices> branchOfficesList) {
        this.branchOfficesList = branchOfficesList;
    }

    @XmlTransient
    public List<Users> getUsersList() {
        return usersList;
    }

    public void setUsersList(List<Users> usersList) {
        this.usersList = usersList;
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
        if (!(object instanceof Clinics)) {
            return false;
        }
        Clinics other = (Clinics) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.innotech.xraymanagerapp.model.Clinics[ id=" + id + " ]";
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    @XmlTransient
    public List<EmailConfig> getEmailConfigList() {
        return emailConfigList;
    }

    public void setEmailConfigList(List<EmailConfig> emailConfigList) {
        this.emailConfigList = emailConfigList;
    }

    public Boolean getClinicType() {
        return clinicType;
    }

    public void setClinicType(Boolean clinicType) {
        this.clinicType = clinicType;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }
    
}
