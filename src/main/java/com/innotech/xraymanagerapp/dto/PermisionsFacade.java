/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.dto;

import com.innotech.xraymanagerapp.model.Permisions;
import com.innotech.xraymanagerapp.model.Profiles;
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
public class PermisionsFacade extends AbstractFacade<Permisions> {

    @PersistenceContext(unitName = "com.innotech_xraymanagerApp_war_1.0PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PermisionsFacade() {
        super(Permisions.class);
    }

    /**
     * Calls a stored procedure witch receives userId as a parameter and returns the 
     * user permission list for all those permissions that have configured an url
     * @param userId
     * @return permissions list by user ID
     */
    public List<Permisions> getPermissionsByUser(Integer userId) {
        try {
            List<Permisions> tList = getEntityManager()
                    .createNativeQuery("{call PermissionsGetByUserId(?)}", Permisions.class)
                    .setParameter(1, userId)
                    .getResultList();
            return tList;
        } catch (NoResultException | NonUniqueResultException e) {
            Logger.getLogger(PermisionsFacade.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }
}
