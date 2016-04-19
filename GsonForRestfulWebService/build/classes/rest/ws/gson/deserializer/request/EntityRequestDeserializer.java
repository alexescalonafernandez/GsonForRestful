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
import static rest.ws.dao.DAOFactory.*;
import rest.ws.dao.EntityDAO;
import rest.gson.common.Log;
import rest.gson.common.LogLevel;
import rest.ws.gson.message.Message;
import static rest.gson.common.MessageReservedWord.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;

/**
 *
 * @author alexander.escalona
 */
public abstract class EntityRequestDeserializer<T> implements JsonDeserializer<Message>{
    protected EntityManager entityManager;
    protected String dataType;
    protected Class<T> clazz;
    protected T entity;

    public EntityRequestDeserializer(String dataType, Class<T> clazz, EntityManager entityManager) {
        this.entityManager = entityManager;
        this.dataType = dataType;
        this.clazz = clazz;
    }
    
    @Override
    public Message deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {
        JsonObject jsonObject = je.getAsJsonObject();
        if(jsonObject.has(DATA)){
            try{
                JsonDeserializer<T> deserializer = getEntityDeserializer();
                if(deserializer == null){
                    Log log = new Log.LogBuilder(LogLevel.ERROR).
                         codeKey(String.valueOf(500)).
                         codeStr(NullPointerException.class.getCanonicalName()).
                         userMsg(String.format(
                                 "You must register an Entity Merge Deserializer for '%s' entity",
                                 clazz.getCanonicalName())).
                         build();
                    return new Message.MessageBuilder(getOperationErrorType()).addLog(log).build();
                }
                entity = deserializer.deserialize(jsonObject.get(DATA), clazz, jdc);
                
                ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
                Validator validator = factory.getValidator();
                Set<ConstraintViolation<T>> uncheckedConstraintViolations = validator.validate(entity);
                if (uncheckedConstraintViolations.size() > 0 ){
                    List<ConstraintViolation<T>> constraintViolations = new ArrayList<>();
                    
                    uncheckedConstraintViolations.stream().forEach((ConstraintViolation<T> constraint) -> {
                        boolean add = true;
                        if(constraint.getConstraintDescriptor().getAnnotation() instanceof NotNull)    
                            try {
                                Field field = constraint.getRootBeanClass().getDeclaredField(constraint.getPropertyPath().toString());
                                for(Annotation a : field.getDeclaredAnnotations())
                                    if(a instanceof GeneratedValue)
                                        add = false;
                            } catch (NoSuchFieldException | SecurityException ex) {
                                Logger.getLogger(EntityRequestDeserializer.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        if(add)
                            constraintViolations.add(constraint);
                    });
                    if(constraintViolations.size() > 0){
                        Message.MessageBuilder messageBuilder = new Message.MessageBuilder(getOperationErrorType());
                        constraintViolations.stream().forEach((constraint) -> {
                            messageBuilder.addLog(
                                    new Log.LogBuilder(LogLevel.ERROR).
                                            codeKey(String.valueOf(412)).
                                            codeStr(ConstraintViolationException.class.getCanonicalName()).userMsg("").
                                            userMsg(String.format(
                                                    "On '%s' entity the property '%s' violates: %s",
                                                    constraint.getRootBeanClass().getCanonicalName(),
                                                    constraint.getPropertyPath(),
                                                    constraint.getMessage())).
                                            build()
                            );
                        });
                        return messageBuilder.build();
                    }
                }
                
                doEntityAction(createEntityDAO(clazz, entityManager));
                return getResponse();
            }
            catch(Exception ex){
                StringWriter errors = new StringWriter();
                ex.printStackTrace(new PrintWriter(errors));
                Log log = new Log.LogBuilder(LogLevel.ERROR).
                         codeKey(String.valueOf(500)).
                         codeStr(ex.getClass().getCanonicalName()).
                         userMsg(errors.toString()).
                         build();
                 return new Message.MessageBuilder(getOperationErrorType()).addLog(log).build();
            }
            
        }
        else{
            Log log = new Log.LogBuilder(LogLevel.ERROR).
                    codeKey(String.valueOf(400)).
                    codeStr("Bad Request").
                    userMsg(String.format("Request protocol error. The '%s' property is missed", DATA)).
                    build();
            return new Message.MessageBuilder(getOperationErrorType()).addLog(log).build();
        }
    }
    
    public abstract JsonDeserializer<T> getEntityDeserializer();
    public abstract void doEntityAction(EntityDAO<T> dao);
    public abstract Message getResponse();
    public abstract String getOperationErrorType();
    
}
