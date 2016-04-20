# GsonRestfulCommon
GsonRestfulCommon is a Java library which is used by [GsonForRestfulWebService](/GsonForRestfulWebService/README.md) library and [GsonForRestfulClient](/GsonForRestfulClient/README.md) library. 

## GsonRestfulCommon Goals
* Contains all reserved words used in client-service communication in [MessageReservedWord](/GsonRestfulCommon/src/rest/gson/common/MessageReservedWord.java) class.

* Contains [Link](/GsonRestfulCommon/src/rest/gson/common/Link.java) class for [HATEOAS](https://en.wikipedia.org/wiki/HATEOAS) **Hypermedia as the Engine of Application State** support and [Log](/GsonRestfulCommon/src/rest/gson/common/Log.java) class for logging support.

* Create mechanism with [Java Generics](https://en.wikipedia.org/wiki/Generics_in_Java) for given a sequence of **field** and its **field type** store the sequence of **filter** to apply to these **field **at [JPQL](https://en.wikipedia.org/wiki/Java_Persistence_Query_Language) query. The stored **filters** can be converted to **json** for sending like query parameter by client using [FiltersJsonDataBuilder](/GsonRestfulCommon/src/rest/gson/common/filter/FiltersJsonDataBuilder.java) class.

* Create mechanism for given a sequence of **field** store the asecending/descending **order** at [JPQL](https://en.wikipedia.org/wiki/Java_Persistence_Query_Language) query. The stored ordering can be converted to **json** for sending like query parameter by client using [OrderByJsonDataBuilder](/GsonRestfulCommon/src/rest/gson/common/order/OrderByJsonDataBuilder.java) class.

* Create mechanism for configure **pagination** where at least the limit **(how many rows will be fetched)** must be supplied. Optionally the page **(the current page)** and calculate **(for like google pagination, if is true try calculate 10 pages where the current page is the 6th position, otherwise just calculate the current page)** can be supplied. The stored pagination can be converted to json for sending like query parameter by client using [PaginationJsonDataBuilder](/GsonRestfulCommon/src/rest/gson/common/pagination/PaginationJsonDataBuilder.java) class. See [pagination like google](http://jasonwatmore.com/post/2015/10/30/ASPNET-MVC-Pagination-Example-with-Logic-like-Google.aspx).

## Examples
### Generate filters as json example
```java
import static rest.gson.common.MessageReservedWord.*;
import rest.gson.common.filter.FiltersJsonDataBuilder;

public class Main{
    public static void main(String[] args){
        FiltersJsonDataBuilder depFilterBuilder = new FiltersJsonDataBuilder();
        depFilterBuilder.addFilterPredicate("p.departmentId", Short.class).
                addFilter(LT_OPERATOR, Short.valueOf("100")).
                addFilter(GT_OPERATOR, Short.valueOf("10"));
        depFilterBuilder.addFilterPredicate("p.departmentName", String.class).
                addFilter(LK_OPERATOR, "S%");
        System.out.println(depFilterBuilder.build());
    }
}
```
The example prints in console: **[{"field":"p.departmentId","fieldType":"Short","filter":{"lt":100,"gt":10}},{"field":"p.departmentName","fieldType":"String","filter":{"lk":"S%"}}]**

### Generate order by as json example
```java
import rest.gson.common.order.OrderByJsonDataBuilder;

public class Main{
    public static void main(String[] args){
        OrderByJsonDataBuilder orderBuilder = new OrderByJsonDataBuilder();
        String json = orderBuilder.
                addOrder("p.departmentId").
                addOrder("p.departmentName", false).
                build();
        System.out.println(json);
    }
}
```
The example prints in console: **[{"field":"p.year"},{"field":"p.name""orderDirection":"DESC"}]**

### Generate pagination as json example
```java
import rest.gson.common.pagination.PaginationJsonDataBuilder;
public class Main{
    public static void main(String[] args){
        PaginationJsonDataBuilder paginationBuilder = new PaginationJsonDataBuilder(30);
        String json = paginationBuilder.
                page(1).
                calculate(false).
                build();
        System.out.println(json);
    }
}
```
The example prints in console: **{"page":1,"limit":30,"calculate":"false"}**

## GsonRestfulCommon Class Diagram
![Class diagram](/GsonRestfulCommon/GsonRestfulCommon.jpg?raw=true "GsonRestfulCommon Class Diagram")
