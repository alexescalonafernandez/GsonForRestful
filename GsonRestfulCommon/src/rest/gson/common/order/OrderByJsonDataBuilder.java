/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.gson.common.order;

import java.util.HashMap;
import java.util.TreeMap;

/**
 *
 * @author alexander.escalona
 */
public class OrderByJsonDataBuilder {
    private final TreeMap<Integer, OrderOperationJsonDataBuilder> order;
    private final HashMap<String, Integer> sort;
    private int inc;
    public OrderByJsonDataBuilder() {
        order = new TreeMap<>();
        sort = new HashMap<>();
        inc = 0;
    }
    
    public OrderByJsonDataBuilder addOrder(String field){
        if(field == null)
            throw new IllegalArgumentException("The field can not be null.");
        Integer index = sort.get(field);
        if(index != null){
            order.put(index, new OrderOperationJsonDataBuilder(field));
        }
        else{
            order.put(inc, new OrderOperationJsonDataBuilder(field));
            sort.put(field, inc++);
        }
        return this;
    }
    
    public OrderByJsonDataBuilder addOrder(String field, boolean ascending){
        if(field == null)
            throw new IllegalArgumentException("The field can not be null.");
        Integer index = sort.get(field);
        if(index != null){
            order.put(index, new OrderOperationJsonDataBuilder(field).
                direction(false));
        }
        else{
            order.put(inc, new OrderOperationJsonDataBuilder(field).
                direction(false));
            sort.put(field, inc++);
        }
        return this;
    }
    
    public OrderByJsonDataBuilder removeFilter(String field){
        if(field == null)
            throw new IllegalArgumentException("The field can not be null.");
        Integer index = sort.get(field);
        if(index != null)
            order.remove(index);
        return this;
    }
    
    public String build(){
        if(order.isEmpty())
            return null;
        StringBuilder builder = new StringBuilder().
                append("[");
        boolean flag = false;
        for(OrderOperationJsonDataBuilder op : order.values()){
            builder.append(String.format("%s%s", flag ? "," : "", op.build()));
            flag = true;
        }
        return builder.append("]").toString();
    }
    
}
