/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.client.gson.message;

import rest.gson.common.Link;
import rest.gson.common.Log;

/**
 *
 * @author alexander.escalona
 */
public interface IGsonResponseVisitor<T> {
    public void visitAction(String action);
    public void visitDataType(String dataType);
    public void visitDataContainer(String dataContainer);
    public void visitLog(Log log);
    public void visitPaginationLink(Link link);
    public void visitDataItem(T value);
    public void visitDataItemLink(Link link, int dataIndex);
}
