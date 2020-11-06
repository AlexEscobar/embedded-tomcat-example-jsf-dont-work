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
 */
public class SelectedBodyPart extends AbstractSelectedAnnotatePart<BodyParts> implements Serializable {
    
    public SelectedBodyPart(String id, boolean isSelected, BodyParts teethNumber, boolean isFromDB) {
        super(id, isSelected, teethNumber, isFromDB);
    }
    
    
    public SelectedBodyPart(String id, boolean isSelected) {
        super(id, isSelected);
    }
    
  
}
