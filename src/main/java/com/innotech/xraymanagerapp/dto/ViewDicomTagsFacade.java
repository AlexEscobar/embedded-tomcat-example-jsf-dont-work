/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.dto;

import com.innotech.xraymanagerapp.model.ViewDicomTags;
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
 * @author Hi
 */
@Stateless
public class ViewDicomTagsFacade extends AbstractFacade<ViewDicomTags> {

    @PersistenceContext(unitName = "com.innotech_xraymanagerApp_war_1.0PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ViewDicomTagsFacade() {
        super(ViewDicomTags.class);
    }


    /**
     * Returns all the tags of an image
     *
     * @param imageId
     * @return TagsToShow List
     */
    public ViewDicomTags findByViewImageId(Integer imageId) {
        try {
            return getEntityManager()
                    .createNamedQuery("ViewDicomTags.findById", ViewDicomTags.class)
                    .setParameter("id", imageId)
                    .getSingleResult();
        } catch (NoResultException | NonUniqueResultException e) {
            Logger.getLogger(PermisionsFacade.class.getName()).log(Level.SEVERE, null, e.getMessage());
            return null;
        }
    }

    /**
     * Returns the list of all the tags of all the images that belongs to a given study
     *
     * @param studyId
     * @return TagsToShow List
     */
    public List<ViewDicomTags> getListByStudyId(Integer studyId) {
        try {
            return getEntityManager()
                    .createNamedQuery("ViewDicomTags.findByStudyId", ViewDicomTags.class)
                    .setParameter("studyId", studyId)
                    .getResultList();
        } catch (NoResultException | NonUniqueResultException e) {
            Logger.getLogger(PermisionsFacade.class.getName()).log(Level.SEVERE, null, e.getMessage());
            return null;
        }
    }

    /**
     * Returns the list of all the tags of all the images that belongs to a given study
     *
     * @param studyUID
     * @return TagsToShow List
     */
    public List<ViewDicomTags> getListByStudyUID(String studyUID) {
        try {
            return getEntityManager()
                    .createNamedQuery("ViewDicomTags.findByStudyUID", ViewDicomTags.class)
                    .setParameter("studyUID", studyUID)
                    .getResultList();
        } catch (NoResultException | NonUniqueResultException e) {
            Logger.getLogger(PermisionsFacade.class.getName()).log(Level.SEVERE, null, e.getMessage());
            return null;
        }
    }
    
}
