/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.client.gson.message;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.reflect.TypeToken;
import static rest.gson.common.MessageReservedWord.*;
import java.lang.reflect.Type;

/**
 *
 * @author alexander.escalona
 */
public class MessageUtils {
    public static <T> ResponseMessage<T> deserializeResponse(Class<T> clazz, String response){
        ResponseGsonDeserializer<T> responseDeserializer = new ResponseGsonDeserializer<>(clazz);
        GsonBuilder builder = new GsonBuilder();
        Type type = new TypeToken<ResponseGsonDeserializer<T>>(){}.getType();
        builder.registerTypeAdapter(type, responseDeserializer);
        return builder.create().fromJson(response, type);
    }
    
    public static <T> ResponseMessage<T> deserializeResponse(Class<T> clazz, String response, JsonDeserializer<T> deserializer){
        ResponseGsonDeserializer<T> responseDeserializer = new ResponseGsonDeserializer<>(clazz);
        responseDeserializer.setDeserializer(deserializer);
        GsonBuilder builder = new GsonBuilder();
        Type type = new TypeToken<ResponseGsonDeserializer<T>>(){}.getType();
        builder.registerTypeAdapter(type, responseDeserializer);
        return builder.create().fromJson(response, type);
    }
    
    public static String buildMessageRequestAsJson(String action, String dataType, String data){
        if(action == null)
            throw new IllegalArgumentException("The action can no te be null");
        switch(action){
            case CREATE:
            case RETRIEVE:
            case UPDATE:
            case DELETE:
                if(dataType == null)
                    throw new IllegalArgumentException("The data type can no te be null");
                return String.format(
                    "{\"%s\":\"%s\",\"%s\":\"%s\",\"%s\":%s}",
                    ACTION, action,
                    DATA_TYPE, dataType,
                    DATA, data);
            default:
                throw new IllegalArgumentException(String.format("The '%s' is not a valid action.", action));
        }
    }
    
    public static String builSearchByIdJsonData(String idName, String value){
        return String.format("{\"%s\":\"%s\"}", idName, value);
    }
}
