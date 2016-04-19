/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.gson.common.filter;

import java.util.HashMap;
import java.util.TreeMap;

/**
 *
 * @author alexander.escalona
 */
public class FiltersJsonDataBuilder {
    private final TreeMap<Integer, FilterPredicateJsonDataBuilder> filters;
    private final HashMap<String, Integer> sort;
    private int inc;
    public FiltersJsonDataBuilder() {
        filters = new TreeMap<>();
        sort = new HashMap<>();
        inc = 0;
    }
    
    public <T> FilterPredicateJsonDataBuilder<T> addFilterPredicate(String field, Class<T> fieldType){
        if(field == null)
            throw new IllegalArgumentException("The field can not be null.");
        Integer index = sort.get(field);
        if(index != null){
            filters.put(index, new FilterPredicateJsonDataBuilder(field, fieldType));
        }
        else{
            filters.put(inc, new FilterPredicateJsonDataBuilder(field, fieldType));
            sort.put(field, inc);
            index = inc++;
        }
        return filters.get(index);
    }
    
    public <T> FilterPredicateJsonDataBuilder getFilter(String field, Class<T> fieldType){
        if(field == null)
            throw new IllegalArgumentException("The field can not be null.");
        Integer index = sort.get(field);
        if(index == null)
            return null;
        return filters.get(index);
    }
    
    public FiltersJsonDataBuilder removeFilter(String field){
        if(field == null)
            throw new IllegalArgumentException("The field can not be null.");
        Integer index = sort.get(field);
        if(index != null)
            filters.remove(index);
        return this;
    }
    
    public String build(){
        if(filters.isEmpty())
            return null;
        StringBuilder builder = new StringBuilder().
                append("[");
        boolean flag = false;
        String data;
        for(FilterPredicateJsonDataBuilder predicateBuilder : filters.values()){
            data = predicateBuilder.build();
            if(data != null){
                builder.append(String.format("%s%s", flag ? "," : "", data));
                flag = true;
            }
        }
        if(flag)
            return builder.append("]").toString();
        return null;
    }
}
