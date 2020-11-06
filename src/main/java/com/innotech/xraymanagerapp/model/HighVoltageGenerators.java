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
@Table(name = "HighVoltageGenerators")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "HighVoltageGenerators.findAll", query = "SELECT h FROM HighVoltageGenerators h"),
    @NamedQuery(name = "HighVoltageGenerators.findById", query = "SELECT h FROM HighVoltageGenerators h WHERE h.id = :id"),
    @NamedQuery(name = "HighVoltageGenerators.findByModel", query = "SELECT h FROM HighVoltageGenerators h WHERE h.model = :model"),
    @NamedQuery(name = "HighVoltageGenerators.findByDescription", query = "SELECT h FROM HighVoltageGenerators h WHERE h.description = :description"),
    @NamedQuery(name = "HighVoltageGenerators.findByMaxPower", query = "SELECT h FROM HighVoltageGenerators h WHERE h.maxPower = :maxPower"),
    @NamedQuery(name = "HighVoltageGenerators.findByMinKV", query = "SELECT h FROM HighVoltageGenerators h WHERE h.minKV = :minKV"),
    @NamedQuery(name = "HighVoltageGenerators.findByMaxKV", query = "SELECT h FROM HighVoltageGenerators h WHERE h.maxKV = :maxKV"),
    @NamedQuery(name = "HighVoltageGenerators.findByMinMA", query = "SELECT h FROM HighVoltageGenerators h WHERE h.minMA = :minMA"),
    @NamedQuery(name = "HighVoltageGenerators.findByMaxMA", query = "SELECT h FROM HighVoltageGenerators h WHERE h.maxMA = :maxMA"),
    @NamedQuery(name = "HighVoltageGenerators.findByMinMS", query = "SELECT h FROM HighVoltageGenerators h WHERE h.minMS = :minMS"),
    @NamedQuery(name = "HighVoltageGenerators.findByMaxMS", query = "SELECT h FROM HighVoltageGenerators h WHERE h.maxMS = :maxMS"),
    @NamedQuery(name = "HighVoltageGenerators.findByMinMAS", query = "SELECT h FROM HighVoltageGenerators h WHERE h.minMAS = :minMAS"),
    @NamedQuery(name = "HighVoltageGenerators.findByMaxMAS", query = "SELECT h FROM HighVoltageGenerators h WHERE h.maxMAS = :maxMAS"),
    @NamedQuery(name = "HighVoltageGenerators.findByTimeout", query = "SELECT h FROM HighVoltageGenerators h WHERE h.timeout = :timeout"),
    @NamedQuery(name = "HighVoltageGenerators.findByStandardLoadingTime", query = "SELECT h FROM HighVoltageGenerators h WHERE h.standardLoadingTime = :standardLoadingTime"),
    @NamedQuery(name = "HighVoltageGenerators.findByEnableBucky", query = "SELECT h FROM HighVoltageGenerators h WHERE h.enableBucky = :enableBucky"),
    @NamedQuery(name = "HighVoltageGenerators.findByFlatPanelDectTimeout", query = "SELECT h FROM HighVoltageGenerators h WHERE h.flatPanelDectTimeout = :flatPanelDectTimeout"),
    @NamedQuery(name = "HighVoltageGenerators.findByComPort", query = "SELECT h FROM HighVoltageGenerators h WHERE h.comPort = :comPort"),
    @NamedQuery(name = "HighVoltageGenerators.findByEntryDate", query = "SELECT h FROM HighVoltageGenerators h WHERE h.entryDate = :entryDate"),
    @NamedQuery(name = "HighVoltageGenerators.findByStatus", query = "SELECT h FROM HighVoltageGenerators h WHERE h.status = :status"),
    @NamedQuery(name = "HighVoltageGenerators.findByUserId", query = "SELECT h FROM HighVoltageGenerators h WHERE h.userId = :userId")})
public class HighVoltageGenerators implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "sqlite_HighVoltageGenerators")
    @TableGenerator(name = "sqlite_HighVoltageGenerators", table = "sqlite_sequence",
            pkColumnName = "name", valueColumnName = "seq",
            pkColumnValue = "HighVoltageGenerators",
            initialValue = 1, allocationSize = 1)
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "Model")
    private String model;
    @Size(max = 256)
    @Column(name = "Description")
    private String description;
    @Column(name = "MaxPower")
    private Integer maxPower;
    @Basic(optional = false)
    @NotNull
    @Column(name = "MinKV")
    private int minKV;
    @Basic(optional = false)
    @NotNull
    @Column(name = "MaxKV")
    private int maxKV;
    @Basic(optional = false)
    @NotNull
    @Column(name = "MinMA")
    private int minMA;
    @Basic(optional = false)
    @NotNull
    @Column(name = "MaxMA")
    private int maxMA;
    @Basic(optional = false)
    @NotNull
    @Column(name = "MinMS")
    private int minMS;
    @Basic(optional = false)
    @NotNull
    @Column(name = "MaxMS")
    private int maxMS;
    @Basic(optional = false)
    @NotNull
    @Column(name = "MinMAS")
    private long minMAS;
    @Basic(optional = false)
    @NotNull
    @Column(name = "MaxMAS")
    private long maxMAS;
    @Column(name = "Timeout")
    private Integer timeout;
    @Column(name = "StandardLoadingTime")
    private Integer standardLoadingTime;
    @Column(name = "EnableBucky")
    private Boolean enableBucky;
    @Column(name = "FlatPanelDectTimeout")
    private Integer flatPanelDectTimeout;
    @Size(max = 8)
    @Column(name = "ComPort")
    private String comPort;
    @Basic(optional = false)
    @NotNull
    @Column(name = "EntryDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date entryDate;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Status")
    private boolean status;
    @Basic(optional = false)
    @NotNull
    @Column(name = "UserId")
    private int userId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "generatorId", fetch = FetchType.LAZY)
    private List<GeneratorSpeciesBySizeConfig> generatorSpeciesBySizeConfigList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "generatorId", fetch = FetchType.LAZY)
    private List<GeneratorSpeciesByThicknessConfig> generatorSpeciesByThicknessConfigList;

    public HighVoltageGenerators() {
    }

    public HighVoltageGenerators(Integer id) {
        this.id = id;
    }

    public HighVoltageGenerators(Integer id, String model, int minKV, int maxKV, int minMA, int maxMA, int minMS, int maxMS, long minMAS, long maxMAS, Date entryDate, boolean status, int userId) {
        this.id = id;
        this.model = model;
        this.minKV = minKV;
        this.maxKV = maxKV;
        this.minMA = minMA;
        this.maxMA = maxMA;
        this.minMS = minMS;
        this.maxMS = maxMS;
        this.minMAS = minMAS;
        this.maxMAS = maxMAS;
        this.entryDate = entryDate;
        this.status = status;
        this.userId = userId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getMaxPower() {
        return maxPower;
    }

    public void setMaxPower(Integer maxPower) {
        this.maxPower = maxPower;
    }

    public int getMinKV() {
        return minKV;
    }

    public void setMinKV(int minKV) {
        this.minKV = minKV;
    }

    public int getMaxKV() {
        return maxKV;
    }

    public void setMaxKV(int maxKV) {
        this.maxKV = maxKV;
    }

    public int getMinMA() {
        return minMA;
    }

    public void setMinMA(int minMA) {
        this.minMA = minMA;
    }

    public int getMaxMA() {
        return maxMA;
    }

    public void setMaxMA(int maxMA) {
        this.maxMA = maxMA;
    }

    public int getMinMS() {
        return minMS;
    }

    public void setMinMS(int minMS) {
        this.minMS = minMS;
    }

    public int getMaxMS() {
        return maxMS;
    }

    public void setMaxMS(int maxMS) {
        this.maxMS = maxMS;
    }

    public long getMinMAS() {
        return minMAS;
    }

    public void setMinMAS(long minMAS) {
        this.minMAS = minMAS;
    }

    public long getMaxMAS() {
        return maxMAS;
    }

    public void setMaxMAS(long maxMAS) {
        this.maxMAS = maxMAS;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public Integer getStandardLoadingTime() {
        return standardLoadingTime;
    }

    public void setStandardLoadingTime(Integer standardLoadingTime) {
        this.standardLoadingTime = standardLoadingTime;
    }

    public Boolean getEnableBucky() {
        return enableBucky;
    }

    public void setEnableBucky(Boolean enableBucky) {
        this.enableBucky = enableBucky;
    }

    public Integer getFlatPanelDectTimeout() {
        return flatPanelDectTimeout;
    }

    public void setFlatPanelDectTimeout(Integer flatPanelDectTimeout) {
        this.flatPanelDectTimeout = flatPanelDectTimeout;
    }

    public String getComPort() {
        return comPort;
    }

    public void setComPort(String comPort) {
        this.comPort = comPort;
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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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
        if (!(object instanceof HighVoltageGenerators)) {
            return false;
        }
        HighVoltageGenerators other = (HighVoltageGenerators) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.innotech.xraymanagerapp.model.HighVoltageGenerators[ id=" + id + " ]";
    }
    
}
