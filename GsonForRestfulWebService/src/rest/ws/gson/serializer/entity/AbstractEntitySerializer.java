/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.ws.gson.serializer.entity;

import com.google.gson.JsonSerializer;
import rest.ws.gson.serializer.entity.hateoas.ILinksGenerator;
import javax.servlet.ServletContext;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author alexander.escalona
 */
public abstract class AbstractEntitySerializer<T> implements JsonSerializer<T>, ILinksGenerator<T>{
    protected UriInfo uriInfo;
    protected ServletContext servletContext;
    public AbstractEntitySerializer(){}
    public AbstractEntitySerializer(UriInfo uriInfo, ServletContext servletContext) {
        this.uriInfo = uriInfo;
        this.servletContext = servletContext;
    }

    public UriInfo getUriInfo() {
        return uriInfo;
    }

    public void setUriInfo(UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }

    public ServletContext getServletContext() {
        return servletContext;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
    
    
}
