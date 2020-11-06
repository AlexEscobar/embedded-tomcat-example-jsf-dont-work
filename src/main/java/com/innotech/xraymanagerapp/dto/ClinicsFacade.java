/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.dto;

import com.innotech.xraymanagerapp.model.Clinics;
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
public class ClinicsFacade extends AbstractFacade<Clinics> {

    @PersistenceContext(unitName = "com.innotech_xraymanagerApp_war_1.0PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ClinicsFacade() {
        super(Clinics.class);
    }
    
    /**
     * Return a clinic list by user
     *
     * @param userId
     * @return
     */
    public List<Clinics> findClinicByUserId(Integer userId) {
        try {
            return getEntityManager()
                    .createNamedQuery("Clinics.findByUserAndStatus", Clinics.class)
                    .setParameter("userId", userId)
                    .getResultList();
        } catch (NoResultException | NonUniqueResultException e) {
            Logger.getLogger(PermisionsFacade.class.getName()).log(Level.SEVERE, e.getMessage());
            return null;
        }
    }
}
