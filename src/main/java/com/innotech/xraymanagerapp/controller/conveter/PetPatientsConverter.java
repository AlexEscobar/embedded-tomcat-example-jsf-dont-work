/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.controller.conveter;

import com.innotech.xraymanagerapp.controller.PermisionsController;
import com.innotech.xraymanagerapp.controller.PetPatientsController;
import com.innotech.xraymanagerapp.model.Permisions;
import com.innotech.xraymanagerapp.model.PetPatients;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author AlexcobarL
 */
@FacesConverter("petPatientsConverter")
public class PetPatientsConverter implements Converter, Serializable {

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
        if (value == null || value.length() == 0) {
            return null;
        }
        try {
            PetPatientsController controller = (PetPatientsController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "petPatientsController");
            java.lang.Integer key = getKey(value);
            if(key != null)
                return controller.getPetPatients(key);
            else return "";
        } catch (Exception e) {
        }
        return null;
    }

    java.lang.Integer getKey(String value) {
        java.lang.Integer key = null;
        try {
            key = Integer.valueOf(value);
        } catch (NullPointerException | NumberFormatException e) {
        }
        return key;
    }

    String getStringKey(java.lang.Integer value) {
        StringBuilder sb = new StringBuilder();
        sb.append(value);
        return sb.toString();
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
        if (object == null) {
            return "";
        }
        if (object instanceof PetPatients) {
            PetPatients o = (PetPatients) object;
            return getStringKey(o.getId());
        } else {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), PetPatients.class.getName()});
           throw new ConverterException("Value is not a valid ID of PetPatients entity.");
        }
    }
}
