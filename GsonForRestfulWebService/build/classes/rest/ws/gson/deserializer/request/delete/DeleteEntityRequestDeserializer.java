/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.ws.gson.deserializer.request.delete;

import com.google.gson.JsonDeserializer;
import rest.ws.dao.EntityDAO;
import rest.ws.gson.deserializer.entity.EntityMetadata;
import rest.ws.gson.deserializer.entity.SearchEntityDeserializer;
import rest.ws.gson.deserializer.request.EntityRequestDeserializer;
import rest.ws.gson.message.Message;
import static rest.gson.common.MessageReservedWord.*;
import javax.persistence.EntityManager;

/**
 *
 * @author alexander.escalona
 */
public class DeleteEntityRequestDeserializer<T> extends EntityRequestDeserializer<T>{
    private final EntityMetadata entityMetadata;
    public DeleteEntityRequestDeserializer(String dataType, Class<T> clazz, EntityManager entityManager, EntityMetadata entityMetadata) {
        super(dataType, clazz, entityManager);
        this.entityMetadata = entityMetadata;
    }

    @Override
    public String getOperationErrorType() {
        return DELETE_FAIL;
    }

    @Override
    public JsonDeserializer<T> getEntityDeserializer() {
        SearchEntityDeserializer<T> entitySearcher = new SearchEntityDeserializer<>(clazz, entityManager, entityMetadata);
        return entitySearcher;
    }

    @Override
    public void doEntityAction(EntityDAO<T> dao) {
        dao.remove(entity);
    }

    @Override
    public Message getResponse() {
        return new Message.MessageBuilder(DELETED).dataType(dataType).build();
    }
    
}
