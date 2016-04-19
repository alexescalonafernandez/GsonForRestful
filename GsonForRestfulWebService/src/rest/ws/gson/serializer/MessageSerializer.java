/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.ws.gson.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import static rest.ws.gson.GsonUtils.*;
import rest.ws.gson.message.Message;
import static rest.gson.common.MessageReservedWord.*;
import java.lang.reflect.Type;
/**
 *
 * @author alexander.escalona
 */
public class MessageSerializer implements JsonSerializer<Message>{

    @Override
    public JsonElement serialize(Message message, Type type, JsonSerializationContext jsc) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(ACTION, message.getActionName());
        jsonObject.addProperty(DATA_TYPE, message.getDataType());
        if(message.getDataContainer() != null)
            jsonObject.add(DATA_CONTAINER, jsc.serialize(message.getDataContainer()));
        if(message.getLogList() != null)
            jsonObject.add(LOG_LIST, jsc.serialize(message.getLogList()));
        if(message.getLinks() != null)
            jsonObject.add(LINKS, serializeLinkList(message.getLinks(), jsc));
        if(message.getData() != null)
            jsonObject.add(DATA, jsc.serialize(message.getData()));
        return jsonObject;
    }
}
