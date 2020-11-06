/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.dto;

import com.innotech.xraymanagerapp.model.EmailConfig;
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
public class EmailConfigFacade extends AbstractFacade<EmailConfig> {

    @PersistenceContext(unitName = "com.innotech_xraymanagerApp_war_1.0PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EmailConfigFacade() {
        super(EmailConfig.class);
    }
    
    
    /**
     * Searches a list of objects with status true = 1 = active and that belong to the current clinic
     *
     * @param clinicId
     * @param userId
     * @return
     */
    public List<EmailConfig> findByStatusAndClinic(Integer clinicId, Integer userId) {
        return getEntityManager()
                .createNamedQuery("EmailConfig.findByUserOrClinicId", EmailConfig.class)
                .setParameter("userId", userId)
                .setParameter("clinicId", clinicId)
                .getResultList();
    }
    /**
     * Return an email list by logged user or clinic where the user belongs to
     *
     * @param userId
     * @param clinicId
     * @return
     */
    public List<EmailConfig> findEmailByUserId(Integer userId, Integer clinicId) {
        try {
            return getEntityManager()
                    .createNamedQuery("EmailConfig.findByUserOrClinicId", EmailConfig.class)
                    .setParameter("userId", userId)
                    .setParameter("clinicId", clinicId)
                    .getResultList();
        } catch (NoResultException | NonUniqueResultException e) {
            Logger.getLogger(PermisionsFacade.class.getName()).log(Level.SEVERE, e.getMessage());
            return null;
        }
    }

    /**
     * Return an email list by logged user or clinic where the user belongs to
     *
     * @param clinicId
     * @return
     */
    public List<EmailConfig> findEmailByClinicId(Integer clinicId) {
        try {
            return getEntityManager()
                    .createNamedQuery("EmailConfig.findByClinicId", EmailConfig.class)
                    .setParameter("clinicId", clinicId)
                    .getResultList();
        } catch (NoResultException | NonUniqueResultException e) {
            Logger.getLogger(PermisionsFacade.class.getName()).log(Level.SEVERE, e.getMessage());
            return null;
        }
    }
    
}
