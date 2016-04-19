/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.client.gson.message;

import rest.gson.common.Link;
import rest.gson.common.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author alexander.escalona
 */
public class ResponseMessage<T> implements IGsonResponseVisitor<T>{
    private String action;
    private String dataType;
    private String dataContainer;
    private List<Log> logs;
    private LinkProvider<Integer> paginationLinks;
    private List<T> data;
    private HashMap<Integer, LinkProvider<String>> dataLinks;

    @Override
    public void visitAction(String action) {
        this.action = action;
    }

    @Override
    public void visitDataType(String dataType) {
        this.dataType = dataType;
    }

    @Override
    public void visitDataContainer(String dataContainer) {
        this.dataContainer = dataContainer;
    }

    @Override
    public void visitLog(Log log) {
        if(logs == null)
            logs = new ArrayList<>();
        logs.add(log);
    }

    @Override
    public void visitPaginationLink(Link link) {
        if(paginationLinks == null)
            paginationLinks = new LinkProvider<>(Integer.class);
        paginationLinks.addLink(link);
    }

    @Override
    public void visitDataItem(T value) {
        if(data == null)
            data = new ArrayList<>();
        data.add(value);
    }

    @Override
    public void visitDataItemLink(Link link, int dataIndex) {
        if(dataLinks == null)
            dataLinks = new HashMap<>();
        if(!dataLinks.containsKey(dataIndex))
            dataLinks.put(dataIndex, new LinkProvider(String.class));
        dataLinks.get(dataIndex).addLink(link);
    }

    public String getAction() {
        return action;
    }

    public String getDataType() {
        return dataType;
    }

    public String getDataContainer() {
        return dataContainer;
    }

    public List<Log> getLogs() {
        return logs;
    }

    public LinkProvider<Integer> getPaginationLinks() {
        return paginationLinks;
    }

    public List<T> getData() {
        return data;
    }

    public HashMap<Integer, LinkProvider<String>> getDataLinks() {
        return dataLinks;
    }
}
