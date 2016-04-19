# GsonForRestful

GsonForRestful is a Java library that can be used to design Json Restful Web Services and/or Client. For serialization and deserialization purpose the [GSON](https://github.com/google/gson) library is used because can work with arbitrary Java objects including pre-existing objects that you do not have source-code of. This last is very useful at deserialization mode because the class design for this purpose can satisfy the [Single Responsability Principle](https://en.wikipedia.org/wiki/Single_responsibility_principle) easily, also [GSON](https://github.com/google/gson) support the use of Java Generics and register serializer and deserialzer for any class without the use of annotations. 

## GsonForRestful Goals
* Design a group of common class which can be used on service and client. See [GsonRestfulCommon](/GsonRestfulCommon/README.md)
* Ease [CRUD](https://es.wikipedia.org/wiki/CRUD) operations through [DAO pattern](https://es.wikipedia.org/wiki/Data_Access_Object) and [JPA](https://es.wikipedia.org/wiki/Java_Persistence_API). See [GsonForRestfulWebService](/GsonForRestfulWebService/README.md)
* Create anonymous implementation for [DAO pattern](https://es.wikipedia.org/wiki/Data_Access_Object) through [Factory Method Pattern](https://en.wikipedia.org/wiki/Factory_method_pattern) given an `entity` class. See [GsonForRestfulWebService](/GsonForRestfulWebService/README.md)
* Support for [HATEOAS](https://en.wikipedia.org/wiki/HATEOAS) `Hypermedia as the Engine of Application State`. See [GsonForRestfulWebService](/GsonForRestfulWebService/README.md)
* Support filtering and ordering using [FiltersJsonDataBuilder](/GsonRestfulCommon/src/rest/gson/common/filter/FiltersJsonDataBuilder.java) and [OrderByJsonDataBuilder](/GsonRestfulCommon/src/rest/gson/common/order/OrderByJsonDataBuilder.java) class at [GsonRestfulCommon](/GsonRestfulCommon/README.md), sending the builded json like query parameters of client [GET](https://es.wikipedia.org/wiki/Hypertext_Transfer_Protocol) request which will be converted to [JPQL](https://en.wikipedia.org/wiki/Java_Persistence_Query_Language) according to the case. See [GsonForRestfulWebService](/GsonForRestfulWebService/README.md)
* Support [pagination](http://jasonwatmore.com/post/2015/10/30/ASPNET-MVC-Pagination-Example-with-Logic-like-Google.aspx) like google. There are 10 page at any time unless there are less than 10 total pages. See [GsonForRestfulWebService](/GsonForRestfulWebService/README.md)
* Standardizes the data format sended/received between service and client through json using [JSON-Pure](https://mmikowski.github.io/json-pure/). See [GsonForRestfulWebService](/GsonForRestfulWebService/README.md)
* Ease sending client request and deserialize restful service response with [JSON-Pure](https://mmikowski.github.io/json-pure/) format. See [GsonForRestfulClient](/GsonForRestfulClient/README.md)