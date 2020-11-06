/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.dto;

import com.innotech.xraymanagerapp.model.Annotations;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import java.util.Date;

/**
 *
 * @author X-Ray
 */
@Stateless
public class AnnotationsFacade extends AbstractFacade<Annotations> {

    @PersistenceContext(unitName = "com.innotech_xraymanagerApp_war_1.0PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AnnotationsFacade() {
        super(Annotations.class);
    }
    
    
    /**
     * return the annotation list of a given study Id
     * @param studyId
     * @return 
     */
    public List<Annotations> findByStudyId(Integer studyId){
        try {
            List<Annotations> patientList = getEntityManager()
                     .createNamedQuery("Annotations.findByStudy", Annotations.class)
                    .setParameter("studyId", studyId)
                    .getResultList();
            return patientList;
        } catch (NoResultException | NonUniqueResultException e) {
            Logger.getLogger(AnnotationsFacade.class.getName()).log(Level.SEVERE, e.getMessage());
            return null;
        }
    }
    
    /**
     * return the current annotation of a given userId and date
     * @param userId
     * @param annotationDate
     * @return 
     */
    public Annotations getCurrentByUser(Integer userId, Date annotationDate){
        try {
            Annotations currentAnnotation = (Annotations) getEntityManager()
                    .createNativeQuery("{call Annotations_GetCurrentByUser(?,?)}", Annotations.class)
                    .setParameter(1, userId)
                    .setParameter(2, annotationDate)
                    .getSingleResult();
            return currentAnnotation;
        } catch (NoResultException | NonUniqueResultException e) {
            Logger.getLogger(AnnotationsFacade.class.getName()).log(Level.SEVERE, e.getMessage());
            return null;
        }
    }
   
    /**
     * Resets all the annotations marked as current
     */
    public int resetAllCurrentAnnotations(){
        try {
            return getEntityManager()
                .createNamedQuery("Annotations.updateCurrent", Annotations.class)
                .executeUpdate();
        } catch (java.lang.IllegalStateException | NoResultException | NonUniqueResultException e) {
            Logger.getLogger(AnnotationsFacade.class.getName()).log(Level.SEVERE, e.getMessage());
        }
        return 0;
    }

    /**
     * return the image name list
     * @param annotationId
     * @return 
     */
    public List<String> getImagesCountByAnnotation(Integer annotationId){
        try {
            List<String> imageNameList = getEntityManager()
                    .createNativeQuery("{call Images_GetCountByAnnotation(?)}", Annotations.class)
                    .setParameter(1, annotationId)
                    .getResultList();
            return imageNameList;
        } catch (NoResultException | NonUniqueResultException e) {
            Logger.getLogger(AnnotationsFacade.class.getName()).log(Level.SEVERE, e.getMessage());
            return null;
        }
    }
}
