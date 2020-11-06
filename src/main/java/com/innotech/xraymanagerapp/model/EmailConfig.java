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
 * @author Hi
 */
@Entity
@Table(name = "EmailConfig")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "EmailConfig.findAll", query = "SELECT e FROM EmailConfig e")
    , @NamedQuery(name = "EmailConfig.findById", query = "SELECT e FROM EmailConfig e WHERE e.id = :id")
    , @NamedQuery(name = "EmailConfig.findBySmtpPort", query = "SELECT e FROM EmailConfig e WHERE e.smtpPort = :smtpPort")
    , @NamedQuery(name = "EmailConfig.findByEmailUser", query = "SELECT e FROM EmailConfig e WHERE e.emailUser = :emailUser")
    , @NamedQuery(name = "EmailConfig.findByEmailPassword", query = "SELECT e FROM EmailConfig e WHERE e.emailPassword = :emailPassword")
    , @NamedQuery(name = "EmailConfig.findByEmailFrom", query = "SELECT e FROM EmailConfig e WHERE e.emailFrom = :emailFrom")
    , @NamedQuery(name = "EmailConfig.findByEmailTo", query = "SELECT e FROM EmailConfig e WHERE e.emailTo = :emailTo")
    , @NamedQuery(name = "EmailConfig.findByEmailSubject", query = "SELECT e FROM EmailConfig e WHERE e.emailSubject = :emailSubject")
    , @NamedQuery(name = "EmailConfig.findByEmailMessage", query = "SELECT e FROM EmailConfig e WHERE e.emailMessage = :emailMessage")
    , @NamedQuery(name = "EmailConfig.findByCreationDate", query = "SELECT e FROM EmailConfig e WHERE e.creationDate = :creationDate")
    , @NamedQuery(name = "EmailConfig.findByStatus", query = "SELECT e FROM EmailConfig e WHERE e.status = :status")
    , @NamedQuery(name = "EmailConfig.findByUserOrClinicId", query = "SELECT e FROM EmailConfig e WHERE e.userId = :userId OR e.clinicId.id = :clinicId ")
    , @NamedQuery(name = "EmailConfig.findByClinicId", query = "SELECT e FROM EmailConfig e WHERE e.clinicId.id = :clinicId ")
    , @NamedQuery(name = "EmailConfig.findByUserId", query = "SELECT e FROM EmailConfig e WHERE e.userId = :userId")})
public class EmailConfig implements Serializable {

    @JoinColumn(name = "ClinicId", referencedColumnName = "Id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Clinics clinicId;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "sqlite_EmailConfig")
    @TableGenerator(name = "sqlite_EmailConfig", table = "sqlite_sequence",
            pkColumnName = "name", valueColumnName = "seq",
            pkColumnValue = "EmailConfig",
            initialValue = 1, allocationSize = 1)
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "SmtpPort")
    private int smtpPort;
    @Size(max = 50)
    @Column(name = "HostName")
    private String hostName;
    @Size(max = 50)
    @Column(name = "EmailUser")
    private String emailUser;
    @Size(max = 1000)
    @Column(name = "EmailPassword")
    private String emailPassword;
    @Size(max = 50)
    @Column(name = "EmailFrom")
    private String emailFrom;
    @Size(max = 50)
    @Column(name = "EmailTo")
    private String emailTo;
    @Size(max = 50)
    @Column(name = "EmailSubject")
    private String emailSubject;
    @Size(max = 5000)
    @Column(name = "EmailMessage")
    private String emailMessage;
    @Basic(optional = false)
    @NotNull
    @Column(name = "CreationDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Status")
    private boolean status;
    @Column(name = "UserId")
    private Integer userId;

    public EmailConfig() {
    }

    public EmailConfig(Integer id) {
        this.id = id;
    }

    public EmailConfig(Integer id, int smtpPort, Date creationDate, boolean status) {
        this.id = id;
        this.smtpPort = smtpPort;
        this.creationDate = creationDate;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getSmtpPort() {
        return smtpPort;
    }

    public void setSmtpPort(int smtpPort) {
        this.smtpPort = smtpPort;
    }

    public String getEmailUser() {
        return emailUser;
    }

    public void setEmailUser(String emailUser) {
        this.emailUser = emailUser;
    }

    public String getEmailPassword() {
        return emailPassword;
    }

    public void setEmailPassword(String emailPassword) {
        this.emailPassword = emailPassword;
    }

    public String getEmailFrom() {
        return emailFrom;
    }

    public void setEmailFrom(String emailFrom) {
        this.emailFrom = emailFrom;
    }

    public String getEmailTo() {
        return emailTo;
    }

    public void setEmailTo(String emailTo) {
        this.emailTo = emailTo;
    }

    public String getEmailSubject() {
        return emailSubject;
    }

    public void setEmailSubject(String emailSubject) {
        this.emailSubject = emailSubject;
    }

    public String getEmailMessage() {
        return emailMessage;
    }

    public void setEmailMessage(String emailMessage) {
        this.emailMessage = emailMessage;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
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
        if (!(object instanceof EmailConfig)) {
            return false;
        }
        EmailConfig other = (EmailConfig) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.innotech.xraymanagerapp.model.EmailConfig[ id=" + id + " ]";
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public Clinics getClinicId() {
        return clinicId;
    }

    public void setClinicId(Clinics clinicId) {
        this.clinicId = clinicId;
    }
}
