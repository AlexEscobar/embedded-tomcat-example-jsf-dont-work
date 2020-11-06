/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.model;

import java.io.Serializable;
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
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Hi
 */
@Entity
@Table(name = "GeneratorSpeciesBySizeConfig")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "GeneratorSpeciesBySizeConfig.findAll", query = "SELECT g FROM GeneratorSpeciesBySizeConfig g"),
    @NamedQuery(name = "GeneratorSpeciesBySizeConfig.findById", query = "SELECT g FROM GeneratorSpeciesBySizeConfig g WHERE g.id = :id"),
    @NamedQuery(name = "GeneratorSpeciesBySizeConfig.findByEt", query = "SELECT g FROM GeneratorSpeciesBySizeConfig g WHERE g.et = :et"),
    @NamedQuery(name = "GeneratorSpeciesBySizeConfig.findByKv", query = "SELECT g FROM GeneratorSpeciesBySizeConfig g WHERE g.kv = :kv"),
    @NamedQuery(name = "GeneratorSpeciesBySizeConfig.findByMx", query = "SELECT g FROM GeneratorSpeciesBySizeConfig g WHERE g.mx = :mx"),
    @NamedQuery(name = "GeneratorSpeciesBySizeConfig.findByMs", query = "SELECT g FROM GeneratorSpeciesBySizeConfig g WHERE g.ms = :ms"),
    @NamedQuery(name = "GeneratorSpeciesBySizeConfig.findByMa", query = "SELECT g FROM GeneratorSpeciesBySizeConfig g WHERE g.ma = :ma"),
    @NamedQuery(name = "GeneratorSpeciesBySizeConfig.findByFo", query = "SELECT g FROM GeneratorSpeciesBySizeConfig g WHERE g.fo = :fo"),
    @NamedQuery(name = "GeneratorSpeciesBySizeConfig.findByFs", query = "SELECT g FROM GeneratorSpeciesBySizeConfig g WHERE g.fs = :fs"),
    @NamedQuery(name = "GeneratorSpeciesBySizeConfig.findByFi", query = "SELECT g FROM GeneratorSpeciesBySizeConfig g WHERE g.fi = :fi"),
    @NamedQuery(name = "GeneratorSpeciesBySizeConfig.findByFn", query = "SELECT g FROM GeneratorSpeciesBySizeConfig g WHERE g.fn = :fn"),
    @NamedQuery(name = "GeneratorSpeciesBySizeConfig.findBySize", query = "SELECT g FROM GeneratorSpeciesBySizeConfig g "
            + "WHERE g.generatorId.id = :generatorId "
            + "AND g.speciesId.id = :speciesId "
            + "AND g.sizeId.id = :animalSizeId "
            + "AND g.bodyPartId.id = :bodyPartId "
            + "AND g.bodyPartViewId.id = :bodyPartViewId"),
    @NamedQuery(name = "GeneratorSpeciesBySizeConfig.findByThickness", query = "SELECT g FROM GeneratorSpeciesBySizeConfig g WHERE g.thickness = :thickness")})
public class GeneratorSpeciesBySizeConfig implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "sqlite_GeneratorSpeciesBySizeConfig")
    @TableGenerator(name = "sqlite_GeneratorSpeciesBySizeConfig", table = "sqlite_sequence",
            pkColumnName = "name", valueColumnName = "seq",
            pkColumnValue = "GeneratorSpeciesBySizeConfig",
            initialValue = 1, allocationSize = 1)
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "ET")
    private int et;
    @Basic(optional = false)
    @NotNull
    @Column(name = "KV")
    private int kv;
    @Basic(optional = false)
    @NotNull
    @Column(name = "MX")
    private double mx;
    @Basic(optional = false)
    @NotNull
    @Column(name = "MS")
    private double ms;
    @Basic(optional = false)
    @NotNull
    @Column(name = "MA")
    private double ma;
    @Basic(optional = false)
    @NotNull
    @Column(name = "FO")
    private int fo;
    @Basic(optional = false)
    @NotNull
    @Column(name = "FS")
    private int fs;
    @Basic(optional = false)
    @NotNull
    @Column(name = "FI")
    private int fi;
    @Basic(optional = false)
    @NotNull
    @Column(name = "FN")
    private int fn;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Thickness")
    private int thickness;
    @JoinColumn(name = "SizeId", referencedColumnName = "Id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private AnimalSizes sizeId;
    @JoinColumn(name = "BodyPartId", referencedColumnName = "Id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private BodyParts bodyPartId;
    @JoinColumn(name = "BodyPartViewId", referencedColumnName = "Id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private BodyPartViews bodyPartViewId;
    @JoinColumn(name = "GeneratorId", referencedColumnName = "Id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private HighVoltageGenerators generatorId;
    @JoinColumn(name = "SpeciesId", referencedColumnName = "Id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Species speciesId;

    public GeneratorSpeciesBySizeConfig() {
    }

    public GeneratorSpeciesBySizeConfig(Integer id) {
        this.id = id;
    }

    public GeneratorSpeciesBySizeConfig(Integer id, int et, int kv, int mx, int ms, int ma, int fo, int fs, int fi, int fn, int thickness) {
        this.id = id;
        this.et = et;
        this.kv = kv;
        this.mx = mx;
        this.ms = ms;
        this.ma = ma;
        this.fo = fo;
        this.fs = fs;
        this.fi = fi;
        this.fn = fn;
        this.thickness = thickness;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getEt() {
        return et;
    }

    public void setEt(int et) {
        this.et = et;
    }

    public int getKv() {
        return kv;
    }

    public void setKv(int kv) {
        this.kv = kv;
    }

    public double getMx() {
        return mx;
    }

    public void setMx(double mx) {
        this.mx = mx;
    }

    public double getMs() {
        return ms;
    }

    public void setMs(double ms) {
        this.ms = ms;
    }

    public double getMa() {
        return ma;
    }

    public void setMa(double ma) {
        this.ma = ma;
    }

    public int getFo() {
        return fo;
    }

    public void setFo(int fo) {
        this.fo = fo;
    }

    public int getFs() {
        return fs;
    }

    public void setFs(int fs) {
        this.fs = fs;
    }

    public int getFi() {
        return fi;
    }

    public void setFi(int fi) {
        this.fi = fi;
    }

    public int getFn() {
        return fn;
    }

    public void setFn(int fn) {
        this.fn = fn;
    }

    public int getThickness() {
        return thickness;
    }

    public void setThickness(int thickness) {
        this.thickness = thickness;
    }

    public AnimalSizes getSizeId() {
        return sizeId;
    }

    public void setSizeId(AnimalSizes sizeId) {
        this.sizeId = sizeId;
    }

    public BodyParts getBodyPartId() {
        return bodyPartId;
    }

    public void setBodyPartId(BodyParts bodyPartId) {
        this.bodyPartId = bodyPartId;
    }

    public BodyPartViews getBodyPartViewId() {
        return bodyPartViewId;
    }

    public void setBodyPartViewId(BodyPartViews bodyPartViewId) {
        this.bodyPartViewId = bodyPartViewId;
    }

    public HighVoltageGenerators getGeneratorId() {
        return generatorId;
    }

    public void setGeneratorId(HighVoltageGenerators generatorId) {
        this.generatorId = generatorId;
    }

    public Species getSpeciesId() {
        return speciesId;
    }

    public void setSpeciesId(Species speciesId) {
        this.speciesId = speciesId;
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
        if (!(object instanceof GeneratorSpeciesBySizeConfig)) {
            return false;
        }
        GeneratorSpeciesBySizeConfig other = (GeneratorSpeciesBySizeConfig) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.innotech.xraymanagerapp.model.GeneratorSpeciesBySizeConfig[ id=" + id + " ]";
    }

}
