/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.ws.gson.message;

import rest.gson.common.Link;
import rest.gson.common.Log;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author alexander.escalona
 */
public class Message {
    
    private final String actionName, dataType, dataContainer;
    private final List<Log> logList;
    private final List<Link> links; 
    private final Object data;

    private Message(MessageBuilder builder) {
        this.actionName = builder.actionName;
        this.dataType = builder.dataType;
        this.logList = builder.logList;
        this.data = builder.data;
        this.dataContainer = builder.dataContainer;
        this.links = builder.links;
    }
    
    public static class MessageBuilder{
        
        private String actionName, dataType, dataContainer;
        private List<Log> logList;
        private List<Link> links; 
        private Object data;
        

        public MessageBuilder(String actionName) {
            this.actionName = actionName;
        }

        public MessageBuilder dataType(String dataType) {
            this.dataType = dataType;
            return this;
        }
        
        public MessageBuilder data(Object data) {
            this.data = data;
            return this;
        }
        
        public MessageBuilder addLog(Log log){
            if(logList == null)
                logList = new ArrayList<>();
            logList.add(log);
            return this;
        }
        
        public MessageBuilder addLink(Link link){
            if(links == null)
                links = new ArrayList<>();
            links.add(link);
            return this;
        }

        public MessageBuilder dataContainer(String dataContainer) {
            this.dataContainer = dataContainer;
            return this;
        }
        
        public Message build(){
            return new Message(this);
        }
    }
    
    public String getActionName() {
        return actionName;
    }

    public String getDataType() {
        return dataType;
    }

    public List<Log> getLogList() {
        return logList;
    }

    public Object getData() {
        return data;
    }

    public String getDataContainer() {
        return dataContainer;
    }

    public List<Link> getLinks() {
        return links;
    }
}
