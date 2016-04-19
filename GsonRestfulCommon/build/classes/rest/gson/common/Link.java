/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.gson.common;

import static rest.gson.common.MessageReservedWord.*;
import java.util.HashMap;

/**
 *
 * @author alexander.escalona
 */
public class Link {
    private final String href;
    private final String rel;
    private final String method;
    private final String serializationMode;
    private final HashMap<String, String> optionalData;

    private Link(LinkBuilder builder) {
        this.href = builder.href;
        this.rel = builder.rel;
        this.method = builder.method;
        this.serializationMode = builder.serializationMode;
        this.optionalData = builder.optionalData;
    }
    
    
    public static class LinkBuilder{
        private String href;
        private String rel = SELF;
        private String method = GET;
        private String serializationMode = LIST_ITEM_SERIALIZATION_MODE;
        private HashMap<String, String> optionalData;

        public LinkBuilder(String href) {
            this.href = href;
        }

        public LinkBuilder rel(String rel) {
            this.rel = rel;
            return this;
        }

        public LinkBuilder method(String method) {
            this.method = method;
            return this;
        }

        public LinkBuilder serializationMode(String serializationMode) {
            this.serializationMode = serializationMode;
            return this;
        }

        public LinkBuilder addOptionalData(String key, String data) {
            if(optionalData == null)
                optionalData = new HashMap<>();
            optionalData.put(key, data);
            return this;
        }
        
        public Link build(){
            return new Link(this);
        }
    }

    public String getHref() {
        return href;
    }

    public String getRel() {
        return rel;
    }

    public String getMethod() {
        return method;
    }

    public String getSerializationMode() {
        return serializationMode;
    }

    public HashMap<String, String> getOptionalData() {
        return optionalData;
    }
    
    
}
