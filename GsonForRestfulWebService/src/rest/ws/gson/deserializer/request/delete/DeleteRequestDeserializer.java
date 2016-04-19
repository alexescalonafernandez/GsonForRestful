/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.ws.gson.deserializer.request.delete;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import static rest.ws.gson.GsonUtils.*;
import rest.ws.gson.deserializer.entity.EntityMetadata;
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
public class DeleteRequestDeserializer extends RequestDeserializer{

    public DeleteRequestDeserializer(EntityManager entityManager, UriInfo uriInfo, ServletContext servletContext) {
        super(entityManager, uriInfo, servletContext);
    }
    

    @Override
    public String getOperationErrorType() {
        return DELETE_FAIL;
    }

    @Override
    public Message getResponse(JsonElement je, Type type, JsonDeserializationContext jdc) {
        EntityMetadata entityMetadata = entityClass2EntityMetadata(entityClass);
        return buildDeleteEntityRequestDeserializer(
                entityClass, dataType, entityManager, entityMetadata).deserialize(je, type, jdc);
    }
    
}
