/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.ws.gson.deserializer.merge;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import static rest.ws.gson.GsonUtils.*;
import rest.ws.gson.deserializer.entity.CreateEntityDeserializer;
import rest.ws.gson.deserializer.entity.EntityMetadata;
import rest.ws.gson.deserializer.entity.SearchEntityDeserializer;
import javax.persistence.EntityManager;

/**
 *
 * @author alexander.escalona
 */
public abstract class AbstractMergeDeserializer<T> implements JsonDeserializer<T> {
    protected JsonDeserializer<T> deserializer;
    protected EntityManager entityManager;
    
    public AbstractMergeDeserializer() {
    }
    
    public AbstractMergeDeserializer(JsonDeserializer<T> deserializer) {
        this.deserializer = deserializer;
    }

    public JsonDeserializer<T> getDeserializer() {
        return deserializer;
    }

    public void setDeserializer(JsonDeserializer<T> deserializer) {
        this.deserializer = deserializer;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    public <E> E mergeSubEntity(Class<E> subEntityClass, JsonElement je, JsonDeserializationContext jdc){
        EntityMetadata entityMetadata = entityClass2EntityMetadata(subEntityClass);
        JsonDeserializer<E> subEntityDeserializer;
        if(entityMetadata == null){
            subEntityDeserializer = new CreateEntityDeserializer<>(subEntityClass);
        }
        else{
            JsonObject jsonObject = je.getAsJsonObject();
            subEntityDeserializer = jsonObject.has(entityMetadata.getPropertyId()) ?
                    new SearchEntityDeserializer<>(subEntityClass, entityManager, entityMetadata):
                    new CreateEntityDeserializer<>(subEntityClass);
        }
        AbstractMergeDeserializer<E> entityMergeDeserializer = buildMergeDeserializer(subEntityClass, subEntityDeserializer, entityManager);
        if(entityMergeDeserializer != null)
            return entityMergeDeserializer.deserialize(je, subEntityClass, jdc);
        return null;
    }
    
    
    
}
