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
@Table(name = "DicomServers")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DicomServers.findAll", query = "SELECT d FROM DicomServers d")
    , @NamedQuery(name = "DicomServers.findByServerId", query = "SELECT d FROM DicomServers d WHERE d.serverId = :serverId")
    , @NamedQuery(name = "DicomServers.findByServerName", query = "SELECT d FROM DicomServers d WHERE d.serverName = :serverName")
    , @NamedQuery(name = "DicomServers.findByHost", query = "SELECT d FROM DicomServers d WHERE d.host = :host")
    , @NamedQuery(name = "DicomServers.findByPort", query = "SELECT d FROM DicomServers d WHERE d.port = :port")
    , @NamedQuery(name = "DicomServers.findByDescription", query = "SELECT d FROM DicomServers d WHERE d.description = :description")
    , @NamedQuery(name = "DicomServers.findByCreationDate", query = "SELECT d FROM DicomServers d WHERE d.creationDate = :creationDate")
    , @NamedQuery(name = "DicomServers.findByStatus", query = "SELECT d FROM DicomServers d WHERE d.status = :status")})
public class DicomServers implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "sqlite_DicomServers")
    @TableGenerator(name = "sqlite_DicomServers", table = "sqlite_sequence",
            pkColumnName = "name", valueColumnName = "seq",
            pkColumnValue = "DicomServers",
            initialValue = 1, allocationSize = 1)
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ServerId")
    private Integer serverId;
    @Size(max = 50)
    @Column(name = "ServerName")
    private String serverName;
    @Basic(optional = false)
    @NotNull
    @Size(max = 50)
    @Column(name = "AETitle")
    private String aeTitle;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "Host")
    private String host;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Port")
    private int port;
    @Size(max = 500)
    @Column(name = "Description")
    private String description;
    @Basic(optional = false)
    @NotNull
    @Column(name = "CreationDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Status")
    private boolean status;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dicomServerId", fetch = FetchType.LAZY)
    private List<DicomSent> dicomSentList;

    public DicomServers() {
    }

    public DicomServers(Integer serverId) {
        this.serverId = serverId;
    }

    public DicomServers(Integer serverId, String host, int port, Date creationDate, boolean status) {
        this.serverId = serverId;
        this.host = host;
        this.port = port;
        this.creationDate = creationDate;
        this.status = status;
    }

    public Integer getServerId() {
        return serverId;
    }

    public void setServerId(Integer serverId) {
        this.serverId = serverId;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    @XmlTransient
    public List<DicomSent> getDicomSentList() {
        return dicomSentList;
    }

    public void setDicomSentList(List<DicomSent> dicomSentList) {
        this.dicomSentList = dicomSentList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (serverId != null ? serverId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DicomServers)) {
            return false;
        }
        DicomServers other = (DicomServers) object;
        if ((this.serverId == null && other.serverId != null) || (this.serverId != null && !this.serverId.equals(other.serverId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.innotech.xraymanagerapp.model.DicomServers[ serverId=" + serverId + " ]";
    }

    public String getAeTitle() {
        return aeTitle;
    }

    public void setAeTitle(String aeTitle) {
        this.aeTitle = aeTitle;
    }
    
}
