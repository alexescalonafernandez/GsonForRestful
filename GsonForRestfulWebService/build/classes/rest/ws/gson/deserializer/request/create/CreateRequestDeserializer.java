/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.ws.gson.deserializer.request.create;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import static rest.ws.gson.GsonUtils.*;
import rest.ws.gson.deserializer.request.RequestDeserializer;
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
public class CreateRequestDeserializer extends RequestDeserializer{

    public CreateRequestDeserializer(EntityManager entityManager, UriInfo uriInfo, ServletContext servletContext) {
        super(entityManager, uriInfo, servletContext);
    }
    
    @Override
    public String getOperationErrorType() {
        return CREATE_FAIL;
    }

    @Override
    public Message getResponse(JsonElement je, Type type, JsonDeserializationContext jdc) {
        return buildCreateEntityRequestDeserializer(
                entityClass, dataType, entityManager).deserialize(je, type, jdc);
    }
    
}
