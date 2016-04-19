/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.gson.common.filter;
import static rest.gson.common.MessageReservedWord.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
/**
 *
 * @author alexander.escalona
 */
public class FilterPredicateJsonDataBuilder<T> {
    private final String field;
    private String dataType;
    private List<FilterOperation> filters;

    public FilterPredicateJsonDataBuilder(String field, Class<T> dataTypeClass) {
        if(field == null)
            throw new IllegalArgumentException("The field can not be null.");
        this.field = field;
        if(dataTypeClass.equals(Boolean.class))
            dataType = dataTypeClass.getSimpleName();
        else if(dataTypeClass.equals(Number.class))
            dataType = dataTypeClass.getSimpleName();
        else if(dataTypeClass.equals(String.class))
            dataType = dataTypeClass.getSimpleName();
        else if(dataTypeClass.equals(Double.class))
            dataType = dataTypeClass.getSimpleName();
        else if(dataTypeClass.equals(Float.class))
            dataType = dataTypeClass.getSimpleName();
        else if(dataTypeClass.equals(Long.class))
            dataType = dataTypeClass.getSimpleName();
        else if(dataTypeClass.equals(Integer.class))
            dataType = dataTypeClass.getSimpleName();
        else if(dataTypeClass.equals(Byte.class))
            dataType = dataTypeClass.getSimpleName();
        else if(dataTypeClass.equals(Character.class))
            dataType = dataTypeClass.getSimpleName();
        else if(dataTypeClass.equals(BigDecimal.class))
            dataType = dataTypeClass.getSimpleName();
        else if(dataTypeClass.equals(BigInteger.class))
            dataType = dataTypeClass.getSimpleName();
        else if(dataTypeClass.equals(Short.class))
            dataType = dataTypeClass.getSimpleName();
        else if(dataTypeClass.equals(Date.class))
            dataType = dataTypeClass.getSimpleName();
        else throw new IllegalArgumentException(String.format("The class '%s' is not allowed as data type.", dataTypeClass.getCanonicalName()));
    }
    
    private boolean isValid(String operator){
        switch(operator){
            case LT_OPERATOR:
            case LE_OPERATOR:
            case EQ_OPERATOR:
            case NE_OPERATOR:
            case GT_OPERATOR:
            case GE_OPERATOR:
            case NU_OPERATOR:
            case LK_OPERATOR:
            case NL_OPERATOR:
                return true;
            default:
                return false;
        }
    }
    
    private FilterPredicateJsonDataBuilder<T> addFilter(String operator, String value,
            boolean applyQuotes){
        if(isValid(operator))
        {
            if(filters == null)
                filters = new ArrayList<>();
            if(applyQuotes)
                filters.add(new FilterOperation(operator, String.format("\"%s\"", value)));
            else filters.add(new FilterOperation(operator, value));
            return this;
        }
        else throw new IllegalArgumentException(String.format("The '%s' is not a valid operator.", operator));
    }
    
    public FilterPredicateJsonDataBuilder<T> addFilter(String operator, T value){
        if(value == null)
            return addFilter(operator, String.valueOf(value), false);
        if(value.getClass().equals(Boolean.class) ||
                value.getClass().equals(String.class) ||
                value.getClass().equals(Character.class) ||
                value.getClass().equals(Character.class))
            return addFilter(operator, String.valueOf(value), true);
        else if(value.getClass().equals(BigDecimal.class))
            return addFilter(operator, String.valueOf(value), false);
        else if(value.getClass().equals(Date.class))
            return addFilter(operator, String.valueOf(((Date)value).getTime()), false);
        else return addFilter(operator, String.valueOf(value), false);
    }
    
    public FilterPredicateJsonDataBuilder<T> isNullFilter(boolean isNull){
        return addFilter(NU_OPERATOR, String.valueOf(isNull), true);
    }
    
    public FilterPredicateJsonDataBuilder<T> likeFilter(String pattern){
        return addFilter(LK_OPERATOR, pattern, true);
    }
    
    public FilterPredicateJsonDataBuilder<T> notLikeFilter(String pattern){
        return addFilter(NL_OPERATOR, pattern, true);
    }
    
    public String build(){
        if(filters == null)
            return null;
        StringBuilder builder = new StringBuilder().
                append("{").
                append(String.format("\"%s\":\"%s\",", FIELD, field)).
                append(String.format("\"%s\":\"%s\",", FIELD_TYPE, dataType)).
                append(String.format("\"%s\":{", FILTER));
        boolean flag = false;
        for(FilterOperation fo : filters){
            builder.append(String.format("%s\"%s\":%s",
                    flag ? "," : "", fo.getOperator(), fo.getValue()));
            flag = true;
        }
        if(flag)
            return builder.append("}}").toString();
        return null;
    }
    
    
}
