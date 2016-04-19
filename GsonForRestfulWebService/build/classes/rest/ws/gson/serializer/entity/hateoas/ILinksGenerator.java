/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.ws.gson.serializer.entity.hateoas;

import java.util.List;
import rest.gson.common.Link;
/**
 *
 * @author alexander.escalona
 */
public interface ILinksGenerator<T> {
    public List<Link> generateLinks(T resource);
}
