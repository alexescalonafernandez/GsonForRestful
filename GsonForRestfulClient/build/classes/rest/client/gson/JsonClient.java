/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.client.gson;

import java.net.URI;
import java.util.HashMap;
import static javax.ws.rs.client.Entity.*;
import javax.ws.rs.client.WebTarget;
import static javax.ws.rs.core.MediaType.*;
import javax.ws.rs.core.UriBuilder;

/**
 *
 * @author alexander.escalona
 */
public class JsonClient {
    private WebTarget target;

    private JsonClient(JsonClientBuilder builder) {
        this.target = builder.target;
    }
    
    public String get(){
        return target.
                request(APPLICATION_JSON).
                get(String.class);
    }
    
    public String put(String request){
        return target.
                request(APPLICATION_JSON).
                put(entity(request, APPLICATION_JSON), String.class);
    }
    
    public String post(String request){
        return target.
                request(APPLICATION_JSON).
                post(entity(request, APPLICATION_JSON), String.class);
    }
    
    public String delete(){
        return target.
                request(APPLICATION_JSON).
                delete(String.class);
    }
    
    public String options(){
        return target.
                request(APPLICATION_JSON).
                options(String.class);
    }
    
    public static class JsonClientBuilder{
        private WebTarget target;

        public JsonClientBuilder(String targetUri) {
            target = javax.ws.rs.client.ClientBuilder.newClient().target(targetUri);
        }
        
        public JsonClientBuilder(URI targetUri) {
            target = javax.ws.rs.client.ClientBuilder.newClient().target(targetUri);
        }
        
        public JsonClientBuilder(UriBuilder targetUri) {
            target = javax.ws.rs.client.ClientBuilder.newClient().target(targetUri);
        }
        
        public JsonClientBuilder path(String path){
            target = target.path(path);
            return this;
        }
        
        public JsonClientBuilder queryParam(String param, Object value){
            target = target.queryParam(param, value);
            return this;
        }
        
        public JsonClientBuilder queryParams(HashMap<String, Object> params){
            params.entrySet().stream().forEach((entry) -> {
                target = target.queryParam(entry.getKey(), entry.getValue());
            });
            return this;
        }
        
        public JsonClient build(){
            return new JsonClient(this);
        }
        
    }
}
