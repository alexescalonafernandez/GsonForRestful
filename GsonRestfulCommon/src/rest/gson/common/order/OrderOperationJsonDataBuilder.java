/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.gson.common.order;

import static rest.gson.common.MessageReservedWord.*;

/**
 *
 * @author alexander.escalona
 */
public class OrderOperationJsonDataBuilder {
    private final String field;
    private String direction;

    public OrderOperationJsonDataBuilder(String field) {
        if(field == null)
            throw new IllegalArgumentException("The field can not be null.");
        this.field = field;
        this.direction = ASC;
    }
    
    public OrderOperationJsonDataBuilder direction(boolean ascending){
        this.direction = ascending ? ASC : DESC;
        return this;
    }
    
    public String build(){
        StringBuilder builder = new StringBuilder().
                append("{").
                append(String.format("\"%s\":\"%s\"", FIELD, field));
        if(direction.equals(DESC))
            builder.append(String.format("\"%s\":\"%s\"", ORDER_DIRECTION, DESC));
        return builder.append("}").toString();
    }
    
    
    
}
