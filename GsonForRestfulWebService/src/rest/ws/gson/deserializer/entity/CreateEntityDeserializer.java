/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.ws.gson.deserializer.entity;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;

/**
 *
 * @author alexander.escalona
 */
public class CreateEntityDeserializer<T> implements JsonDeserializer<T> {
    private final Class<T> clazz;

    public CreateEntityDeserializer(Class<T> clazz) {
        this.clazz = clazz;
    }
    
    @Override
    public T deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new JsonParseException(ex);
        }
    }
    
}
