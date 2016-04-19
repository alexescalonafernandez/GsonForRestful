/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.ws.gson.serializer.entity.hateoas;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import static rest.gson.common.MessageReservedWord.*;
import java.lang.reflect.Type;
import java.util.List;
import rest.gson.common.Link;
import java.util.Map;

/**
 *
 * @author alexander.escalona
 */
public class LinkListSerializer implements JsonSerializer<List<Link>>{

    @Override
    public JsonElement serialize(List<Link> links, Type type, JsonSerializationContext jsc) {
        if(links.size() > 0 && links.get(0).getSerializationMode().equals(OBJECT_PROPERTY_SERIALIZATION_MODE)){
            JsonObject jsonObject = new JsonObject();
            links.stream().forEach((link) -> {
                jsonObject.add(link.getRel(), buildLinkJsonObject(link, jsc, false));
            });
            return jsonObject;
        }
        else{
            JsonArray jsonArray = new JsonArray();
            links.stream().forEach((link) -> {
                jsonArray.add(buildLinkJsonObject(link, jsc, true));
            });
            return jsonArray;
        }
    }
    
    private JsonObject buildLinkJsonObject(Link link, JsonSerializationContext jsc, boolean withRel){
        JsonObject jsonLink = new JsonObject();
        if(withRel)
            jsonLink.addProperty(REL, link.getRel());
        jsonLink.addProperty(HREF, link.getHref());
        jsonLink.addProperty(METHOD, link.getMethod());
        if(link.getOptionalData() != null)
            link.getOptionalData().entrySet().stream().forEach((Map.Entry<String, String> entry) -> {
                jsonLink.addProperty(entry.getKey(), entry.getValue());
        });
        return jsonLink;
    }
    
}
