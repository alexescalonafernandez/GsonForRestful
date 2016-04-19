/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.ws.gson.deserializer.entity;

/**
 *
 * @author alexander.escalona
 */
public class EntityMetadata {
    private final String propertyId;
    private final Class propertyIdType;

    public EntityMetadata(String propertyId, Class propertyIdType) {
        this.propertyId = propertyId;
        this.propertyIdType = propertyIdType;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public Class getPropertyIdType() {
        return propertyIdType;
    }
    
}
