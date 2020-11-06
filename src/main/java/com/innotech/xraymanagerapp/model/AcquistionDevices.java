/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Hi
 */
@Entity
@Table(name = "AcquistionDevices")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AcquistionDevices.findAll", query = "SELECT a FROM AcquistionDevices a"),
    @NamedQuery(name = "AcquistionDevices.findById", query = "SELECT a FROM AcquistionDevices a WHERE a.id = :id"),
    @NamedQuery(name = "AcquistionDevices.findByName", query = "SELECT a FROM AcquistionDevices a WHERE a.name = :name"),
    @NamedQuery(name = "AcquistionDevices.findByBrand", query = "SELECT a FROM AcquistionDevices a WHERE a.brand = :brand"),
    @NamedQuery(name = "AcquistionDevices.findBySerialNumber", query = "SELECT a FROM AcquistionDevices a WHERE a.serialNumber = :serialNumber"),
    @NamedQuery(name = "AcquistionDevices.findByType", query = "SELECT a FROM AcquistionDevices a WHERE a.type = :type"),
    @NamedQuery(name = "AcquistionDevices.findByColumnCellSpacing", query = "SELECT a FROM AcquistionDevices a WHERE a.columnCellSpacing = :columnCellSpacing"),
    @NamedQuery(name = "AcquistionDevices.findByRowCellSpacing", query = "SELECT a FROM AcquistionDevices a WHERE a.rowCellSpacing = :rowCellSpacing"),
    @NamedQuery(name = "AcquistionDevices.findByHeight", query = "SELECT a FROM AcquistionDevices a WHERE a.height = :height"),
    @NamedQuery(name = "AcquistionDevices.findByWitdh", query = "SELECT a FROM AcquistionDevices a WHERE a.witdh = :witdh"),
    @NamedQuery(name = "AcquistionDevices.findByTimeout", query = "SELECT a FROM AcquistionDevices a WHERE a.timeout = :timeout"),
    @NamedQuery(name = "AcquistionDevices.findByOutputPath", query = "SELECT a FROM AcquistionDevices a WHERE a.outputPath = :outputPath"),
    @NamedQuery(name = "AcquistionDevices.findByEntryDate", query = "SELECT a FROM AcquistionDevices a WHERE a.entryDate = :entryDate"),
    @NamedQuery(name = "AcquistionDevices.findByStatus", query = "SELECT a FROM AcquistionDevices a WHERE a.status = :status"),
    @NamedQuery(name = "AcquistionDevices.findByStatusAndType", query = "SELECT a FROM AcquistionDevices a WHERE a.status = :status AND a.type = :type"),
    @NamedQuery(name = "AcquistionDevices.findByUserId", query = "SELECT a FROM AcquistionDevices a WHERE a.userId = :userId")})
public class AcquistionDevices implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id    
    @GeneratedValue(generator = "sqlite_AcquistionDevices")
    @TableGenerator(name = "sqlite_AcquistionDevices", table = "sqlite_sequence",
            pkColumnName = "name", valueColumnName = "seq",
            pkColumnValue = "AcquistionDevices",
            initialValue = 1, allocationSize = 1)
//    @GeneratedValue(strategy = GenerationType.IDENTITY)// Use it with SQL Server identity columns
    @Column(name = "Id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "Name")
    private String name;
    @Size(max = 50)
    @Column(name = "Brand")
    private String brand;
    @Size(max = 50)
    @Column(name = "SerialNumber")
    private String serialNumber;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "Type")
    private String type;
    @Size(max = 16)
    @Column(name = "ColumnCellSpacing")
    private String columnCellSpacing;
    @Size(max = 16)
    @Column(name = "RowCellSpacing")
    private String rowCellSpacing;
    @Size(max = 8)
    @Column(name = "Height")
    private String height;
    @Size(max = 8)
    @Column(name = "Witdh")
    private String witdh;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Timeout")
    private int timeout;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "OutputPath")
    private String outputPath;
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

    public AcquistionDevices() {
    }

    public AcquistionDevices(Integer id) {
        this.id = id;
    }

    public AcquistionDevices(Integer id, String name, String type, int timeout, String outputPath, boolean status, int userId) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.timeout = timeout;
        this.outputPath = outputPath;
        this.status = status;
        this.userId = userId;
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

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getColumnCellSpacing() {
        return columnCellSpacing;
    }

    public void setColumnCellSpacing(String columnCellSpacing) {
        this.columnCellSpacing = columnCellSpacing;
    }

    public String getRowCellSpacing() {
        return rowCellSpacing;
    }

    public void setRowCellSpacing(String rowCellSpacing) {
        this.rowCellSpacing = rowCellSpacing;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWitdh() {
        return witdh;
    }

    public void setWitdh(String witdh) {
        this.witdh = witdh;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AcquistionDevices)) {
            return false;
        }
        AcquistionDevices other = (AcquistionDevices) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.innotech.xraymanagerapp.model.AcquistionDevices[ id=" + id + " ]";
    }
    
}
