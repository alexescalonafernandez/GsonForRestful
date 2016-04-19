/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.gson.common.filter;

/**
 *
 * @author alexander.escalona
 */
public class FilterOperation {
    private final String operator;
    private final String value;

    public FilterOperation(String operator, String value) {
        this.operator = operator;
        this.value = value;
    }

    public String getOperator() {
        return operator;
    }

    public String getValue() {
        return value;
    }
    
    
}
