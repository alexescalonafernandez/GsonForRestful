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
import static rest.ws.gson.GsonUtils.*;
import static rest.gson.common.MessageReservedWord.*;
import rest.ws.gson.message.predicate.JPQLPredicate;
import java.lang.reflect.Type;

/**
 *
 * @author alexander.escalona
 */
public class JPQLFilterPredicateDeserializer implements JsonDeserializer<JPQLPredicate>{

    @Override
    public JPQLPredicate deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {
        JsonObject jsonObject = je.getAsJsonObject();
        StringBuilder sb = new StringBuilder("(");
        boolean flag = false;
        String field = jsonObject.get(FIELD).getAsString();
        Class fieldType = castSimpleName2Class(jsonObject.get(FIELD_TYPE).getAsString());
        JsonObject filter = jsonObject.get(FILTER).getAsJsonObject();
        for(String op : getJPQLOperatorKeys()){
            if(filter.has(op)){
                String value;
                switch(op){
                    case NU_OPERATOR:
                       value = filter.get(op).getAsBoolean() ? "NULL": "NOT NULL";
                       break;
                    case LK_OPERATOR:
                    case NL_OPERATOR:
                        value = castProperty2String(filter, op, String.class);
                        break;
                    default:
                        value = castProperty2String(filter, op, fieldType);
                        break;
                }
                sb = sb.append(String.format("%s %s %s %s", 
                        flag ? " AND" : "", 
                        field, 
                        getJPQLOperator(op), 
                        value));
                flag = true;
            }
        }
        if(flag){
            return new JPQLPredicate(sb.append(")").toString());
        }
        else return null;
    }
    
}
