/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.ws.gson.deserializer.request.jpql;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import static rest.gson.common.MessageReservedWord.*;
import rest.ws.gson.message.predicate.JPQLPredicate;
import java.lang.reflect.Type;

/**
 *
 * @author alexander.escalona
 */
public class JPQLOrderByPredicateDeserializer implements JsonDeserializer<JPQLPredicate>{

    @Override
    public JPQLPredicate deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {
        JsonObject jsonObject = je.getAsJsonObject();
        String field = jsonObject.get(FIELD).getAsString();
        if(jsonObject.has(ORDER_DIRECTION) && 
                jsonObject.get(ORDER_DIRECTION).getAsString().equals(DESC)){
            return new JPQLPredicate(String.format("%s %s", field, DESC));
        }
        else return new JPQLPredicate(field);
    }
    
}
