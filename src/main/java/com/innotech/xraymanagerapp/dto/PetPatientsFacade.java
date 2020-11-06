/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.dto;

import com.innotech.xraymanagerapp.model.PetPatients;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;

/**
 *
 * @author X-Ray
 */
@Stateless
public class PetPatientsFacade extends AbstractFacade<PetPatients> {

    @PersistenceContext(unitName = "com.innotech_xraymanagerApp_war_1.0PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PetPatientsFacade() {
        super(PetPatients.class);
    }
    
    public List<PetPatients> findByOwnerId(Integer ownerID) {
        try {
            List<PetPatients> patientList = getEntityManager()
                    .createNamedQuery("PetPatients.findByOwnerId", PetPatients.class)
                    .setParameter("ownerId", ownerID)
                    .getResultList();
            return patientList;
        } catch (NoResultException | NonUniqueResultException e) {
            Logger.getLogger(PermisionsFacade.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }

    public List<PetPatients> findByDifferentCriteria(String firtsParameter, String secondParameter, String thirdParameter) {
        try {
            System.out.println("SearchPersonPet: " + firtsParameter + " - " + secondParameter + " - " + thirdParameter);
            List<PetPatients> patientList = getEntityManager()
                    .createNativeQuery("{call SearchPersonPet(?,?,?)}", PetPatients.class)
                    .setParameter(1, firtsParameter)
                    .setParameter(2, secondParameter)
                    .setParameter(3, thirdParameter)
                    .getResultList();
            return patientList;
        } catch (NoResultException | NonUniqueResultException e) {
            Logger.getLogger(PermisionsFacade.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }

    public List<PetPatients> findByDifferentCriteria(Integer clinicId, String firtsParameter, String secondParameter, String thirdParameter) {
        try {
            System.out.println("SearchPersonPet: " +clinicId+" - "+ firtsParameter + " - " + secondParameter + " - " + thirdParameter);
            List<PetPatients> patientList = getEntityManager()
                    .createNativeQuery("{call SearchPersonPet(?,?,?,?)}", PetPatients.class)
                    .setParameter(1, clinicId)
                    .setParameter(2, firtsParameter)
                    .setParameter(3, secondParameter)
                    .setParameter(4, thirdParameter)
                    .getResultList();
            return patientList;
        } catch (NoResultException | NonUniqueResultException e) {
            Logger.getLogger(PermisionsFacade.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }

}
