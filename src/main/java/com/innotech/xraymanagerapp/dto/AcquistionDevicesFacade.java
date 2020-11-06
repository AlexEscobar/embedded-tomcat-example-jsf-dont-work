/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.dto;

import com.innotech.xraymanagerapp.model.AcquistionDevices;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Alexander Escobar L.
 */
@Stateless
public class AcquistionDevicesFacade extends AbstractFacade<AcquistionDevices> {

    @PersistenceContext(unitName = "com.innotech_xraymanagerApp_war_1.0PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AcquistionDevicesFacade() {
        super(AcquistionDevices.class);
    }

    /**
     * return the current active acquisition device by type: either Flat panel or Dental sensor
     * @param deviceType
     * @return 
     */
    public AcquistionDevices getCurrentByType(String deviceType){
        try {
            AcquistionDevices currentAnnotation = (AcquistionDevices) getEntityManager()
                    .createNamedQuery("AcquistionDevices.findByStatusAndType", AcquistionDevices.class)
                    .setParameter("status", true)
                    .setParameter("type", deviceType)
                    .getSingleResult();
            return currentAnnotation;
        } catch (NoResultException | NonUniqueResultException e) {
            Logger.getLogger(AcquistionDevicesFacade.class.getName()).log(Level.SEVERE, e.getMessage());
            return null;
        }
    }    
}
