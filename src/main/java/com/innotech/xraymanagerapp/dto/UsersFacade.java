/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.dto;

import com.innotech.xraymanagerapp.controller.UsersController;
import com.innotech.xraymanagerapp.controller.util.JsfUtil;
import com.innotech.xraymanagerapp.model.Users;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

/**
 *
 * @author X-Ray
 */
@Stateless
public class UsersFacade extends AbstractFacade<Users> {

    @PersistenceContext(unitName = "com.innotech_xraymanagerApp_war_1.0PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UsersFacade() {
        super(Users.class);
    }

    public Users getUserByUserName(String username) {
        try {
            return getEntityManager()
                    .createNamedQuery("Users.findByUsername", Users.class)
                    .setParameter("username", username)
                    .getSingleResult();
        } catch (NoResultException | NonUniqueResultException e) {
            Logger.getLogger(UsersFacade.class.getName()).log(Level.SEVERE, e.getMessage());
//            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("FailLogin"), ResourceBundle.getBundle("/Bundle").getString("UserNotFound"));
            return null;
        }
    }

    public Users getUserByToken(String token) {
        try {
            return getEntityManager()
                    .createNamedQuery("Users.findByToken", Users.class)
                    .setParameter("token", token)
                    .getSingleResult();
        } catch (NoResultException | NonUniqueResultException e) {
            Logger.getLogger(UsersFacade.class.getName()).log(Level.SEVERE, e.getMessage());
//            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("FailLogin"), ResourceBundle.getBundle("/Bundle").getString("UserNotFound"));
            return null;
        }
    }
/**
     * Return a clinic list by user
     *
     * @param clinicId the clinic related to the user that is the administrator
     * of that clinic
     * @return
     */
    public List<Users> findUsersByClinicAdmin(Integer clinicId) {
        try {
            return getEntityManager()
                    .createNamedQuery("Users.findByClinicId", Users.class)
                    .setParameter("clinicId", clinicId)
                    .getResultList();
        } catch (NoResultException | NonUniqueResultException e) {
            Logger.getLogger(PermisionsFacade.class.getName()).log(Level.SEVERE, e.getMessage());
            return null;
        }
    }
}
