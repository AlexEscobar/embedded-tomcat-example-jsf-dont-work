/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.dto;

import com.innotech.xraymanagerapp.model.GeneratorSpeciesBySizeConfig;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Alexander Escobar L.
 */
@Stateless
public class GeneratorSpeciesBySizeConfigFacade extends AbstractFacade<GeneratorSpeciesBySizeConfig> {

    @PersistenceContext(unitName = "com.innotech_xraymanagerApp_war_1.0PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public GeneratorSpeciesBySizeConfigFacade() {
        super(GeneratorSpeciesBySizeConfig.class);
    }

    /**
     * Return a clinic list by user
     *
     * @param generatorId
     * @param speciesId
     * @param animalSizeId
     * @param bodyPartId
     * @param bodyPartViewId
//     * @param technique
     * @return
     */
    public GeneratorSpeciesBySizeConfig findGeneratorConfiguratorBySize(Integer generatorId, Integer speciesId, Integer animalSizeId, Integer bodyPartId,
            Integer bodyPartViewId) {
        try {
            return getEntityManager()
                    .createNamedQuery("GeneratorSpeciesBySizeConfig.findBySize", GeneratorSpeciesBySizeConfig.class)
                    .setParameter("generatorId", generatorId)
                    .setParameter("speciesId", speciesId)
                    .setParameter("animalSizeId", animalSizeId)
                    .setParameter("bodyPartId", bodyPartId)
                    .setParameter("bodyPartViewId", bodyPartViewId)
//                    .setParameter("technique", technique)
                    .getSingleResult();
        } catch (NoResultException | NonUniqueResultException e) {
            Logger.getLogger(PermisionsFacade.class.getName()).log(Level.SEVERE, e.getMessage());
            return null;
        }
    }

}
