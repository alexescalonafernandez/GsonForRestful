/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.ws.dao;

import java.util.List;
import javax.persistence.EntityManager;

/**
 *
 * @author alexander.escalona
 */
public abstract class EntityDAO<T>{
    protected final Class<T> entityClass;
    
    public EntityDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }
    
    protected abstract EntityManager getEntityManager();
    
    private String generateQuery(JPQLQuery query){
        StringBuilder sb = new StringBuilder();
        
        String[] data = new String[]{query.getBaseQuery(), query.getConcatWith(), query.getFilter(), query.getGroupBy(), query.getHaving(), query.getOrderBy()};
        String[] keys = new String[]{"", "", "", "GROUP BY", "HAVING", "ORDER BY"};
        for(int i = 0, length = data.length; i < length; i++)
            if(data[i] != null)
                sb.append(String.format(" %s %s ", keys[i], data[i]));
        return sb.toString();
    }
    
    public List<T> filter(JPQLQuery query){
        String baseQuery = generateQuery(query);
        return getEntityManager().createQuery(baseQuery, entityClass).
                getResultList();
    }
    
    public List<T> filter(JPQLQuery query, int startPosition, int length){
        String baseQuery = generateQuery(query);
        return getEntityManager().createQuery(baseQuery, entityClass).
                setFirstResult(startPosition).
                setMaxResults(length).
                getResultList();
    }
    
    public void create(T entity) {
        getEntityManager().persist(entity);
    }

    public void edit(T entity) {
        getEntityManager().merge(entity);
    }

    public void remove(T entity) {
        getEntityManager().remove(getEntityManager().merge(entity));
    }

    public T find(Object id) {
        return getEntityManager().find(entityClass, id);
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
    
    
}
