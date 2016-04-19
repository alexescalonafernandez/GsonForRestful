/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.ws.gson;

import rest.ws.gson.deserializer.entity.EntityMetadata;
import rest.ws.gson.deserializer.merge.AbstractMergeDeserializer;
import static rest.gson.common.MessageReservedWord.*;
import rest.ws.gson.serializer.entity.AbstractEntitySerializer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author alexander.escalona
 */
public class GsonRegistry {
    private final HashMap<Class<?>, AbstractEntitySerializer> entitySerializerRegistry;
    private final HashMap<Class<?>, AbstractMergeDeserializer> entityMergeDeserializerRegistry;
    private final HashMap<Class<?>, String> entityPrimaryKeyNameRegistry;
    private final HashMap<String, String> jpqlOperators;
    private final HashMap<String, Class<?>> dtoRegistry;

    public GsonRegistry() {
        this.entitySerializerRegistry = new HashMap<>();
        this.entityMergeDeserializerRegistry = new HashMap<>();
        this.entityPrimaryKeyNameRegistry = new HashMap<>();
        this.jpqlOperators = new HashMap<>();
        this.dtoRegistry = new HashMap<>();
        
        jpqlOperators.put(LT_OPERATOR, "<");
        jpqlOperators.put(LE_OPERATOR, "<=");
        jpqlOperators.put(EQ_OPERATOR, "=");
        jpqlOperators.put(NE_OPERATOR, "<>");
        jpqlOperators.put(GT_OPERATOR, ">");
        jpqlOperators.put(GE_OPERATOR, ">=");
        jpqlOperators.put(NU_OPERATOR, "IS");
        jpqlOperators.put(LK_OPERATOR, "LIKE");
        jpqlOperators.put(NL_OPERATOR, "NOT LIKE");
    }
    
    public void registerEntitySerializer(Class<?> entityClass, Class<? extends AbstractEntitySerializer> entitySerializerClass) throws InstantiationException, IllegalAccessException{
        entitySerializerRegistry.put(entityClass, entitySerializerClass.newInstance());
    }
    
    public void unregisterEntitySerializer(Class<?> entityClass){
        entitySerializerRegistry.remove(entityClass);
    }
    
    public Set<Map.Entry<Class<?>, AbstractEntitySerializer>> getEntitySerializerRegistry(){
        return entitySerializerRegistry.entrySet();
    }
    
    public void registerEntityMergeDeserializer(Class<?> entityClass, Class<? extends AbstractMergeDeserializer> entityMergeDeserializerClass) throws InstantiationException, IllegalAccessException{
        entityMergeDeserializerRegistry.put(entityClass, entityMergeDeserializerClass.newInstance());
    }
    
    public void unregisterEntityMergeDeserializer(Class<?> entityClass){
        entityMergeDeserializerRegistry.remove(entityClass);
    }
    
    public AbstractMergeDeserializer getEntityMergeDeserializer(Class<?> entityClass){
        return entityMergeDeserializerRegistry.get(entityClass);
    }
    
    public void registerEntityPrimaryKeyName(Class<?> entityClass, String entityPrimaryKeyName){
        entityPrimaryKeyNameRegistry.put(entityClass, entityPrimaryKeyName);
    }
    
    public void unregisterEntityPrimaryKeyName(Class<?> entityClass){
        entityPrimaryKeyNameRegistry.remove(entityClass);
    }
    
    public EntityMetadata getEntityMetadataByPrimaryKeyName(Class<?> entityClass){
        String pk = entityPrimaryKeyNameRegistry.get(entityClass);
        if(pk == null)
            return null;
        else{
            try {
                Class propertyType = entityClass.getDeclaredField(pk).getType();
                return new EntityMetadata(pk, propertyType);
            } catch (NoSuchFieldException | SecurityException ex) {
                return null;
            }
        }
    }
    
    public void registerDtoClass(Class<?> dtoClass){
        dtoRegistry.put(dtoClass.getCanonicalName(), dtoClass);
    }
    
    public void unregisterDtoClass(Class<?> dtoClass){
        dtoRegistry.remove(dtoClass.getCanonicalName());
    }
    
    public Set<Map.Entry<String, Class<?>>> getDtoRegistry(){
        return dtoRegistry.entrySet();
    }
    
    public Class<?> findEntityClass(String className){
        for(Class<?> clazz : entitySerializerRegistry.keySet())
            if(clazz.getCanonicalName().equals(className))
                return clazz;
        for(Class<?> clazz : entityMergeDeserializerRegistry.keySet())
            if(clazz.getCanonicalName().equals(className))
                return clazz;
        for(Class<?> clazz : entityPrimaryKeyNameRegistry.keySet())
            if(clazz.getCanonicalName().equals(className))
                return clazz;
        return null;
    }
    
    public Class<?> findDtoClass(String className){
        return dtoRegistry.get(className);
    }
    
    public Set<String> getJPQLOperatorKeys(){
        return jpqlOperators.keySet();
    }
    
    public String getJPQLOperator(String key){
        return jpqlOperators.get(key);
    }
    
    
    
    
}
