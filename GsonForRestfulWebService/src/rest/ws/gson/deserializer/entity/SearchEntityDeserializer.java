/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.ws.gson.deserializer.entity;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import static rest.ws.dao.DAOFactory.*;
import static rest.ws.gson.GsonUtils.*;
import java.lang.reflect.Type;
import javax.persistence.EntityManager;

/**
 *
 * @author alexander.escalona
 */
public class SearchEntityDeserializer<T> implements JsonDeserializer<T> {
    private final EntityManager entityManager;
    private final EntityMetadata entityMetadata;
    private final Class<T> clazz;
    
    public SearchEntityDeserializer(Class<T> clazz, EntityManager entityManager, EntityMetadata entityMetadata) {
        this.entityManager = entityManager;
        this.entityMetadata = entityMetadata;
        this.clazz = clazz;
    }
    
    @Override
    public T deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {
       JsonObject jsonObject = je.getAsJsonObject();
       Object id;
       String property = entityMetadata.getPropertyId();
       Class propertyType = entityMetadata.getPropertyIdType();
       if(jsonObject.has(property)){
           id = castProperty(jsonObject, property, propertyType);
           if(id == null)
               return null;
           else return createEntityDAO(clazz, entityManager).find(id);
       }
       else{
           throw new JsonParseException(String.format("Bad json configuration, '%s' property is missed", property));
       }
    }
    
}
