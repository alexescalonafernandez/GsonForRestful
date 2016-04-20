# GsonForRestfulClient
GsonForRestfulClient is a Java library which can be used by send **request** to [Restful](https://en.wikipedia.org/wiki/Representational_state_transfer) [Web Service](https://en.wikipedia.org/wiki/Web_service), which have been designed using [GsonForRestfulWebService](/GsonForRestfulWebService/README.md) library and deserialize its **response**. 

## GsonForRestfulClient Goals
* Build a [Client](https://en.wikipedia.org/wiki/Client_%28computing%29) given the [Restful](https://en.wikipedia.org/wiki/Representational_state_transfer) [Web Service](https://en.wikipedia.org/wiki/Web_service) **path** and a sequence of **query parameters** if necessary. See [JsonClient](/GsonForRestfulClient/src/rest/client/gson/JsonClient.java) class.

* Invoke [GET, PUT, POST, DELETE, OPTIONS](https://es.wikipedia.org/wiki/Hypertext_Transfer_Protocol) **request** where [PUT](https://es.wikipedia.org/wiki/Hypertext_Transfer_Protocol) and [POST](https://es.wikipedia.org/wiki/Hypertext_Transfer_Protocol) contains [JSON-Pure](https://mmikowski.github.io/json-pure/) message on **request** body. See [JsonClient](/GsonForRestfulClient/src/rest/client/gson/JsonClient.java) class.

* Build [JSON-Pure](https://mmikowski.github.io/json-pure/) **request** message given the **action**, **dataType**, and **data** fields. See **buildMessageRequestAsJson** method at [MessageUtils](/GsonForRestfulClient/src/rest/client/gson/message/MessageUtils.java) class.

* Deserialize [Restful](https://en.wikipedia.org/wiki/Representational_state_transfer) [Web Service](https://en.wikipedia.org/wiki/Web_service) **response** to [ResponseMessage](/GsonForRestfulClient/src/rest/client/gson/message/ResponseMessage.java) class, given a **Class<T>** (which represent the [DTO](https://en.wikipedia.org/wiki/Data_transfer_object) **Data Transfer Object** where will be stored the data received on [Service](https://en.wikipedia.org/wiki/Web_service) **response**), the [Restful](https://en.wikipedia.org/wiki/Representational_state_transfer) [Web Service](https://en.wikipedia.org/wiki/Web_service) **response**, and optionally a [JsonDeserializer](https://github.com/google/gson) class which will be used by [GSON](https://github.com/google/gson) class to deserialize **data** field to [DTO](https://en.wikipedia.org/wiki/Data_transfer_object) class. The [ResponseMessage](/GsonForRestfulClient/src/rest/client/gson/message/ResponseMessage.java) class contains the **action** executed by [Web Service](https://en.wikipedia.org/wiki/Web_service), the **data type** received, a list of [Link](/GsonRestfulCommon/src/rest/gson/common/Link.java) if errors occurred, a list of [LinkProvider](/GsonForRestfulClient/src/rest/client/gson/message/LinkProvider.java) for [pagination](http://jasonwatmore.com/post/2015/10/30/ASPNET-MVC-Pagination-Example-with-Logic-like-Google.aspx) [HATEOAS](https://en.wikipedia.org/wiki/HATEOAS) support if exists and a [map](https://en.wikipedia.org/wiki/Hash_table) of [LinkProvider](/GsonForRestfulClient/src/rest/client/gson/message/LinkProvider.java) for data [HATEOAS](https://en.wikipedia.org/wiki/HATEOAS) support if exists. See **deserializeResponse** method at [MessageUtils](/GsonForRestfulClient/src/rest/client/gson/message/MessageUtils.java) class.

## Example
### Data Transfer Object
```java
public class DecisionDTO {
    private BigDecimal decisionId;
    private String folio;
    private String topic;
    private Date takenDate;
    private String event;
    private Date startDate;
    private Date finishDate;
    private String state;
    private String priority;
    private String description;
    private Long attachments;

    public DecisionDTO() {
    }
    
    public BigDecimal getDecisionId() {
        return decisionId;
    }

    public void setDecisionId(BigDecimal decisionId) {
        this.decisionId = decisionId;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Date getTakenDate() {
        return takenDate;
    }

    public void setTakenDate(Date takenDate) {
        this.takenDate = takenDate;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getAttachments() {
        return attachments;
    }

    public void setAttachments(Long attachments) {
        this.attachments = attachments;
    }
}
```

### Filter class
```java
public class DecisionFilter{
    private Date startDate;
    private Date finishDate;
    private int state;
    private int event;
    
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }

    public Date getFinishDate() {
        return finishDate;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public void setEvent(int event) {
        this.event = event;
    }

    public int getEvent() {
        return event;
    }
}
```

### Client class
```java
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import rest.client.gson.JsonClient;
import rest.client.gson.message.MessageUtils;
import static rest.gson.common.MessageReservedWord.*;
import rest.gson.common.filter.FiltersJsonDataBuilder;
import org.glassfish.jersey.uri.UriComponent;

public class Client{
    public List<DecisionDTO> getDecisions(String targetUrl, DecisionFilter filter){
        JsonClient.JsonClientBuilder clientBuilder =  new JsonClient.JsonClientBuilder(targetUrl);
        if(filter != null){
            FiltersJsonDataBuilder filterBuilder = new FiltersJsonDataBuilder();
            boolean flag = false;
            if(filter.getState() > 0){
                flag = true;
                filterBuilder.addFilterPredicate("p.stateFk.stateId", BigDecimal.class).
                    addFilter(EQ_OPERATOR, BigDecimal.valueOf(filter.getState()));
            }
            if(filter.getEvent() > 0){
                flag = true;
                filterBuilder.addFilterPredicate("p.eventFk.eventId", BigDecimal.class).
                    addFilter(EQ_OPERATOR, BigDecimal.valueOf(filter.getEvent()));
            }
            if(filter.getStartDate() != null){
                flag = true;
                filterBuilder.addFilterPredicate("p.termFk.startDate", Date.class).
                    addFilter(GE_OPERATOR, filter.getStartDate());
            }
            if(filter.getFinishDate() != null){
                flag = true;
                filterBuilder.addFilterPredicate("p.termFk.finishDate", Date.class).
                    addFilter(LE_OPERATOR, filter.getFinishDate());
            }
            if(flag)
                clientBuilder.queryParam("filter", 
                                     UriComponent.encode(filterBuilder.build(),
                                                         UriComponent.Type.QUERY_PARAM_SPACE_ENCODED));
        }
        
        JsonClient client = clientBuilder.build();
        String response = client.get();
        ResponseMessage<DecisionDTO> responseMessage = 
            MessageUtils.deserializeResponse(DecisionDTO.class,
                                             response,
                                             new JsonDeserializer<DecisionDTO>(){
                @Override
                public DecisionDTO deserialize(JsonElement jsonElement, Type type,
                                          JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                    DecisionDTO ldt = new DecisionDTO();
                    JsonObject jo = jsonElement.getAsJsonObject();
                    ldt.setDecisionId(jo.get("decisionId").getAsBigDecimal());
                    ldt.setFolio(jo.get("folio").getAsString());
                    ldt.setTopic(jo.get("topic").getAsString());
                    ldt.setTakenDate(new Date(jo.get("emissionDate").getAsLong()));
                    ldt.setEvent(jo.get("event").getAsString());
                    ldt.setStartDate(new Date(jo.get("startDate").getAsLong()));
                    ldt.setFinishDate(new Date(jo.get("finishDate").getAsLong()));
                    ldt.setState(jo.get("state").getAsString());
                    ldt.setPriority(jo.get("priority").getAsString());
                    ldt.setDescription(jo.get("description").getAsString());
                    ldt.setAttachments(jo.get("attachments").getAsInt());
                    return ldt;
                }
            });
        return responseMessage.getData();
    }
}
```

## GsonForRestfulClient Class Diagram
![Class diagram](/GsonForRestfulClient/GsonForRestfulClient.jpg?raw=true "GsonForRestfulClient Class Diagram")
