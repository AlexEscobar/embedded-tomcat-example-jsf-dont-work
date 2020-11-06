/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.model;

import java.io.Serializable;

/**
 *
 * @author AEscobarL
 * @param <T> Entity class
 */
public class AbstractSelectedAnnotatePart<T> implements Serializable {
    
    private T annotatePart;
    private boolean isSelected;
    private boolean isFromDB;// flag used to let the system know if the current object was got from the database.
    private boolean isFromSelectAll;// flag used to let the system know if the current object is created on select all function.
    private String id;

   
    public AbstractSelectedAnnotatePart(String id, boolean isSelected, T teethNumber, boolean isFromDB) {
        this.annotatePart = teethNumber;
        this.isSelected = isSelected;
        this.id = id;
        this.isFromDB = isFromDB;
    }
   
    public AbstractSelectedAnnotatePart(String id, boolean isSelected) {
        this.isSelected = isSelected;
        this.id = id;
    }
    
    public T getAnnotatePart() {
        return annotatePart;
    }

    public void setAnnotatePart(T annotatePart) {
        this.annotatePart = annotatePart;
    }

    public boolean isIsSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isIsFromDB() {
        return isFromDB;
    }

    public void setIsFromDB(boolean isFromDB) {
        this.isFromDB = isFromDB;
    }

    public boolean isIsFromSelectAll() {
        return isFromSelectAll;
    }

    public void setIsFromSelectAll(boolean isFromSelectAll) {
        this.isFromSelectAll = isFromSelectAll;
    }
    
}
