/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.gson.common.pagination;

import static rest.gson.common.MessageReservedWord.*;

/**
 *
 * @author alexander.escalona
 */
public class PaginationJsonDataBuilder {
    private Integer page;
    private Integer limit;
    private Boolean calculate;

    public PaginationJsonDataBuilder(Integer limit) {
        if(limit <= 0)
            throw new IllegalArgumentException("The limit can not be <= 0.");
        this.limit = limit;
    }

    public PaginationJsonDataBuilder page(Integer page) {
        if(page <= 0)
            throw new IllegalArgumentException("The page can not be <= 0.");
        this.page = page;
        return this;
    }

    public PaginationJsonDataBuilder calculate(Boolean calculate) {
        this.calculate = calculate;
        return this;
    }
    
    public String build(){
        String[] keys = new String[]{PAGE, LIMIT, CALCULATE};
        String[] data = new String[]{page == null ? null : String.valueOf(page),
            String.valueOf(limit),
            calculate == null ? 
                null : String.format("\"%s\"", String.valueOf(calculate))};
        StringBuilder builder = new StringBuilder().
                append("{");
        boolean flag = false;
        for(int i = 0, length = data.length; i < length; i++)
            if(data[i] != null){
                builder.append(String.format("%s\"%s\":%s",
                    flag ? "," : "", keys[i], data[i]));
                flag = true;
            }
        return builder.append("}").toString();
    }
    
    
    
    
    
    
}
