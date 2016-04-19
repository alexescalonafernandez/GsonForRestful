/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.ws.dao;

import javax.persistence.EntityManager;

/**
 *
 * @author alexander.escalona
 */
public class DAOFactory {
    public static <T> EntityDAO<T> createEntityDAO(Class<T> clazz, final EntityManager entityManager){
        return new EntityDAO<T>(clazz) {
            @Override
            protected EntityManager getEntityManager() {
                return entityManager;
            }
        };
    }
}
