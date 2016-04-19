/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.ws.gson.deserializer.request;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import rest.ws.gson.deserializer.request.create.CreateRequestDeserializer;
import rest.ws.gson.deserializer.request.delete.DeleteRequestDeserializer;
import rest.ws.gson.deserializer.request.read.ReadRequestDeserializer;
import rest.ws.gson.deserializer.request.update.UpdateRequestDeserializer;
import rest.gson.common.Log;
import rest.gson.common.LogLevel;
import rest.ws.gson.message.Message;
import static rest.gson.common.MessageReservedWord.*;
import java.lang.reflect.Type;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author alexander.escalona
 */
public class RequestProcessor implements JsonDeserializer<Message>{
    protected EntityManager entityManager;
    protected UriInfo uriInfo;
    protected ServletContext servletContext;

    public RequestProcessor(EntityManager entityManager, UriInfo uriInfo, ServletContext servletContext) {
        this.entityManager = entityManager;
        this.uriInfo = uriInfo;
        this.servletContext = servletContext;
    }
    
    @Override
    public Message deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {
        JsonObject jsonObject = je.getAsJsonObject();
        if(jsonObject.has(ACTION)){
            switch(jsonObject.get(ACTION).getAsString()){
                case CREATE:
                    CreateRequestDeserializer crd = new CreateRequestDeserializer(entityManager, uriInfo, servletContext);
                    return crd.deserialize(je, Message.class, jdc);
                case UPDATE:
                    UpdateRequestDeserializer urd = new UpdateRequestDeserializer(entityManager, uriInfo, servletContext);
                    return urd.deserialize(je, Message.class, jdc);
                case DELETE:
                    DeleteRequestDeserializer drd = new DeleteRequestDeserializer(entityManager, uriInfo, servletContext);
                    return drd.deserialize(je, Message.class, jdc);
                case RETRIEVE:
                    ReadRequestDeserializer rrd = new ReadRequestDeserializer(entityManager, uriInfo, servletContext);
                    return rrd.deserialize(je, Message.class, jdc);
            }
            return null;
        }
        else{
            Log log = new Log.LogBuilder(LogLevel.ERROR).
                    codeKey(String.valueOf(400)).
                    codeStr("Bad Request").
                    userMsg(String.format("Request protocol error. The '%s' property is missed", ACTION)).
                    build();
            return new Message.MessageBuilder(REQUEST_FAIL).addLog(log).build();
        }
    }
    
}
