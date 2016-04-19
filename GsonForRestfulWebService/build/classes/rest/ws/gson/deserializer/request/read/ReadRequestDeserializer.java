/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.ws.gson.deserializer.request.read;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import static rest.ws.dao.DAOFactory.*;
import rest.ws.gson.deserializer.request.RequestDeserializer;
import rest.ws.gson.deserializer.request.jpql.JPQLFilterPredicateDeserializer;
import rest.ws.gson.deserializer.request.jpql.JPQLOrderByPredicateDeserializer;
import rest.gson.common.Log;
import rest.gson.common.LogLevel;
import rest.ws.gson.message.Message;
import static rest.gson.common.MessageReservedWord.*;
import rest.ws.gson.message.predicate.JPQLPredicate;
import rest.gson.common.Link;
import rest.ws.dao.JPQLQuery;
import java.lang.reflect.Type;
import java.util.List;
import java.util.function.Predicate;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author alexander.escalona
 */
public class ReadRequestDeserializer extends RequestDeserializer{
    private boolean calculate;
    private int currentPage, page, limit = 30, pagePointer, estimatedSize;
    private final UriBuilder baseUri;
    
    public ReadRequestDeserializer(EntityManager entityManager, UriInfo uriInfo, ServletContext servletContext) {
        super(entityManager, uriInfo, servletContext);
        baseUri = getBaseUriBuilder();
    }
    
    @Override
    public String getOperationErrorType() {
        return RETRIEVE_FAIL;
    }
    
    @Override
    public Message getResponse(JsonElement je, Type type, JsonDeserializationContext jdc) {
        JsonObject jsonObject = je.getAsJsonObject();
        if(jsonObject.has(DATA)){
            JsonObject jsonData = jsonObject.get(DATA).getAsJsonObject();
            String baseQuery = jsonData.get(BASE_QUERY).getAsString();
            JPQLQuery.JPQLQueryBuilder queryBuilder = 
                    new JPQLQuery.JPQLQueryBuilder(baseQuery);
            
            if(jsonData.has(CONCAT_WITH))
                queryBuilder.concatWith(jsonData.get(CONCAT_WITH).getAsString());
            
            StringBuilder jpqlFilter = configureFilter(jsonData, jdc);
            if(jpqlFilter.length() > 0)
                queryBuilder.filter(jpqlFilter.toString());
            
             if(jsonData.has(GROUP_BY))
                 queryBuilder.groupBy(jsonData.get(GROUP_BY).getAsString());
             
             if(jsonData.has(HAVING))
                 queryBuilder.having(jsonData.get(HAVING).getAsString());
            
            StringBuilder jpqlOrderBy = configureOrderBy(jsonData, jdc);
            if(jpqlOrderBy.length() > 0)
                queryBuilder.orderBy(jpqlOrderBy.toString());
            
            boolean paginate = true;
            if(jsonData.has(PAGINATION_ENABLED))
                paginate = jsonData.get(PAGINATION_ENABLED).getAsBoolean();
            if(!paginate){
                return new Message.MessageBuilder(RETRIEVED).
                        dataType(entityClass.getCanonicalName()).
                        dataContainer(LIST_CONTAINER).
                        data(createEntityDAO(entityClass, entityManager).
                                filter(queryBuilder.build())).
                        build();
            }
            
            if(jsonData.has(PAGINATION)){
                JsonObject jsonPagination = jsonData.get(PAGINATION).getAsJsonObject();
                if(jsonPagination.has(PAGE))
                    currentPage = jsonPagination.get(PAGE).getAsInt() - 1;
                else currentPage = 0;
                if(jsonPagination.has(CALCULATE))
                    calculate = jsonPagination.get(CALCULATE).getAsBoolean();
                else calculate = true;

                if(jsonPagination.has(LIMIT))
                {
                    limit = jsonPagination.get(LIMIT).getAsInt();
                    if(limit <= 0)
                        limit = 30;
                }
                
                if(calculate){
                    estimatedSize = 10 * limit;
                    if(currentPage < 6){
                        page = 0;
                        pagePointer = currentPage;
                    }
                    else{
                        page = currentPage - 5;
                        pagePointer = 5;
                    }
                }
                else{
                    estimatedSize = limit;
                    page = currentPage;
                    pagePointer = 0;
                }
            }
            else{
                page = currentPage = pagePointer = 0;
                calculate = true;
                limit = 30;
                estimatedSize = 10 * limit;
            }
            
            List messageData = createEntityDAO(entityClass, entityManager).
                    filter(queryBuilder.build(), page * limit, estimatedSize);
            Message.MessageBuilder messageBuilder = new Message.MessageBuilder(RETRIEVED).
                    dataType(entityClass.getCanonicalName()).
                    dataContainer(LIST_CONTAINER);
            int size = messageData.size();
            if(size > 0){
                int pages = size / limit;
                if(size % limit > 0)
                    pages++;
                if(calculate){
                    int total = 0;
                    Predicate<Integer> calculatePredicate;
                    if(pages == 10){
                        if(page == 0)
                            calculatePredicate = buildCalculatePredicate(GT_OPERATOR, 5);
                        else calculatePredicate = buildCalculatePredicate(NE_OPERATOR, -1);
                    }
                    else{
                        total = page + pages;
                        if(total < 10){
                            pagePointer = page;
                            calculatePredicate = buildCalculatePredicate(EQ_OPERATOR, -1);
                        }
                        else{
                            if(currentPage + 1 > total)
                                return messageBuilder.data(messageData.subList(0, 0)).build();
                            page = total - 10;
                            pagePointer = pages - (total - (currentPage + 1)) - 1;
                            pages = 10;
                            calculatePredicate = buildCalculatePredicate(LE_OPERATOR, 5);
                        } 
                    }
                    int fromIndex = pagePointer * limit;
                    int fetch = limit;
                    if(messageData.size() < fromIndex + fetch)
                        fetch -= fromIndex + fetch - messageData.size();
                    messageBuilder.data(messageData.subList(fromIndex, fromIndex + fetch));
                    messageBuilder = configureMessageLinks(messageBuilder, pages,
                            buildLastPagePredicate(total - 1), calculatePredicate);
                }
                else
                {
                    messageBuilder.data(messageData);
                }
            }
            else messageBuilder.data(messageData.subList(0, 0));
            return messageBuilder.build();
        }
        else{
            Log log = new Log.LogBuilder(LogLevel.ERROR).
                    codeKey(String.valueOf(400)).
                    codeStr("Bad Request").
                    userMsg(String.format("Request protocol error. The '%s' property is missed", DATA)).
                    build();
            return new Message.MessageBuilder(getOperationErrorType()).addLog(log).build();
        }
    }
    
    private UriBuilder getBaseUriBuilder(){
        return UriBuilder.fromUri(uriInfo.getAbsolutePath());
    }
    
    private StringBuilder configureJPQL(JsonObject jsonData, JsonDeserializationContext jdc,
            String jpqlPredicateType, JsonDeserializer<JPQLPredicate> jpqlPredicateDeserializer,
            String predicateSeparator)
    {
        StringBuilder builder = new StringBuilder();
        if(jsonData.has(jpqlPredicateType)){
            JsonArray jsonArray = jsonData.get(jpqlPredicateType).getAsJsonArray();
            baseUri.queryParam(jpqlPredicateType, jsonArray.toString());
            boolean flag = false;
            for(JsonElement filter : jsonArray){
                JPQLPredicate value = jpqlPredicateDeserializer.deserialize(filter, JPQLPredicate.class, jdc);
                builder.append(String.format("%s %s", flag ? predicateSeparator : "", value.getJpqlPredicate()));
                flag = true;
            }
        }
        return builder;
    }
    
    private StringBuilder configureFilter(JsonObject jsonData, JsonDeserializationContext jdc){
        return configureJPQL(jsonData, jdc, FILTER, new JPQLFilterPredicateDeserializer(), " AND");
    }
    
    private StringBuilder configureOrderBy(JsonObject jsonData, JsonDeserializationContext jdc){
        return configureJPQL(jsonData, jdc, ORDER_BY, new JPQLOrderByPredicateDeserializer(), ",");
    }
    
    private Predicate<Integer> buildLastPagePredicate(int lastPageValue){
        return p -> p == lastPageValue;
    }
    
    private Predicate<Integer> buildCalculatePredicate(String operator, int value){
        switch(operator){
            case EQ_OPERATOR:
                return p -> p == value;
            case NE_OPERATOR:
                return p -> p != value;
            case LE_OPERATOR:
                return p -> p <= value;
            case GT_OPERATOR:
                return p -> p > value;
            default: return null;
        }
    }
    
    private Message.MessageBuilder configureMessageLinks(Message.MessageBuilder messageBuilder,
            int pages,
            Predicate<Integer> lastPagePredicate,
            Predicate<Integer> calculatePredicate){
        for(int i = 0; i < pages; i++){
            UriBuilder current = baseUri.clone();
            current.queryParam(PAGINATION,
                    String.format("{\"%s\":%d,\"%s\":%d,\"%s\":\"%s\"}",
                            PAGE, page + 1,
                            LIMIT, limit,
                            CALCULATE, String.valueOf(calculatePredicate.test(i))));
            messageBuilder.addLink(new Link.LinkBuilder(
                    current.toString()).
                    rel(String.format("%d", page + 1)).
                    addOptionalData(FIRST_PAGE, String.valueOf(page == 0)).
                    addOptionalData(SELF_PAGE, String.valueOf(page == currentPage)).
                    addOptionalData(LAST_PAGE, String.valueOf(lastPagePredicate.test(page))).
                    build());
            page++;
        }
        return messageBuilder;
    }
    
}
