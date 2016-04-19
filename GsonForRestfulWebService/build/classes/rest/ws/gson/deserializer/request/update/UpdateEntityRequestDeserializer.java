/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.ws.gson.deserializer.request.update;

import com.google.gson.JsonDeserializer;
import rest.ws.dao.EntityDAO;
import static rest.ws.gson.GsonUtils.*;
import rest.ws.gson.deserializer.entity.EntityMetadata;
import rest.ws.gson.deserializer.entity.SearchEntityDeserializer;
import rest.ws.gson.deserializer.merge.AbstractMergeDeserializer;
import rest.ws.gson.deserializer.request.EntityRequestDeserializer;
import rest.ws.gson.message.Message;
import static rest.gson.common.MessageReservedWord.*;
import javax.persistence.EntityManager;

/**
 *
 * @author alexander.escalona
 */
public class UpdateEntityRequestDeserializer<T> extends EntityRequestDeserializer<T> {
    
    private final EntityMetadata entityMetadata;
    public UpdateEntityRequestDeserializer(String dataType, Class<T> clazz, EntityManager entityManager, EntityMetadata entityMetadata) {
        super(dataType, clazz, entityManager);
        this.entityMetadata = entityMetadata;
    }

    @Override
    public String getOperationErrorType() {
        return UPDATE_FAIL;
    }

    @Override
    public JsonDeserializer<T> getEntityDeserializer() {
        SearchEntityDeserializer<T> entitySearcher = new SearchEntityDeserializer<>(clazz, entityManager, entityMetadata);
        AbstractMergeDeserializer<T> deserializer = buildMergeDeserializer(clazz, entitySearcher, entityManager);
        return deserializer;
    }

    @Override
    public void doEntityAction(EntityDAO<T> dao) {
        dao.edit(entity);
    }

    @Override
    public Message getResponse() {
        return new Message.MessageBuilder(UPDATED).dataType(dataType).data(entity).build();
    }
    
}
