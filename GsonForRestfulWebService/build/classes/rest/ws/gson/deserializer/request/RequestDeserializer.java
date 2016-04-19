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
import static rest.ws.gson.GsonUtils.*;
import rest.ws.gson.deserializer.request.read.ReadRequestDeserializer;
import rest.gson.common.Log;
import rest.gson.common.LogLevel;
import rest.ws.gson.message.Message;
import static rest.gson.common.MessageReservedWord.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Type;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author alexander.escalona
 */
public abstract class RequestDeserializer implements JsonDeserializer<Message>{
    protected EntityManager entityManager;
    protected String dataType;
    protected Class<?> entityClass;
    protected UriInfo uriInfo;
    protected ServletContext servletContext;

    public RequestDeserializer(EntityManager entityManager, UriInfo uriInfo, ServletContext servletContext) {
        this.entityManager = entityManager;
        this.uriInfo = uriInfo;
        this.servletContext = servletContext;
    }
    
    
    @Override
    public Message deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {
        try{
            JsonObject jsonObject = je.getAsJsonObject();
            if(jsonObject.has(DATA_TYPE)){
                dataType = jsonObject.get(DATA_TYPE).getAsString();
                entityClass = dataType2EntityClass(dataType);
                if(entityClass == null && (this instanceof ReadRequestDeserializer))
                    entityClass = dataType2DtoClass(dataType);
                if(entityClass != null){
                    return getResponse(je, type, jdc);
                }
                else{
                     Log log = new Log.LogBuilder(LogLevel.ERROR).
                             codeKey(String.valueOf(400)).
                             codeStr("Bad Request").
                             userMsg(String.format("Request data type error. The '%s' data type not exists.", dataType)).
                             build();
                        return new Message.MessageBuilder(getOperationErrorType()).dataType(dataType).addLog(log).build();
                }
            }
            else{
                Log log = new Log.LogBuilder(LogLevel.ERROR).
                        codeKey(String.valueOf(400)).
                        codeStr("Bad Request").
                        userMsg(String.format("Request protocol error. The '%s' property is missed", DATA_TYPE)).
                        build();
                return new Message.MessageBuilder(getOperationErrorType()).addLog(log).build();
            }
        }
        catch(Exception ex){
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            Log log = new Log.LogBuilder(LogLevel.ERROR).
                    codeKey(String.valueOf(500)).
                    codeStr(ex.getClass().getCanonicalName()).
                    userMsg(errors.toString()).build();
            return new Message.MessageBuilder(getOperationErrorType()).addLog(log).build();
        }
    }
    
    public abstract String getOperationErrorType();
    public abstract Message getResponse(JsonElement je, Type type, JsonDeserializationContext jdc);
    
}
