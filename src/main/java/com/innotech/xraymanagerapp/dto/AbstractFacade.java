/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.dto;

import com.innotech.xraymanagerapp.model.Clinics;
import com.innotech.xraymanagerapp.model.Users;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.Join;

/**
 *
 * @author X-Ray
 * @param <T>
 */
public abstract class AbstractFacade<T> {

    private Class<T> entityClass;

    public AbstractFacade(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected abstract EntityManager getEntityManager();

    public void create(T entity) {
//        Logger.getLogger(entityClass.getName()).log(Level.INFO, "The User {0} is trying to create new registry of {1}, with values: {2}",
//                new Object[]{AuthenticationUtils.getLoggedUser(), entityClass.getName(), entityClass.toString()});
        getEntityManager().persist(entity);
    }

    public T edit(T entity) {
//        Logger.getLogger(entityClass.getName()).log(Level.INFO, "The User {0} is trying to create or edit a {1}, with values: {2}",
//                new Object[]{AuthenticationUtils.getLoggedUser(), entityClass.getName(), entity.toString()});
        return getEntityManager().merge(entity);
    }

    public void remove(T entity) {
        getEntityManager().remove(getEntityManager().merge(entity));
    }

    public void refresh(T entity) {
        getEntityManager().refresh(getEntityManager().merge(entity));
    }

    public T find(Object id) {
        try {
            return getEntityManager().find(entityClass, id);
        } catch (NullPointerException | NoResultException | NonUniqueResultException e) {
            getEntityManager().getTransaction().rollback();
            Logger.getLogger(entityClass.getName()).log(Level.SEVERE, e.getMessage(), e.getMessage());
            return null;
        }
    }

    public List<T> findAll() {
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        return getEntityManager().createQuery(cq).getResultList();
    }

    public List<T> findRange(int[] range) {
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        javax.persistence.Query q = getEntityManager().createQuery(cq);
        q.setMaxResults(range[1] - range[0] + 1);
        q.setFirstResult(range[0]);
        return q.getResultList();
    }

    public int count() {
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        javax.persistence.criteria.Root<T> rt = cq.from(entityClass);
        cq.select(getEntityManager().getCriteriaBuilder().count(rt));
        javax.persistence.Query q = getEntityManager().createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }

    /**
     * Persists multiple entities with high performance on database access and
     * transactions
     *
     * @param entityList lists of entities to be persisted
     * @return true if all entities were correctly persisted
     */
    public boolean persistMulti(List<T> entityList) {
        try {
//            getEntityManager().getTransaction().begin();
            for (T entity : entityList) {
//                Logger.getLogger(entityClass.getName()).log(Level.INFO, "The User {0} is trying to create new registry of {1}, with values: {2}",
//                        new Object[]{AuthenticationUtils.getLoggedUser(), entityClass.getName(), entity.toString()});
                getEntityManager().merge(entity);
                // Optimization #5 - avoid n^2 persist calls
                //for (OrderLine orderLine : order.getOrderLines()) {
                //    em.persist(orderLine);
                //}
            }
//            getEntityManager().getTransaction().commit();
        } catch (NoResultException | NonUniqueResultException e) {
            getEntityManager().getTransaction().rollback();
            Logger.getLogger(PermisionsFacade.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            return false;
        }
        return true;
    }

    /**
     * Restarts cache memory to get database changes immediately
     */
    public void resetCache() {
        getEntityManager().getEntityManagerFactory().getCache().evictAll();
    }

    /**
     * Searches a list of objects with status true = 1 = active
     *
     * @param namedQuery table.column name
     * @param paramName status column
     * @return
     */
    public List<T> findByStatus(String namedQuery, String paramName) {
        return getEntityManager()
                .createNamedQuery(namedQuery, entityClass)
                .setParameter(paramName, true)
                .getResultList();
    }

    /**
     * Searches a list of objects with status true = 1 = active belonging to the
     * current clinic
     *
     * @param namedQuery table.column name
     * @param paramName status column
     * @param clinicId
     * @return
     */
    public List<T> findByStatusAndClinic(String namedQuery, String paramName, Integer clinicId) {
        return getEntityManager()
                .createNamedQuery(namedQuery, entityClass)
                .setParameter("status", true)
                .setParameter("clinicId", clinicId)
                .getResultList();
    }

    /**
     * Return an object list by date range
     *
     * @param initialDate
     * @param finalDate
     * @return
     */
    public List<T> findByDateRange(Date initialDate, Date finalDate) {
        try {
            javax.persistence.criteria.CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            javax.persistence.criteria.CriteriaQuery q = cb.createQuery();
            javax.persistence.criteria.Root<T> c = q.from(entityClass);

            q.select(c);
            javax.persistence.criteria.ParameterExpression<Date> initD = cb.parameter(Date.class);
            javax.persistence.criteria.ParameterExpression<Date> finalD = cb.parameter(Date.class);
            javax.persistence.criteria.ParameterExpression<Integer> cId = cb.parameter(Integer.class);
            q.where(
                    cb.between(c.get("creationDate"), initD, finalD)
            );
            //query execution 
            TypedQuery<T> typedQuery = getEntityManager().createQuery(q);
            typedQuery.setParameter(initD, initialDate);
            typedQuery.setParameter(finalD, finalDate);
            System.out.println(typedQuery.toString());
            System.out.println(getEntityManager().toString());
            return typedQuery.getResultList();
        } catch (NoResultException | NonUniqueResultException e) {
            Logger.getLogger(PermisionsFacade.class.getName()).log(Level.SEVERE, e.getMessage());
            return null;
        }
    }

    /**
     * Return an object list by date range by using Criteria API
     *
     * @param initialDate
     * @param finalDate
     * @param clinicId
     * @return
     */
    public List<T> findByDateRangeAndClinicId(Date initialDate, Date finalDate, Integer clinicId) {
        javax.persistence.criteria.CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        javax.persistence.criteria.CriteriaQuery q = cb.createQuery();
        javax.persistence.criteria.Root<T> c = q.from(entityClass);

        Join<T, Users> user = c.join("userId");
        Join<Users, Clinics> clinic = user.join("clinicId");

        q.select(c);
        javax.persistence.criteria.ParameterExpression<Date> initD = cb.parameter(Date.class);
        javax.persistence.criteria.ParameterExpression<Date> finalD = cb.parameter(Date.class);
        javax.persistence.criteria.ParameterExpression<Integer> cId = cb.parameter(Integer.class);
        q.where(
                cb.equal(clinic.get("id"), user.get("clinicId").get("id")),// join clinics and users
                cb.equal(user.get("id"), c.get("userId").get("id")),// join users and entity
                cb.between(c.get("creationDate"), initD, finalD),
                cb.equal(clinic.get("id"), cId)
        );
        //query execution 
        TypedQuery<T> typedQuery = getEntityManager().createQuery(q);
        typedQuery.setParameter(initD, initialDate);
        typedQuery.setParameter(finalD, finalDate);
        typedQuery.setParameter(cId, clinicId);
        return typedQuery.getResultList();
    }
}
