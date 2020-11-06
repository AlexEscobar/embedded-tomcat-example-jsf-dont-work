/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.dto;

import com.innotech.xraymanagerapp.model.HighVoltageGenerators;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Hi
 */
@Stateless
public class HighVoltageGeneratorsFacade extends AbstractFacade<HighVoltageGenerators> {

    @PersistenceContext(unitName = "com.innotech_xraymanagerApp_war_1.0PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public HighVoltageGeneratorsFacade() {
        super(HighVoltageGenerators.class);
    }
    
    
    /**
     * Returns the High Voltage Generator that is active, that is the one the clinic is currently using.
     * @return 
     */
    public HighVoltageGenerators findCurrentGenerator(){
        try {
            HighVoltageGenerators currentGenerator = getEntityManager()
                     .createNamedQuery("HighVoltageGenerators.findByStatus", HighVoltageGenerators.class)
                    .setParameter("status", true)
                    .getSingleResult();
            return currentGenerator;
        } catch (javax.ejb.EJBException | NoResultException | NonUniqueResultException e) {
            Logger.getLogger(AnnotationsFacade.class.getName()).log(Level.SEVERE, e.getMessage());
            return null;
        }
    }
    
}
