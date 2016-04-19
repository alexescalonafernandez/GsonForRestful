/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.ws.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import rest.ws.gson.deserializer.entity.EntityMetadata;
import rest.ws.gson.deserializer.merge.AbstractMergeDeserializer;
import rest.ws.gson.deserializer.request.RequestProcessor;
import rest.ws.gson.deserializer.request.create.CreateEntityRequestDeserializer;
import rest.ws.gson.deserializer.request.delete.DeleteEntityRequestDeserializer;
import rest.ws.gson.deserializer.request.update.UpdateEntityRequestDeserializer;
import rest.ws.gson.message.Message;
import static rest.gson.common.MessageReservedWord.*;
import rest.ws.gson.serializer.MessageSerializer;
import rest.ws.gson.serializer.entity.AbstractEntitySerializer;
import rest.gson.common.Link;
import rest.ws.gson.deserializer.entity.CreateEntityDeserializer;
import rest.ws.gson.deserializer.entity.SearchEntityDeserializer;
import rest.ws.gson.serializer.entity.hateoas.LinkListSerializer;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author alexander.escalona
 */
public class GsonUtils {
    
    private static final GsonRegistry gsonRegistry = new GsonRegistry();
    public static <T> void registerEntitySerializer(Class<T> entityClass, Class<? extends AbstractEntitySerializer<T>> entitySerializerClass) throws InstantiationException, IllegalAccessException{
        gsonRegistry.registerEntitySerializer(entityClass, entitySerializerClass);
    }
    
    public static <T> void unregisterEntitySerializer(Class<T> entityClass){
        gsonRegistry.unregisterEntitySerializer(entityClass);
    }
    
    public static <T> void registerEntityMergeDeserializer(Class<T> entityClass, Class<? extends AbstractMergeDeserializer<T>> entitySerializerClass) throws InstantiationException, IllegalAccessException{
        gsonRegistry.registerEntityMergeDeserializer(entityClass, entitySerializerClass);
    }
    
    public static <T> void unregisterEntityMergeDeserializer(Class<T> entityClass){
        gsonRegistry.unregisterEntityMergeDeserializer(entityClass);
    }
    
    public static <T> void registerEntityPrimaryKeyName(Class<T> entityClass, String entityPrimaryKeyName){
        gsonRegistry.registerEntityPrimaryKeyName(entityClass, entityPrimaryKeyName);
    }
    
    public static <T> void unregisterEntityPrimaryKeyName(Class<T> entityClass){
        gsonRegistry.unregisterEntityPrimaryKeyName(entityClass);
    }
    
    public static <T> void registerDtoClass(Class<T> dtoClass){
        gsonRegistry.registerDtoClass(dtoClass);
    }
    
    public static <T> void unregisterDtoClass(Class<T> dtoClass){
        gsonRegistry.unregisterDtoClass(dtoClass);
    }
    
    private static GsonBuilder buildGsonBuilder(UriInfo info, ServletContext servletContext){
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonRegistry.getEntitySerializerRegistry().stream().forEach((entry) -> {
            AbstractEntitySerializer serializer = entry.getValue();
            serializer.setUriInfo(info);
            serializer.setServletContext(servletContext);
            gsonBuilder.registerTypeAdapter(entry.getKey(), serializer);
        });
        return gsonBuilder;
    }
    
    public static GsonBuilder buildGsonBuilder(UriInfo info, ServletContext servletContext, JsonDeserializer<Message> messageDeserializer){
        return buildGsonBuilder(info, servletContext).
                registerTypeAdapter(Message.class, messageDeserializer);
    }
    
    public static GsonBuilder buildGsonBuilder(UriInfo info, ServletContext servletContext, JsonSerializer<Message> messageSerializer){
        return buildGsonBuilder(info, servletContext).
                registerTypeAdapter(Message.class, messageSerializer);
    }
    
    public static <T> String serializeListT2Message(UriInfo info, ServletContext servletContext, String dataType, List<T> toAdapt){
        Message response = new Message.MessageBuilder(RETRIEVED).
                dataType(dataType).
                dataContainer(LIST_CONTAINER).
                data(toAdapt).
                build();
        return serializeMessage(info, servletContext, response);
    }
    
    public static <T> String serializeT2Message(UriInfo info, ServletContext servletContext, String dataType, T toAdapt){
        Message response = new Message.MessageBuilder(RETRIEVED).
                dataType(dataType).
                dataContainer(OBJECT_CONTAINER).
                data(toAdapt).
                build();
        return serializeMessage(info, servletContext, response);
    }
    
    public static String serializeMessage(UriInfo info, ServletContext servletContext, Message response){
        GsonBuilder gsonBuilder = buildGsonBuilder(info, servletContext, new MessageSerializer());
        Gson gson = gsonBuilder.create();
        return gson.toJson(response);
    }
    
    public static JsonElement serializeLinkList(List<Link> links, JsonSerializationContext jsc){
        Type type = new TypeToken<List<Link>>(){}.getType();
        return new LinkListSerializer().serialize(links, type, jsc);
    }
    
    public static Message deserializeJson2Message(String json, UriInfo info, ServletContext servletContext, EntityManager entityManager){
        GsonBuilder gsonBuilder = buildGsonBuilder(info, servletContext, new RequestProcessor(entityManager, info, servletContext));
        Gson gson = gsonBuilder.create();
        return gson.fromJson(json, Message.class);
    }
    
    public static <T> T deserializeJsonWithMergeDeserializer(Class<T> clazz, 
            JsonElement je, JsonDeserializationContext jdc,
            String action,EntityManager entityManager){
        return buildMergeDeserializer(clazz,action,entityManager).deserialize(je, clazz, jdc);
    }
    
    public static Class<?> dataType2EntityClass(String dataType){
        return gsonRegistry.findEntityClass(dataType);
    }
    
    public static Class<?> dataType2DtoClass(String dataType){
        return gsonRegistry.findDtoClass(dataType);
    }
    
    public static EntityMetadata entityClass2EntityMetadata(Class<?> clazz){
        return gsonRegistry.getEntityMetadataByPrimaryKeyName(clazz);
    }
    
    public static <T> AbstractMergeDeserializer<T> buildMergeDeserializer(Class<T> clazz, JsonDeserializer<T> deserializer, EntityManager entityManager){
        AbstractMergeDeserializer entityMergeDeserializer = gsonRegistry.getEntityMergeDeserializer(clazz);
        if(entityMergeDeserializer != null){
            entityMergeDeserializer.setDeserializer(deserializer);
            entityMergeDeserializer.setEntityManager(entityManager);
        }
        return entityMergeDeserializer;
    }
    
    public static <T> AbstractMergeDeserializer<T> buildMergeDeserializer(Class<T> entityClass, String action, EntityManager entityManager){
        JsonDeserializer<T> deserializer = null;
        switch(action){
            case CREATE:
                deserializer = new CreateEntityDeserializer<>(entityClass);
                break;
            case UPDATE:
                EntityMetadata entityMetadata = entityClass2EntityMetadata(entityClass);
                deserializer = new SearchEntityDeserializer<>(entityClass, entityManager, entityMetadata);
                break;
            default:
                throw new IllegalArgumentException(String.format("The action only can be: %s or %s", CREATE, UPDATE));
        }
        AbstractMergeDeserializer entityMergeDeserializer = gsonRegistry.getEntityMergeDeserializer(entityClass);
        if(entityMergeDeserializer != null){
            entityMergeDeserializer.setDeserializer(deserializer);
            entityMergeDeserializer.setEntityManager(entityManager);
        }
        return entityMergeDeserializer;
    }
    
    public static <T> CreateEntityRequestDeserializer<T> buildCreateEntityRequestDeserializer(Class<T> clazz, String dataType, EntityManager entityManager){
        return new CreateEntityRequestDeserializer<>(dataType, clazz, entityManager);
    }
    
    public static <T> UpdateEntityRequestDeserializer<T> buildUpdateEntityRequestDeserializer(Class<T> clazz, String dataType, EntityManager entityManager, EntityMetadata entityMetadata){
        return new UpdateEntityRequestDeserializer<>(dataType, clazz, entityManager, entityMetadata);
    }
    
    public static <T> DeleteEntityRequestDeserializer<T> buildDeleteEntityRequestDeserializer(Class<T> clazz, String dataType, EntityManager entityManager, EntityMetadata entityMetadata){
        return new DeleteEntityRequestDeserializer<>(dataType, clazz, entityManager, entityMetadata);
    }
    
    public static Set<String> getJPQLOperatorKeys(){
        return gsonRegistry.getJPQLOperatorKeys();
    }
    
    public static String getJPQLOperator(String key){
        return gsonRegistry.getJPQLOperator(key);
    }
    
    public static Object castProperty(JsonObject jsonObject, String property, Class propertyType){
        if(propertyType.equals(Boolean.class))
            return jsonObject.get(property).getAsBoolean();
         else if(propertyType.equals(Number.class))
             return jsonObject.get(property).getAsNumber();
         else if(propertyType.equals(String.class))
             return jsonObject.get(property).getAsString();
         else if(propertyType.equals(Double.class))
             return jsonObject.get(property).getAsDouble();
         else if(propertyType.equals(Float.class))
             return jsonObject.get(property).getAsFloat();
         else if(propertyType.equals(Long.class))
             return jsonObject.get(property).getAsLong();
         else if(propertyType.equals(Integer.class))
             return jsonObject.get(property).getAsInt();
         else if(propertyType.equals(Byte.class))
             return jsonObject.get(property).getAsByte();
         else if(propertyType.equals(Character.class))
             return jsonObject.get(property).getAsCharacter();
         else if(propertyType.equals(BigDecimal.class))
             return jsonObject.get(property).getAsBigDecimal();
         else if(propertyType.equals(BigInteger.class))
             return jsonObject.get(property).getAsBigInteger();
         else if(propertyType.equals(Short.class))
             return jsonObject.get(property).getAsShort();
         else if(propertyType.equals(Date.class))
             return new Date(jsonObject.get(property).getAsLong());
         else return null;
    }
    
    public static Class castSimpleName2Class(String name){
        if(name.equals(Boolean.class.getSimpleName())) 
            return Boolean.class;
        else if(name.equals(Number.class.getSimpleName())) 
            return Number.class;
        else if(name.equals(String.class.getSimpleName())) 
            return String.class;
        else if(name.equals(Double.class.getSimpleName())) 
            return Double.class;
        else if(name.equals(Float.class.getSimpleName())) 
            return Float.class;
        else if(name.equals(Long.class.getSimpleName())) 
            return Long.class;
        else if(name.equals(Integer.class.getSimpleName())) 
            return Integer.class;
        else if(name.equals(Byte.class.getSimpleName())) 
            return Byte.class;
        else if(name.equals(Character.class.getSimpleName())) 
            return Character.class;
        else if(name.equals(BigDecimal.class.getSimpleName())) 
            return BigDecimal.class;
        else if(name.equals(BigInteger.class.getSimpleName())) 
            return BigInteger.class;
        else if(name.equals(Short.class.getSimpleName())) 
            return Short.class;
        else if(name.equals(Date.class.getSimpleName())) 
            return Date.class;
        else return null;
    }
    
    public static String castProperty2String(JsonObject jsonObject, String property, Class propertyType){
        if(propertyType.equals(Boolean.class))
            return String.valueOf(jsonObject.get(property).getAsBoolean());
         else if(propertyType.equals(Number.class))
             return String.valueOf(jsonObject.get(property).getAsNumber());
         else if(propertyType.equals(String.class))
             return String.format("'%s'", jsonObject.get(property).getAsString());
         else if(propertyType.equals(Double.class))
             return String.valueOf(jsonObject.get(property).getAsDouble());
         else if(propertyType.equals(Float.class))
             return String.valueOf(jsonObject.get(property).getAsFloat());
         else if(propertyType.equals(Long.class))
             return String.valueOf(jsonObject.get(property).getAsLong());
         else if(propertyType.equals(Integer.class))
             return String.valueOf(jsonObject.get(property).getAsInt());
         else if(propertyType.equals(Byte.class))
             return String.valueOf(jsonObject.get(property).getAsByte());
         else if(propertyType.equals(Character.class))
             return String.format("'%c'", jsonObject.get(property).getAsCharacter());
         else if(propertyType.equals(BigDecimal.class))
             return String.valueOf(jsonObject.get(property).getAsBigDecimal());
         else if(propertyType.equals(BigInteger.class))
             return String.valueOf(jsonObject.get(property).getAsBigInteger());
         else if(propertyType.equals(Short.class))
             return String.valueOf(jsonObject.get(property).getAsShort());
         else if(propertyType.equals(Date.class)){
             Date date = new Date(jsonObject.get(property).getAsLong());
             SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
             return String.format("'%s'", sdf.format(date));
         }
         else return null;
    }
    
    public static String buildResponse(UriInfo uriInfo,
            ServletContext servletContext, EntityManager entityManager,String request){
         Message response = deserializeJson2Message(request, uriInfo,
                servletContext, entityManager);
        return serializeMessage(uriInfo, servletContext, response);
    }
    
    public static <T> String buildMessageRequestAsJson(String action, Class<T> dataType, String data){
         return String.format(
                 "{\"%s\":\"%s\",\"%s\":\"%s\",\"%s\":%s}",
                 ACTION, action,
                 DATA_TYPE, dataType.getCanonicalName(),
                 DATA, data);
    }
    
    public static <T> String buildMessageRequestSearchByIdJsonData(Class<T> dataType, String value){
        EntityMetadata entityMetadata  = entityClass2EntityMetadata(dataType);
        return String.format("{\"%s\":\"%s\"}", entityMetadata.getPropertyId(), value);
    }
}
