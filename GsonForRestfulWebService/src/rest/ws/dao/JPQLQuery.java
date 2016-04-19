/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.ws.dao;

/**
 *
 * @author alexander.escalona
 */
public class JPQLQuery {
    private final String baseQuery;
    private final String concatWith;
    private final String filter;
    private final String groupBy;
    private final String having; 
    private final String orderBy;

    private JPQLQuery(JPQLQueryBuilder builder) {
        this.baseQuery = builder.baseQuery;
        this.concatWith = builder.concatWith;
        this.filter = builder.filter;
        this.groupBy = builder.groupBy;
        this.having = builder.having;
        this.orderBy = builder.orderBy;
    }
    
    public static class JPQLQueryBuilder{
        private final String baseQuery;
        private String concatWith;
        private String filter;
        private String groupBy;
        private String having; 
        private String orderBy;

        public JPQLQueryBuilder(String baseQuery) {
            this.baseQuery = baseQuery;
        }

        public JPQLQueryBuilder concatWith(String concatWith) {
            this.concatWith = concatWith;
            return this;
        }

        public JPQLQueryBuilder filter(String filter) {
            this.filter = filter;
            return this;
        }

        public JPQLQueryBuilder groupBy(String groupBy) {
            this.groupBy = groupBy;
            return this;
        }

        public JPQLQueryBuilder having(String having) {
            this.having = having;
            return this;
        }

        public JPQLQueryBuilder orderBy(String orderBy) {
            this.orderBy = orderBy;
            return this;
        }
        
        public JPQLQuery build(){
            return new JPQLQuery(this);
        }
    }

    public String getBaseQuery() {
        return baseQuery;
    }

    public String getConcatWith() {
        return concatWith;
    }

    public String getFilter() {
        return filter;
    }

    public String getGroupBy() {
        return groupBy;
    }

    public String getHaving() {
        return having;
    }

    public String getOrderBy() {
        return orderBy;
    }
    
    
}
