/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.dto;

import com.innotech.xraymanagerapp.model.Images;
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
 * @author X-Ray
 */
@Stateless
public class ImagesFacade extends AbstractFacade<Images> {

    @PersistenceContext(unitName = "com.innotech_xraymanagerApp_war_1.0PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ImagesFacade() {
        super(Images.class);
    }

    /**
     * return the image name list of a given study ID
     *
     * @param studyId
     * @return
     */
    public List<Images> getImagesByStudy(Integer studyId) {
        try {
            List<Images> imageNameList = getEntityManager()
                    .createNativeQuery("{call Images_GetByStudy(?)}", Images.class)
                    .setParameter(1, studyId)
                    .getResultList();
            return imageNameList;
        } catch (NoResultException | NonUniqueResultException e) {
            Logger.getLogger(ImagesFacade.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }

    /**
     * Searches a list of objects with status true = 1 = active
     *
     * @param namedQuery Images.findByStudyId
     * @param paramName studyId
     * @param paramValue 77
     * @return
     */
    public List<Images> findByStudyId(String namedQuery, String paramName, Integer paramValue) {
        try {
            return getEntityManager()
                    .createNamedQuery(namedQuery, Images.class)
                    .setParameter(paramName, paramValue)
                    .getResultList();
        } catch (NoResultException | NonUniqueResultException e) {
            Logger.getLogger(ImagesFacade.class.getName()).log(Level.SEVERE, null, e.getMessage());
            return null;
        }
    }

    public int deleteSelectedImageById(Integer imageId) {

        try {
            return getEntityManager()
                    .createNamedQuery("Images.deleteById", Images.class)
                    .setParameter("imageId", imageId)
                    .executeUpdate();
        } catch (NoResultException | NonUniqueResultException e) {
            Logger.getLogger(ImagesFacade.class.getName()).log(Level.SEVERE, null, e.getMessage());
            return 0;
        }
    }

    /**
     * Activates an image that is in inactive state
     * @param imageId
     * @return 
     */
    public int activateImageById(Integer imageId) {

        try {
            return getEntityManager()
                    .createNamedQuery("Images.activateById", Images.class)
                    .setParameter("imageId", imageId)
                    .executeUpdate();
        } catch (NoResultException | NonUniqueResultException e) {
            Logger.getLogger(ImagesFacade.class.getName()).log(Level.SEVERE, null, e.getMessage());
            return 0;
        }
    }

}
