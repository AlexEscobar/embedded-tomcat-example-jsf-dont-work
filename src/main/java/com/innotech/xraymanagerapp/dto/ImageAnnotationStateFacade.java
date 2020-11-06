/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.dto;

import com.innotech.xraymanagerapp.model.ImageAnnotationState;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Hi
 */
@Stateless
public class ImageAnnotationStateFacade extends AbstractFacade<ImageAnnotationState> {

    @PersistenceContext(unitName = "com.innotech_xraymanagerApp_war_1.0PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ImageAnnotationStateFacade() {
        super(ImageAnnotationState.class);
    }

    /**
     * Gets the last annotations of a given image id
     *
     * @param imageId
     * @return ImageAnnotationState object
     */
    public ImageAnnotationState findByImageId(Integer imageId) {
        return getEntityManager()
                .createNamedQuery("ImageAnnotationState.findByImageId", ImageAnnotationState.class)
                .setParameter("imageId", imageId)
                .setMaxResults(1)
                .getSingleResult();
    }

    /**
     * Returns the image annotation state list by study id
     *
     * @param studyId
     * @return The annotation state list
     */
    public List<ImageAnnotationState> findByStudyId(Integer studyId) {
        return getEntityManager()
                .createNamedQuery("ImageAnnotationState.findByStudyId", ImageAnnotationState.class)
                .setParameter("studyId", studyId)
                .getResultList();
    }
}
