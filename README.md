# GsonForRestful

GsonForRestful is a Java library that can be used to design a [Json](https://es.wikipedia.org/wiki/JSON) [Restful](https://en.wikipedia.org/wiki/Representational_state_transfer) [Web Services](https://en.wikipedia.org/wiki/Web_service) and/or [Client](https://en.wikipedia.org/wiki/Client_%28computing%29). For serialization and deserialization purpose the [GSON](https://github.com/google/gson) library is used because can work with arbitrary Java objects including pre-existing objects that you do not have source-code of. This last is very useful at deserialization mode because the class design for this purpose can satisfy the [Single Responsability Principle](https://en.wikipedia.org/wiki/Single_responsibility_principle) easily, also [GSON](https://github.com/google/gson) support the use of [Java Generics](https://en.wikipedia.org/wiki/Generics_in_Java) and register serializer and deserializer for any class without the use of annotations. 

## GsonForRestful Goals
* Design a group of common class which can be used on [Service](https://en.wikipedia.org/wiki/Web_service) and [Client](https://en.wikipedia.org/wiki/Client_%28computing%29). See [GsonRestfulCommon](/GsonRestfulCommon/README.md) library.

* Ease [CRUD](https://es.wikipedia.org/wiki/CRUD) operations through [DAO Pattern](https://es.wikipedia.org/wiki/Data_Access_Object) and [JPA](https://es.wikipedia.org/wiki/Java_Persistence_API). See [GsonForRestfulWebService](/GsonForRestfulWebService/README.md) library.

* Create anonymous implementation for [DAO Pattern](https://es.wikipedia.org/wiki/Data_Access_Object) through [Factory Method Pattern](https://en.wikipedia.org/wiki/Factory_method_pattern) given an **entity** class. See [GsonForRestfulWebService](/GsonForRestfulWebService/README.md) library.

* Support for [HATEOAS](https://en.wikipedia.org/wiki/HATEOAS) **Hypermedia as the Engine of Application State**. See [GsonForRestfulWebService](/GsonForRestfulWebService/README.md) library.

* Support filtering and ordering using [FiltersJsonDataBuilder](/GsonRestfulCommon/src/rest/gson/common/filter/FiltersJsonDataBuilder.java) and [OrderByJsonDataBuilder](/GsonRestfulCommon/src/rest/gson/common/order/OrderByJsonDataBuilder.java) class at [GsonRestfulCommon](/GsonRestfulCommon/README.md), sending the builded [Json](https://es.wikipedia.org/wiki/JSON) like query parameters of client [GET](https://es.wikipedia.org/wiki/Hypertext_Transfer_Protocol) request which will be converted to [JPQL](https://en.wikipedia.org/wiki/Java_Persistence_Query_Language) according to the case. See [GsonForRestfulWebService](/GsonForRestfulWebService/README.md) library.

* Support [pagination](http://jasonwatmore.com/post/2015/10/30/ASPNET-MVC-Pagination-Example-with-Logic-like-Google.aspx) like google. There are 10 page at any time unless there are less than 10 total pages. See [GsonForRestfulWebService](/GsonForRestfulWebService/README.md) library.

* Standardizes the data format sended/received between [Service](https://en.wikipedia.org/wiki/Web_service) and [Client](https://en.wikipedia.org/wiki/Client_%28computing%29) through [Json](https://es.wikipedia.org/wiki/JSON) using [JSON-Pure](https://mmikowski.github.io/json-pure/). See [GsonForRestfulWebService](/GsonForRestfulWebService/README.md) library.

* Ease sending [Client](https://en.wikipedia.org/wiki/Client_%28computing%29) request and deserialize [Restful](https://en.wikipedia.org/wiki/Representational_state_transfer) [Service](https://en.wikipedia.org/wiki/Web_service) response with [JSON-Pure](https://mmikowski.github.io/json-pure/) format. See [GsonForRestfulClient](/GsonForRestfulClient/README.md) library.
