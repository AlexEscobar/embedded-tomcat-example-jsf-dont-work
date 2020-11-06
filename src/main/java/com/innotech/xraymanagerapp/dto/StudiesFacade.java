/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.dto;

import com.innotech.xraymanagerapp.model.Studies;
import java.util.Date;
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
public class StudiesFacade extends AbstractFacade<Studies> {

    @PersistenceContext(unitName = "com.innotech_xraymanagerApp_war_1.0PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public StudiesFacade() {
        super(Studies.class);
    }
    
    /**
     * return the study list of a given patient
     * @param patientId
     * @return 
     */
    public List<Studies> findByOwnerId(Integer patientId){
        try {
            List<Studies> patientList = getEntityManager()
                    .createNamedQuery("Studies.findByStatusAndPatientId", Studies.class)
                    .setParameter("patientId", patientId)
                    .getResultList();
            return patientList;
        } catch (NoResultException | NonUniqueResultException e) {
            Logger.getLogger(StudiesFacade.class.getName()).log(Level.SEVERE, e.getMessage());
            return null;
        }
    }
    
    /**
     * return today's study of a given patient
     * @param patientId
     * @param userId
     * @param today Today's date
     * @param tomorrow
     * @return 
     */
    public Studies findCurrentByOwnerId(Integer patientId, Integer userId, Date today, Date tomorrow){
        try {
//            Date now = new Date();
            Studies patient = (Studies) getEntityManager()
                    .createNamedQuery("Studies.findByCreationDateAndPatientId", Studies.class)
                    .setParameter("patientId", patientId)
                    .setParameter("today", today)
                    .setParameter("tomorrow", tomorrow)
                    .getSingleResult();
            return patient;
        } catch (NoResultException | NonUniqueResultException e) {
            Logger.getLogger(StudiesFacade.class.getName()).log(Level.SEVERE, e.getMessage());
            return null;
        }
    }
    
}
