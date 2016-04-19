/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.client.gson.message;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import rest.gson.common.Link;
import rest.gson.common.Log;
import static rest.gson.common.MessageReservedWord.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author alexander.escalona
 */
public class ResponseGsonDeserializer<T> implements JsonDeserializer<ResponseMessage<T>>{
    private final Class<T> clazz;
    private JsonDeserializer<T> deserializer;

    public ResponseGsonDeserializer(Class<T> clazz) {
        this.clazz = clazz;
    }

    
    
    public JsonDeserializer<T> getDeserializer() {
        return deserializer;
    }

    public void setDeserializer(JsonDeserializer<T> deserializer) {
        this.deserializer = deserializer;
    }
    
    @Override
    public ResponseMessage<T> deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {
        ResponseMessage<T> response = new ResponseMessage<>();
        JsonObject jsonObject = je.getAsJsonObject();
        if(jsonObject.has(ACTION))
            response.visitAction(jsonObject.get(ACTION).getAsString());
        if(jsonObject.has(DATA_TYPE))
            response.visitDataType(jsonObject.get(DATA_TYPE).getAsString());
        if(jsonObject.has(LOG_LIST)){
            Type logListType = new TypeToken<ArrayList<Log>>(){}.getType();
            List<Log> logs = jdc.deserialize(jsonObject.get(LOG_LIST), logListType);
            logs.stream().forEach((log) -> {
                response.visitLog(log);
            });
        }
        if(jsonObject.has(LINKS)){
            deserializeLinks(jsonObject.get(LINKS)).stream().forEach((link) -> {
                response.visitPaginationLink(link);
            });
        }
        if(jsonObject.has(DATA_CONTAINER)){
            response.visitDataContainer(jsonObject.get(DATA_CONTAINER).getAsString());
            if(jsonObject.has(DATA)){
                switch(jsonObject.get(DATA_CONTAINER).getAsString()){
                    case LIST_CONTAINER:
                        JsonArray arrayData = jsonObject.get(DATA).getAsJsonArray();
                        int index = 0;
                        for(JsonElement e : arrayData){
                            deserializeData(response, index++, e, jdc);
                        }
                        break;
                    case OBJECT_CONTAINER:
                        deserializeData(response, 0, jsonObject.get(DATA), jdc);
                        break;
                        
                }
            }
        }
        return response;
    }
    
    private void deserializeData(ResponseMessage<T> response, int index, JsonElement je, JsonDeserializationContext jdc){
        T element = deserializeDataItem(je, jdc);
        response.visitDataItem(element);
        
        JsonObject jo = je.getAsJsonObject();
        if(jo.has(LINKS)){
            deserializeLinks(jo.get(LINKS)).stream().forEach((link) -> {
                response.visitDataItemLink(link, index);
            });
        }
    }
    
    private T deserializeDataItem(JsonElement je, JsonDeserializationContext jdc){
        if(deserializer != null)
            return deserializer.deserialize(je, clazz, jdc);
        else return jdc.deserialize(je, clazz);
    }
    
    private List<Link> deserializeLinks(JsonElement je){
        List<Link> links = new ArrayList<>();
        if(je.isJsonArray()){
            je.getAsJsonArray().forEach((e) -> {
                JsonObject jo = e.getAsJsonObject();
                Link.LinkBuilder builder = new Link.LinkBuilder(jo.get(HREF).getAsString());
                links.add(deserializeLink(builder, jo));
            });
        }
        else{
            JsonObject jo = je.getAsJsonObject();
            jo.entrySet().stream().forEach((entry) -> {
                Link.LinkBuilder builder = new Link.LinkBuilder(entry.getKey());
                links.add(deserializeLink(builder, entry.getValue().getAsJsonObject()));
            });
        }
        return links;
    }
    
    private Link deserializeLink(Link.LinkBuilder builder, JsonObject jo){
        for(Map.Entry<String, JsonElement> entry : jo.entrySet()){
            switch(entry.getKey()){
                case HREF:
                    break;
                case REL:
                    builder.rel(entry.getValue().getAsString());
                    break;
                case METHOD:
                    builder.method(entry.getValue().getAsString());
                    break;
                default:
                    builder.addOptionalData(entry.getKey(), entry.getValue().getAsString());
                    break;
            }
        }
        return builder.build();
    }
    
}
