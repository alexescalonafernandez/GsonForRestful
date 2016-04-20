/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.ws.gson.deserializer.request;

import static rest.gson.common.MessageReservedWord.*;
import rest.ws.dao.JPQLQuery;
import rest.ws.dao.JPQLQuery.JPQLQueryBuilder;

/**
 *
 * @author alexander.escalona
 */
public class MessageRequesFilterJsonDataBuilder {
    private final JPQLQueryBuilder builder;
    private String pagination;
    private boolean paginationEnabled;

    public MessageRequesFilterJsonDataBuilder(String baseQuery) {
        builder = new JPQLQueryBuilder(baseQuery);
        this.paginationEnabled = true;
    }
    
    public MessageRequesFilterJsonDataBuilder concatWith(String concatWith) {
        builder.concatWith(concatWith);
        return this;
    }

    public MessageRequesFilterJsonDataBuilder filter(String filter) {
        if(filter == null || filter.trim().isEmpty())
            builder.filter(null);
        else builder.filter(filter);
        return this;
    }
    
    public MessageRequesFilterJsonDataBuilder groupBy(String groupBy) {        
        if(groupBy == null || groupBy.trim().isEmpty())
            builder.groupBy(null);
        else builder.groupBy(groupBy);
        return this;
    }

    public MessageRequesFilterJsonDataBuilder having(String having) {
        if(having == null || having.trim().isEmpty())
            builder.having(null);
        else builder.having(having);
        return this;
    }
    
    public MessageRequesFilterJsonDataBuilder orderBy(String orderBy) {
        if(orderBy == null || orderBy.trim().isEmpty())
            builder.orderBy(null);
        else builder.orderBy(orderBy);
        return this;
    }

    public MessageRequesFilterJsonDataBuilder pagination(String pagination) {
        if(pagination == null || pagination.trim().isEmpty())
            this.pagination = null;
        else this.pagination = pagination;
        return this;
    }

    public MessageRequesFilterJsonDataBuilder paginationEnabled(boolean paginationEnabled) {
        this.paginationEnabled = paginationEnabled;
        return this;
    }
    
    public String build(){
        JPQLQuery query = builder.build();
        String[] data = new String[]{quote(query.getBaseQuery()), quote(query.getConcatWith()), query.getFilter(), quote(query.getGroupBy()), quote(query.getHaving()), query.getOrderBy(), quote(String.valueOf(paginationEnabled)), pagination};
        String[] keys = new String[]{BASE_QUERY, CONCAT_WITH, FILTER, GROUP_BY, HAVING, ORDER_BY, PAGINATION_ENABLED, PAGINATION};
        StringBuilder sb = new StringBuilder("{");
        boolean flag = false;
        for(int i = 0, length = data.length; i < length; i++){
            if(data[i] != null){
                sb.append(String.format("%s\"%s\":%s",
                    flag ? "," : "", keys[i], data[i]));
                flag = true;
            }
        }
        sb.append("}");
        return sb.toString();
    }
    
    private String quote(String value){
        if(value == null)
            return null;
        return String.format("\"%s\"", value);
    }
}
