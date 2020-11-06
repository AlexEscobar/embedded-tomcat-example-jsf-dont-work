/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.dto;

import com.innotech.xraymanagerapp.model.TeethNumbers;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author X-Ray
 */
@Stateless
public class TeethNumbersFacade extends AbstractFacade<TeethNumbers> {

    @PersistenceContext(unitName = "com.innotech_xraymanagerApp_war_1.0PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TeethNumbersFacade() {
        super(TeethNumbers.class);
    }
    
    public List<TeethNumbers> findBySpecies(Integer speciesId){
        return getEntityManager()
                .createNamedQuery("TeethNumbers.findBySpeciesId", TeethNumbers.class)
                .setParameter("status", true)
                .setParameter("speciesId", speciesId)
                .getResultList();
    }
    
    
}
