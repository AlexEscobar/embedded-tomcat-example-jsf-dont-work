/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.dto;

import com.innotech.xraymanagerapp.model.Breeds;
import com.innotech.xraymanagerapp.model.Permisions;
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
public class BreedsFacade extends AbstractFacade<Breeds> {

    @PersistenceContext(unitName = "com.innotech_xraymanagerApp_war_1.0PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public BreedsFacade() {
        super(Breeds.class);
    }
    

    /**
     * Calls a stored procedure witch receives specieId as a parameter and returns the 
     * breed list of that specie
     * @param specieId
     * @return breed list by specie ID
     */
    public List<Breeds> getBreedsBySpecie(Integer specieId) {
        try {
            List<Breeds> tList = getEntityManager()
                    .createNamedQuery("Breeds.findBySpeciesId", Breeds.class)
                    .setParameter("speciesId", specieId)
                    .getResultList();
            return tList;
        } catch (NoResultException | NonUniqueResultException e) {
            Logger.getLogger(PermisionsFacade.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }
    
}
