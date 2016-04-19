/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.ws.gson.deserializer.request.create;

import com.google.gson.JsonDeserializer;
import rest.ws.dao.EntityDAO;
import static rest.ws.gson.GsonUtils.*;
import rest.ws.gson.deserializer.entity.CreateEntityDeserializer;
import rest.ws.gson.deserializer.merge.AbstractMergeDeserializer;
import rest.ws.gson.deserializer.request.EntityRequestDeserializer;
import rest.ws.gson.message.Message;
import static rest.gson.common.MessageReservedWord.*;
import javax.persistence.EntityManager;

/**
 *
 * @author alexander.escalona
 */
public class CreateEntityRequestDeserializer<T> extends EntityRequestDeserializer<T>{

    public CreateEntityRequestDeserializer(String dataType, Class<T> clazz, EntityManager entityManager) {
        super(dataType, clazz, entityManager);
    }

    @Override
    public String getOperationErrorType() {
        return CREATE_FAIL;
    }

    @Override
    public JsonDeserializer<T> getEntityDeserializer() {
        CreateEntityDeserializer<T> entityCreator = new CreateEntityDeserializer<>(clazz);
        AbstractMergeDeserializer<T> deserializer = buildMergeDeserializer(clazz, entityCreator, entityManager);
        return deserializer;
    }

    @Override
    public void doEntityAction(EntityDAO<T> dao) {
        dao.create(entity);
    }

    @Override
    public Message getResponse() {
        return new Message.MessageBuilder(CREATED).dataType(dataType).data(entity).build();
    }
    
}
