# GsonForRestful

GsonForRestful is a Java library that can be used to design Json Restful Web Services and/or Client. For serialization and deserialization purpose the [GSON](https://github.com/google/gson) library is used because can work with arbitrary Java objects including pre-existing objects that you do not have source-code of. This last is very useful at deserialization mode because the class design for this purpose can satisfy the Single Responsability Principle easily, also [GSON](https://github.com/google/gson) support the use of Java Generics and register serializer and deserialzer for any class without the use of annotations. 

## GsonForRestful Goals
* Design a group of common class which can be used on service and client. See [GsonRestfulCommon]()
* Ease CRUD operations through DAO pattern and JPA. See [GsonForRestfulWebService]()
* Create anonymous implementation for DAO Pattern through Factory Method Pattern given an entity class. See [GsonForRestfulWebService]()
* Support for [HATEOAS](https://en.wikipedia.org/wiki/HATEOAS) `Hypermedia as the Engine of Application State`. See [GsonForRestfulWebService]()
* Standardizes the data format sended/received between service and client through json using [JSON-Pure](https://mmikowski.github.io/json-pure/). See [GsonForRestfulWebService]()
* Support filtering and ordering building using FiltersJsonDataBuilder and OrderByJsonDataBuilder class at [GsonRestfulCommon](), sending the builded json like query parameter of client GET request which will be converted to [JPQL](https://en.wikipedia.org/wiki/Java_Persistence_Query_Language) according to the case. See [GsonForRestfulWebService]()
* Support [pagination](http://jasonwatmore.com/post/2015/10/30/ASPNET-MVC-Pagination-Example-with-Logic-like-Google.aspx) like google. There are 10 page at any time unless there are less than 10 total pages. See [GsonForRestfulWebService]()
