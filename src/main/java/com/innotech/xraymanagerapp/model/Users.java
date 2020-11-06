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
@Table(name = "Users")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Users.findAll", query = "SELECT u FROM Users u"),
    @NamedQuery(name = "Users.findById", query = "SELECT u FROM Users u WHERE u.id = :id"),
    @NamedQuery(name = "Users.findByUsername", query = "SELECT u FROM Users u WHERE u.username = :username AND u.status = true"),
    @NamedQuery(name = "Users.findByToken", query = "SELECT u FROM Users u WHERE u.token = :token AND u.status = true"),
    @NamedQuery(name = "Users.findByFName", query = "SELECT u FROM Users u WHERE u.fName = :fName"),
    @NamedQuery(name = "Users.findByLName", query = "SELECT u FROM Users u WHERE u.lName = :lName"),
    @NamedQuery(name = "Users.findBySignupDate", query = "SELECT u FROM Users u WHERE u.signupDate = :signupDate"),
    @NamedQuery(name = "Users.findByStatus", query = "SELECT u FROM Users u WHERE u.status = :status"),
    @NamedQuery(name = "Users.findByIsOwner", query = "SELECT u FROM Users u WHERE u.isOwner = :isOwner"),
    @NamedQuery(name = "Users.findByPhone", query = "SELECT u FROM Users u WHERE u.phone = :phone"),
    @NamedQuery(name = "Users.findByEmail", query = "SELECT u FROM Users u WHERE u.email = :email"),
    @NamedQuery(name = "Users.findBySex", query = "SELECT u FROM Users u WHERE u.sex = :sex"),
    @NamedQuery(name = "Users.findByBirthDate", query = "SELECT u FROM Users u WHERE u.birthDate = :birthDate"),
    @NamedQuery(name = "Users.findByLastIP", query = "SELECT u FROM Users u WHERE u.lastIP = :lastIP"),
    @NamedQuery(name = "Users.findByClinicId", query = "SELECT u FROM Users u WHERE u.clinicId.id = :clinicId"),
    @NamedQuery(name = "Users.findByLastDate", query = "SELECT u FROM Users u WHERE u.lastDate = :lastDate")})
public class Users implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "sqlite_Users")
    @TableGenerator(name = "sqlite_Users", table = "sqlite_sequence",
            pkColumnName = "name", valueColumnName = "seq",
            pkColumnValue = "Users",
            initialValue = 1, allocationSize = 1)
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Size(max = 2147483647)
    @Column(name = "Token")
    private String token;
    @Column(name = "TokenDate")
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date tokenDate;
    @Column(name = "TokenExpDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date tokenExpDate;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userId", fetch = FetchType.LAZY)
    private List<ImageRejection> imageRejectionList;

    @Column(name = "Attempts")
    private short attempts;

    @OneToMany(mappedBy = "userId", fetch = FetchType.LAZY)
    private List<StudyAnnotationState> studyAnnotationStateList;

    @Column(name = "Status")
    private Boolean status;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userId", fetch = FetchType.LAZY)
    private List<Breeds> breedsList;
    @OneToMany(mappedBy = "userId", fetch = FetchType.LAZY)
    private List<ImageAnnotationState> imageAnnotationStateList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userId", fetch = FetchType.LAZY)
    private List<Species> speciesList;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 16)
    @Column(name = "Username")
    private String username;
    @Basic(optional = false)
    @NotNull    
    @Size(min = 4)
    @Column(name = "Password")
    private String password;
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
    
    @Column(name = "SignupDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date signupDate;
    
    @Column(name = "IsOwner")
    private boolean isOwner;
    // @Pattern(regexp="^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{4})$", message="Invalid phone/fax format, should be as xxx-xxx-xxxx")//if the field contains phone or fax number consider using this annotation to enforce field validation
    @Size(max = 150)
    @Column(name = "Phone")
    private String phone;
    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
    @Size(max = 150)
    @Column(name = "Email")
    private String email;
    @Column(name = "Sex")
    private Boolean sex;
    @Column(name = "BirthDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date birthDate;
    @Size(max = 50)
    @Column(name = "LastIP")
    private String lastIP;
    @Basic(optional = false)
    @NotNull
    @Column(name = "LastDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastDate;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userId", fetch = FetchType.LAZY)
    private List<WorkList> workListList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userId", fetch = FetchType.LAZY)
    private List<Images> imagesList;
    @JoinColumn(name = "LPageId", referencedColumnName = "Id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Permisions lPageId;//Landing page Id based on the user permissions 
    @JoinColumn(name = "ClinicId", referencedColumnName = "Id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Clinics clinicId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userId", fetch = FetchType.LAZY)
    private List<Studies> studiesList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userId", fetch = FetchType.LAZY)
    private List<Annotations> annotationsList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userId", fetch = FetchType.LAZY)
    private List<Persons> personsList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "users", fetch = FetchType.EAGER)
    private List<UserProfile> userProfileList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userId", fetch = FetchType.LAZY)
    private List<PetPatients> petPatientsList;
//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userId", fetch = FetchType.LAZY)
//    private List<Breeds> breedsList;
//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userId", fetch = FetchType.LAZY)
//    private List<Species> speciesList;

    public Users() {
    }

    public Users(Integer id) {
        this.id = id;
    }

    public Users(Integer id, String username, String password, String fName, String lName, Date signupDate, boolean status, boolean isOwner, Date lastDate) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fName = fName;
        this.lName = lName;
        this.signupDate = signupDate;
        this.status = status;
        this.isOwner = isOwner;
        this.lastDate = lastDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public Date getSignupDate() {
        return signupDate;
    }

    public void setSignupDate(Date signupDate) {
        this.signupDate = signupDate;
    }


    public boolean getIsOwner() {
        return isOwner;
    }

    public void setIsOwner(boolean isOwner) {
        this.isOwner = isOwner;
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

    public Boolean getSex() {
        return sex;
    }

    public void setSex(Boolean sex) {
        this.sex = sex;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getLastIP() {
        return lastIP;
    }

    public void setLastIP(String lastIP) {
        this.lastIP = lastIP;
    }

    public Date getLastDate() {
        return lastDate;
    }

    public void setLastDate(Date lastDate) {
        this.lastDate = lastDate;
    }

    @XmlTransient
    public List<WorkList> getWorkListList() {
        return workListList;
    }

    public void setWorkListList(List<WorkList> workListList) {
        this.workListList = workListList;
    }

    @XmlTransient
    public List<Images> getImagesList() {
        return imagesList;
    }

    public void setImagesList(List<Images> imagesList) {
        this.imagesList = imagesList;
    }
//
//    @XmlTransient
//    public List<Breeds> getBreedsList() {
//        return breedsList;
//    }
//
//    public void setBreedsList(List<Breeds> breedsList) {
//        this.breedsList = breedsList;
//    }

    public Clinics getClinicId() {
        return clinicId;
    }

    public void setClinicId(Clinics clinicId) {
        this.clinicId = clinicId;
    }

    @XmlTransient
    public List<Studies> getStudiesList() {
        return studiesList;
    }

    public void setStudiesList(List<Studies> studiesList) {
        this.studiesList = studiesList;
    }

    @XmlTransient
    public List<Annotations> getAnnotationsList() {
        return annotationsList;
    }

    public void setAnnotationsList(List<Annotations> annotationsList) {
        this.annotationsList = annotationsList;
    }

    @XmlTransient
    public List<Persons> getPersonsList() {
        return personsList;
    }

    public void setPersonsList(List<Persons> personsList) {
        this.personsList = personsList;
    }

    @XmlTransient
    public List<UserProfile> getUserProfileList() {
        return userProfileList;
    }

    public void setUserProfileList(List<UserProfile> userProfileList) {
        this.userProfileList = userProfileList;
    }

    @XmlTransient
    public List<PetPatients> getPetPatientsList() {
        return petPatientsList;
    }

    public void setPetPatientsList(List<PetPatients> petPatientsList) {
        this.petPatientsList = petPatientsList;
    }
    
//
//    @XmlTransient
//    public List<Species> getSpeciesList() {
//        return speciesList;
//    }
//
//    public void setSpeciesList(List<Species> speciesList) {
//        this.speciesList = speciesList;
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
        if (!(object instanceof Users)) {
            return false;
        }
        Users other = (Users) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.innotech.xraymanagerapp.model.Users[ id=" + id + " ]";
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public Permisions getlPageId() {
        return lPageId;//From permissions table
    }

    public void setlPageId(Permisions lPageId) {
        this.lPageId = lPageId;
    }    

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    @XmlTransient
    public List<Breeds> getBreedsList() {
        return breedsList;
    }

    public void setBreedsList(List<Breeds> breedsList) {
        this.breedsList = breedsList;
    }

    @XmlTransient
    public List<ImageAnnotationState> getImageAnnotationStateList() {
        return imageAnnotationStateList;
    }

    public void setImageAnnotationStateList(List<ImageAnnotationState> imageAnnotationStateList) {
        this.imageAnnotationStateList = imageAnnotationStateList;
    }

    @XmlTransient
    public List<Species> getSpeciesList() {
        return speciesList;
    }

    public void setSpeciesList(List<Species> speciesList) {
        this.speciesList = speciesList;
    }

    @XmlTransient
    public List<StudyAnnotationState> getStudyAnnotationStateList() {
        return studyAnnotationStateList;
    }

    public void setStudyAnnotationStateList(List<StudyAnnotationState> studyAnnotationStateList) {
        this.studyAnnotationStateList = studyAnnotationStateList;
    }

    public short getAttempts() {
        return attempts;
    }

    public void setAttempts(short attempts) {
        this.attempts = attempts;
    }

    @XmlTransient
    public List<ImageRejection> getImageRejectionList() {
        return imageRejectionList;
    }

    public void setImageRejectionList(List<ImageRejection> imageRejectionList) {
        this.imageRejectionList = imageRejectionList;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getTokenDate() {
        return tokenDate;
    }

    public void setTokenDate(Date tokenDate) {
        this.tokenDate = tokenDate;
    }

    public Date getTokenExpDate() {
        return tokenExpDate;
    }

    public void setTokenExpDate(Date tokenExpDate) {
        this.tokenExpDate = tokenExpDate;
    }
}
