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
 * @author Alexander Escobar L.
 */
@Entity
@Table(name = "Devices")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Devices.findAll", query = "SELECT d FROM Devices d"),
    @NamedQuery(name = "Devices.findById", query = "SELECT d FROM Devices d WHERE d.id = :id"),
    @NamedQuery(name = "Devices.findByName", query = "SELECT d FROM Devices d WHERE d.name = :name"),
    @NamedQuery(name = "Devices.findByBrand", query = "SELECT d FROM Devices d WHERE d.brand = :brand"),
    @NamedQuery(name = "Devices.findBySerialNumber", query = "SELECT d FROM Devices d WHERE d.serialNumber = :serialNumber"),
    @NamedQuery(name = "Devices.findByType", query = "SELECT d FROM Devices d WHERE d.type = :type"),
    @NamedQuery(name = "Devices.findByColumnCellSpacing", query = "SELECT d FROM Devices d WHERE d.columnCellSpacing = :columnCellSpacing"),
    @NamedQuery(name = "Devices.findByRowCellSpacing", query = "SELECT d FROM Devices d WHERE d.rowCellSpacing = :rowCellSpacing"),
    @NamedQuery(name = "Devices.findByHeight", query = "SELECT d FROM Devices d WHERE d.height = :height"),
    @NamedQuery(name = "Devices.findByWidth", query = "SELECT d FROM Devices d WHERE d.width = :width"),
    @NamedQuery(name = "Devices.findByEntryDate", query = "SELECT d FROM Devices d WHERE d.entryDate = :entryDate"),
    @NamedQuery(name = "Devices.findByStatus", query = "SELECT d FROM Devices d WHERE d.status = :status"),
    @NamedQuery(name = "Devices.findByUserId", query = "SELECT d FROM Devices d WHERE d.userId = :userId")})
public class Devices implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "sqlite_Devices")
    @TableGenerator(name = "sqlite_Devices", table = "sqlite_sequence",
            pkColumnName = "name", valueColumnName = "seq",
            pkColumnValue = "Devices",
            initialValue = 1, allocationSize = 1)
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    @Column(name = "Width")
    private String width;
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

    public Devices() {
    }

    public Devices(Integer id) {
        this.id = id;
    }

    public Devices(Integer id, String name, String type, boolean status, int userId) {
        this.id = id;
        this.name = name;
        this.type = type;
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

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
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
        if (!(object instanceof Devices)) {
            return false;
        }
        Devices other = (Devices) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.innotech.xraymanagerapp.model.Devices[ id=" + id + " ]";
    }
    
}
