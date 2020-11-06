/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.model.dicom;

/**
 *
 * @author Alexander Escobar L.
 */
public class ImageSize {
    
    private short rows;
    private short columns;

    public short getRows() {
        return rows;
    }

    public void setRows(short rows) {
        this.rows = rows;
    }

    public short getColumns() {
        return columns;
    }

    public void setColumns(short columns) {
        this.columns = columns;
    }
}
