/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.client.gson.message;

import rest.gson.common.Link;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 *
 * @author alexander.escalona
 */
public class LinkProvider<T> {
    private final TreeMap<T, Link> links;
    private final Class<T> clazz;
    private final Method valueOf;
    public LinkProvider(Class<T> clazz) {
        if(clazz == null)
            throw  new IllegalArgumentException("The class can not be null");
        try {
            this.links = new TreeMap<>();
            this.clazz = clazz;
            if(!clazz.isAssignableFrom(String.class))
                this.valueOf = this.clazz.getMethod("valueOf", String.class);
            else valueOf = null;
        } catch (NoSuchMethodException | SecurityException ex) {
            throw new IllegalArgumentException(String.format(
                    "The generic class must be String or contains method with signature: public static %s valueOf(String arg)",
                    clazz.getSimpleName()));
        }
    }
    
    public Collection<Link> getAsCollection(){
        return links.values();
    }
    
    public Set<Map.Entry<T, Link>> getAsMap(){
        return links.entrySet();
    }
    
    public Link getLink(T rel){
        return links.get(rel);
    }
    
    public void addLink(Link link) {
        if(clazz.isAssignableFrom(String.class))
            links.put((T) link.getRel(), link);
        else{
            try {
                T key = (T) valueOf.invoke(clazz, link.getRel());
                links.put(key, link);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                throw new IllegalArgumentException(String.format("The link rel can not be converted to %s value", clazz.getSimpleName()));
            }
        }
        
    }
    
    
}
