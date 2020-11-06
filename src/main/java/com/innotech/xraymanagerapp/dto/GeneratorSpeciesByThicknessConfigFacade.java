/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.dto;

import com.innotech.xraymanagerapp.model.GeneratorSpeciesByThicknessConfig;
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
public class GeneratorSpeciesByThicknessConfigFacade extends AbstractFacade<GeneratorSpeciesByThicknessConfig> {

    @PersistenceContext(unitName = "com.innotech_xraymanagerApp_war_1.0PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public GeneratorSpeciesByThicknessConfigFacade() {
        super(GeneratorSpeciesByThicknessConfig.class);
    }
    

    /**
     * Return a clinic list by user
     *
     * @param generatorId
     * @param bodyPartId
     * @param bodyPartViewId
     * @param technique
     * @param thickness
     * @return
     */
    public GeneratorSpeciesByThicknessConfig findGeneratorConfiguratorByThickness(Integer generatorId, Integer bodyPartId,
            Integer bodyPartViewId, Integer technique, Integer thickness) {
        try {
            return getEntityManager()
                    .createNamedQuery("GeneratorSpeciesByThicknessConfig.findByThickness", GeneratorSpeciesByThicknessConfig.class)
                    .setParameter("generatorId", generatorId)
                    .setParameter("bodyPartId", bodyPartId)
                    .setParameter("bodyPartViewId", bodyPartViewId)
                    .setParameter("technique", technique)
                    .setParameter("thickness", thickness)
                    .getSingleResult();
        } catch (NoResultException | NonUniqueResultException e) {
            Logger.getLogger(PermisionsFacade.class.getName()).log(Level.SEVERE, e.getMessage());
            return null;
        }
    }
}
