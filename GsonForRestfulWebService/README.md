# GsonForRestfulWebService
GsonForRestfulWebService is a Java library which can be used to design a [Restful](https://en.wikipedia.org/wiki/Representational_state_transfer) [Web Service](https://en.wikipedia.org/wiki/Web_service) which support [HATEOAS](https://en.wikipedia.org/wiki/HATEOAS), ease [CRUD](https://es.wikipedia.org/wiki/CRUD) operations through [DAO Pattern](https://es.wikipedia.org/wiki/Data_Access_Object), also allow filtering, ordering, pagination, support for [DTO](https://en.wikipedia.org/wiki/Data_transfer_object) class, and decouples [Service](https://en.wikipedia.org/wiki/Web_service) and [Client](https://en.wikipedia.org/wiki/Client_%28computing%29) throug [GSON](https://github.com/google/gson) library which can work with arbitrary Java objects including pre-existing objects that you do not have source-code of. Thus [Service](https://en.wikipedia.org/wiki/Web_service) and [Client](https://en.wikipedia.org/wiki/Client_%28computing%29) may decide how serialize/deserialize the data to fit your needs. The communication [Service](https://en.wikipedia.org/wiki/Web_service)-[Client](https://en.wikipedia.org/wiki/Client_%28computing%29) is strongly recommended to be like [JSON-Pure](https://mmikowski.github.io/json-pure/) although the comunication [Client](https://en.wikipedia.org/wiki/Client_%28computing%29)-[Service](https://en.wikipedia.org/wiki/Web_service) not always must be like [JSON-Pure](https://mmikowski.github.io/json-pure/), all depends of [Web Service](https://en.wikipedia.org/wiki/Web_service) contract definition.

## Table of Contents
+ [GsonForRestfulWebService Goals](#gsonforrestfulwebservice-goals)
+ [Example](#example)
    - [Entities](#entities)
    - [Data Transfer Object](#data-transfer-object)
    - [Entity Serializers](#entity-serializers)
    - [Entity Merge Deserializers](#entity-merge-deserializers)
    - [Service](#service)
+ [GsonForRestfulWebService Class Diagram](#gsonforrestfulwebservice-class-diagram)

## GsonForRestfulWebService Goals
* Create anonymous implementation for [DAO Pattern](https://es.wikipedia.org/wiki/Data_Access_Object) through [Factory Method Pattern](https://en.wikipedia.org/wiki/Factory_method_pattern) given an **entity** class through [DAOFactory](/GsonForRestfulWebService/src/rest/ws/dao/DAOFactory.java) class.  
* Build [JPQL](https://en.wikipedia.org/wiki/Java_Persistence_Query_Language) query using [JPQLQuery](/GsonForRestfulWebService/src/rest/ws/dao/JPQLQuery.java) class.

* Register **Entity Serializer** class which extends from [AbstractEntitySerializer](/GsonForRestfulWebService/src/rest/ws/gson/serializer/entity/AbstractEntitySerializer.java) class. This serializer lets you define how serialize the entity and its dependencies **(for dependencies is strongly recommended register its Entity Serializer)** and how generate if required the [HATEOAS](https://en.wikipedia.org/wiki/HATEOAS) **Hypermedia as the Engine of Application State** links associated to entity. In the case of [HATEOAS](https://en.wikipedia.org/wiki/HATEOAS) links you can define how will be serialized according to the value of **serializationMode** on [Link](/GsonRestfulCommon/src/rest/gson/common/Link.java) class, if its value is equal [MessageReservedWord](/GsonRestfulCommon/src/rest/gson/common/MessageReservedWord.java).**OBJECT_PROPERTY_SERIALIZATION_MODE** so the list of [Link](/GsonRestfulCommon/src/rest/gson/common/Link.java) will be serialized like [JsonObject](https://github.com/google/gson) otherwise like [JsonArray](https://github.com/google/gson). For [Link](/GsonRestfulCommon/src/rest/gson/common/Link.java) **href** generation the [UriInfo](https://docs.oracle.com/javaee/6/api/javax/ws/rs/core/UriInfo.html) with the request path info  **(useful for extract schema, host and port)** and [ServletContext](http://docs.oracle.com/javaee/6/api/javax/servlet/ServletContext.html) **(useful for extract servlet name)** is available. See **registerEntitySerializer** method at [GsonUtils](/GsonForRestfulWebService/src/rest/ws/gson/GsonUtils.java) class and [GsonRegistry](/GsonForRestfulWebService/src/rest/ws/gson/GsonRegistry.java) class.

*  Register **Entity Merge Deserializer** class which extends from [AbstractMergeDeserializer](/GsonForRestfulWebService/src/rest/ws/gson/deserializer/merge/AbstractMergeDeserializer.java) class. The proccess of **merge** an entity is needed because if an **entity** have 5 properties and if only 4 are sent to the [Client](https://en.wikipedia.org/wiki/Client_%28computing%29) on a [DTO](https://en.wikipedia.org/wiki/Data_transfer_object), then when the [Client](https://en.wikipedia.org/wiki/Client_%28computing%29) invoke for example an update request with the 4 previously sent modified and are simply deserialized on an **entity** instance, the 5th property will be **null**. If the 5th property is **null** so when the [EntityDAO](/GsonForRestfulWebService/src/rest/ws/dao/EntityDAO.java) invoke the **merge** method of [JPA](https://es.wikipedia.org/wiki/Java_Persistence_API) [EntityManager](http://www.objectdb.com/api/java/jpa/EntityManager) implementation can throw a [ConstraintViolation](http://docs.oracle.com/javaee/6/api/javax/validation/ConstraintViolation.html) if the 5th property contains [@NotNull](http://docs.oracle.com/javaee/7/api/javax/validation/constraints/NotNull.html) **annotation** on **entity** class definition or simply the 5th will be updated on data base to **null**, which can cause a loss of information. The [AbstractMergeDeserializer](/GsonForRestfulWebService/src/rest/ws/gson/deserializer/merge/AbstractMergeDeserializer.java) is used on **create** or **update** process since in both the entity must be **merged** with the data received on [Client](https://en.wikipedia.org/wiki/Client_%28computing%29) request. The difference is that in the **update** process the **entity** must be searched on database by **id** before be **merged** with received data, instead in the **create** process only must create a new instane of **entity** before be **merged**. Given this slight difference [AbstractMergeDeserializer](/GsonForRestfulWebService/src/rest/ws/gson/deserializer/merge/AbstractMergeDeserializer.java) use the [Decorator Pattern](https://en.wikipedia.org/wiki/Decorator_pattern), which use the [CreateEntityDeserializer](/GsonForRestfulWebService/src/rest/ws/gson/deserializer/entity/CreateEntityDeserializer.java) class or [SearchEntityDeserializer](/GsonForRestfulWebService/src/rest/ws/gson/deserializer/entity/SearchEntityDeserializer.java) class according to the case. The [AbstractMergeDeserializer](/GsonForRestfulWebService/src/rest/ws/gson/deserializer/merge/AbstractMergeDeserializer.java) class also contains the mechanism to **merge** the entity dependencies **(for dependencies is strongly recommended register its Entity Merge Deserializer)**. See **registerEntityMergeDeserializer** method at [GsonUtils](/GsonForRestfulWebService/src/rest/ws/gson/GsonUtils.java) class, [GsonRegistry](/GsonForRestfulWebService/src/rest/ws/gson/GsonRegistry.java) class, [EntityRequestDeserializer](/GsonForRestfulWebService/src/rest/ws/gson/deserializer/request/EntityRequestDeserializer.java) class, [CreateEntityRequestDeserializer](/GsonForRestfulWebService/src/rest/ws/gson/deserializer/request/create/CreateEntityRequestDeserializer.java) class and [UpdateEntityRequestDeserializer](/GsonForRestfulWebService/src/rest/ws/gson/deserializer/request/update/UpdateEntityRequestDeserializer.java) class.

* Register **Entity Primary Key** which represent the name of **entity** class **attribute** which contains the [@Id](http://www.objectdb.com/java/jpa/entity/id) **annotation**. This information is used on **update** and **delete** process through [EntityMetadata](/GsonForRestfulWebService/src/rest/ws/gson/deserializer/entity/EntityMetadata.java) class, which will be used on [SearchEntityDeserializer](/GsonForRestfulWebService/src/rest/ws/gson/deserializer/entity/SearchEntityDeserializer.java) class. See **registerEntityPrimaryKeyName** method at [GsonUtils](/GsonForRestfulWebService/src/rest/ws/gson/GsonUtils.java) class and [GsonRegistry](/GsonForRestfulWebService/src/rest/ws/gson/GsonRegistry.java) class.

* Register [Data Transfer Objects](https://en.wikipedia.org/wiki/Data_transfer_object) class, which will be used on [GET](https://es.wikipedia.org/wiki/Hypertext_Transfer_Protocol) request. See **registerDtoClass** method at [GsonUtils](/GsonForRestfulWebService/src/rest/ws/gson/GsonUtils.java) class, [GsonRegistry](/GsonForRestfulWebService/src/rest/ws/gson/GsonRegistry.java) class and [ReadRequestDeserializer](/GsonForRestfulWebService/src/rest/ws/gson/deserializer/request/read/ReadRequestDeserializer.java) class.

* Deserialize [JSON-Pure](https://mmikowski.github.io/json-pure/) **request** to [Message](/GsonForRestfulWebService/src/rest/ws/gson/message/Message.java) class. See **deserializeJson2Message** at [GsonUtils](/GsonForRestfulWebService/src/rest/ws/gson/GsonUtils.java) class.

* Serialize [Message](/GsonForRestfulWebService/src/rest/ws/gson/message/Message.java) to [JSON-Pure](https://mmikowski.github.io/json-pure/) **response**. See **serializeMessage** method at [GsonUtils](/GsonForRestfulWebService/src/rest/ws/gson/GsonUtils.java) class.

* Build a [JSON-Pure](https://mmikowski.github.io/json-pure/) **request** given **action type**, **data type** and **data** request. See **buildMessageRequestAsJson** method at [GsonUtils](/GsonForRestfulWebService/src/rest/ws/gson/GsonUtils.java) class.

* Build a [JSON-Pure](https://mmikowski.github.io/json-pure/) **response** given [UriInfo](https://docs.oracle.com/javaee/6/api/javax/ws/rs/core/UriInfo.html), [ServletContext](http://docs.oracle.com/javaee/6/api/javax/servlet/ServletContext.html), [EntityManager](http://www.objectdb.com/api/java/jpa/EntityManager) and [JSON-Pure](https://mmikowski.github.io/json-pure/) **request**. See **buildResponse** method at [GsonUtils](/GsonForRestfulWebService/src/rest/ws/gson/GsonUtils.java) class.

* Serialize an **entity** instance to [JSON-Pure](https://mmikowski.github.io/json-pure/) **response** given [UriInfo](https://docs.oracle.com/javaee/6/api/javax/ws/rs/core/UriInfo.html), [ServletContext](http://docs.oracle.com/javaee/6/api/javax/servlet/ServletContext.html), [EntityManager](http://www.objectdb.com/api/java/jpa/EntityManager), **entity** class and instance. See **serializeT2Message** method at [GsonUtils](/GsonForRestfulWebService/src/rest/ws/gson/GsonUtils.java) class.

* Serialize a list of **entity** to [JSON-Pure](https://mmikowski.github.io/json-pure/) **response** given [UriInfo](https://docs.oracle.com/javaee/6/api/javax/ws/rs/core/UriInfo.html), [ServletContext](http://docs.oracle.com/javaee/6/api/javax/servlet/ServletContext.html), [EntityManager](http://www.objectdb.com/api/java/jpa/EntityManager), **entity** class and the list of elements. See **serializeListT2Message** method at [GsonUtils](/GsonForRestfulWebService/src/rest/ws/gson/GsonUtils.java) class.

* Generate [JSON-Pure](https://mmikowski.github.io/json-pure/) **request data** for filtering, ordering, pagination, etc. See [MessageRequestFilterJsonDataBuilder](/GsonForRestfulWebService/src/rest/ws/gson/deserializer/request/MessageRequestFilterJsonDataBuilder.java) class, [JPQLFilterPredicateDeserializer](/GsonForRestfulWebService/src/rest/ws/gson/deserializer/request/jpql/JPQLFilterPredicateDeserializer.java) class, [JPQLOrderByPredicateDeserializer](/GsonForRestfulWebService/src/rest/ws/gson/deserializer/request/jpql/JPQLOrderByPredicateDeserializer.java) class and [ReadRequestDeserializer](/GsonForRestfulWebService/src/rest/ws/gson/deserializer/request/read/ReadRequestDeserializer.java) class.

**[Back to top](#table-of-contents)**

## Example
### Entities
```java

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author alexander.escalona
 */
@Entity
@Table(name = "DECISION")
@NamedQueries({
    @NamedQuery(name = "Decision.findAll", query = "SELECT p FROM Decision p"),
    @NamedQuery(name = "Decision.findByDecisionId", query = "SELECT p FROM Decision p WHERE p.decisionId = :decisionId"),
    @NamedQuery(name = "Decision.findByFolio", query = "SELECT p FROM Decision p WHERE p.folio = :folio"),
    @NamedQuery(name = "Decision.findByemissionDate", query = "SELECT p FROM Decision p WHERE p.emissionDate = :emissionDate"),
    @NamedQuery(name = "Decision.findBytopic", query = "SELECT p FROM Decision p WHERE p.topic = :topic"),
    @NamedQuery(name = "Decision.findBydescription", query = "SELECT p FROM Decision p WHERE p.description = :description"),
    @NamedQuery(name = "Decision.findByfiled", query = "SELECT p FROM Decision p WHERE p.filed = :filed"),
    @NamedQuery(name = "Decision.findByreaded", query = "SELECT p FROM Decision p WHERE p.readed = :readed")})
@XmlRootElement
public class Decision implements Serializable {

    
    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "decisionId")
    private BigDecimal decisionId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "folio")
    private String folio;
    @Basic(optional = false)
    @NotNull
    @Column(name = "emissionDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date emissionDate;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "topic")
    private String topic;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 4000)
    @Column(name = "description")
    private String description;
    @Basic(optional = false)
    @NotNull
    @Column(name = "filed")
    private Character filed;
    @Basic(optional = false)
    @NotNull
    @Column(name = "readed")
    private Character readed;
    @JoinColumn(name = "stateFk", referencedColumnName = "stateId")
    @ManyToOne(optional = false)
    private State stateFk;
    @JoinColumn(name = "eventFk", referencedColumnName = "eventId")
    @ManyToOne(optional = false)
    private Event eventFk;
    @JoinColumn(name = "priorityFk", referencedColumnName = "priorityId")
    @ManyToOne(optional = false)
    private Priority priorityFk;
	@JoinColumn(name = "termFk", referencedColumnName = "termId")
    @ManyToOne(optional = false)
    private Term termFk;
    @OneToMany(mappedBy = "decisionFk")
    private Collection<Attachment> attachments;
	
    public Decision() {
    }

    public Decision(BigDecimal decisionId) {
        this.decisionId = decisionId;
    }

    public Decision(BigDecimal decisionId, String folio, Date emissionDate, String topic, String description, Character filed, Character readed) {
        this.decisionId = decisionId;
        this.folio = folio;
        this.emissionDate = emissionDate;
        this.topic = topic;
        this.description = description;
        this.filed = filed;
        this.readed = readed;
    }

    public BigDecimal getDecisionId() {
        return decisionId;
    }

    public void setDecisionId(BigDecimal decisionId) {
        this.decisionId = decisionId;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public Date getEmissionDate() {
        return emissionDate;
    }

    public void setEmissionDate(Date emissionDate) {
        this.emissionDate = emissionDate;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Character getFiled() {
        return filed;
    }

    public void setFiled(Character filed) {
        this.filed = filed;
    }

    public Character getReaded() {
        return readed;
    }

    public void setReaded(Character readed) {
        this.readed = readed;
    }

    public State getStateFk() {
        return stateFk;
    }

    public void setStateFk(State stateFk) {
        this.stateFk = stateFk;
    }

    public Event getEventFk() {
        return eventFk;
    }

    public void setEventFk(Event eventFk) {
        this.eventFk = eventFk;
    }

    public Priority getPriorityFk() {
        return priorityFk;
    }

    public void setPriorityFk(Priority priorityFk) {
        this.priorityFk = priorityFk;
    }
    
    public Collection<Attachment> geAttachments() {
        return attachments;
    }

    public void setAttachments(Collection<Content> attachments) {
        this.attachments = attachments;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (decisionId != null ? decisionId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Decision)) {
            return false;
        }
        Decision other = (Decision) object;
        if ((this.decisionId == null && other.decisionId != null) || (this.decisionId != null && !this.decisionId.equals(other.decisionId))) {
            return false;
        }
        return true;
    }

    public Term getTermFk() {
        return termFk;
    }

    public void setTermFk(Term termFk) {
        this.termFk = termFk;
    }
    
}

```

```java
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author alexander.escalona
 */
@Entity
@Table(name = "STATE")
@NamedQueries({
    @NamedQuery(name = "State.findAll", query = "SELECT p FROM State p"),
    @NamedQuery(name = "State.findBystateId", query = "SELECT p FROM State p WHERE p.stateId = :stateId"),
    @NamedQuery(name = "State.findBystate", query = "SELECT p FROM State p WHERE p.state = :state"),
    @NamedQuery(name = "State.findByenabled", query = "SELECT p FROM State p WHERE p.enabled = :enabled")})
@XmlRootElement
public class State implements Serializable {

    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "stateId")
    private BigDecimal stateId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "state")
    private String state;
    @Basic(optional = false)
    @NotNull
    @Column(name = "enabled")
    private Character enabled;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "stateFk")
    private Collection<Decision> decisions;

    public State() {
    }

    public State(BigDecimal stateId) {
        this.stateId = stateId;
    }

    public State(BigDecimal stateId, String state, Character enabled) {
        this.stateId = stateId;
        this.state = state;
        this.enabled = enabled;
    }

    public BigDecimal getStateId() {
        return stateId;
    }

    public void setStateId(BigDecimal stateId) {
        this.stateId = stateId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Character getEnabled() {
        return enabled;
    }

    public void setEnabled(Character enabled) {
        this.enabled = enabled;
    }

    @XmlTransient
    public Collection<Decision> getDecisions() {
        return decisions;
    }

    public void setDecisions(Collection<Decision> decisions) {
        this.decisions = decisions;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (stateId != null ? stateId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof State)) {
            return false;
        }
        State other = (State) object;
        if ((this.stateId == null && other.stateId != null) || (this.stateId != null && !this.stateId.equals(other.stateId))) {
            return false;
        }
        return true;
    }
    
}
```

```java
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author alexander.escalona
 */
@Entity
@Table(name = "EVENT")
@NamedQueries({
    @NamedQuery(name = "Event.findAll", query = "SELECT p FROM Event p"),
    @NamedQuery(name = "Event.findByeventId", query = "SELECT p FROM Event p WHERE p.eventId = :eventId"),
    @NamedQuery(name = "Event.findByevent", query = "SELECT p FROM Event p WHERE p.event = :event"),
    @NamedQuery(name = "Event.findByenabled", query = "SELECT p FROM Event p WHERE p.enabled = :enabled")})
@XmlRootElement
public class Event implements Serializable {

    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "eventId")
    private BigDecimal eventId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "event")
    private String event;
    @Basic(optional = false)
    @NotNull
    @Column(name = "enabled")
    private Character enabled;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "eventFk")
    private Collection<Decision> decisions;

    public Event() {
    }

    public Event(BigDecimal eventId) {
        this.eventId = eventId;
    }

    public Event(BigDecimal eventId, String event, Character enabled) {
        this.eventId = eventId;
        this.event = event;
        this.enabled = enabled;
    }

    public BigDecimal getEventId() {
        return eventId;
    }

    public void setEventId(BigDecimal eventId) {
        this.eventId = eventId;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public Character getEnabled() {
        return enabled;
    }

    public void setEnabled(Character enabled) {
        this.enabled = enabled;
    }

    @XmlTransient
    public Collection<Decision> getDecisions() {
        return decisions;
    }

    public void setDecisions(Collection<Decision> decisions) {
        this.decisions = decisions;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (eventId != null ? eventId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Event)) {
            return false;
        }
        Event other = (Event) object;
        if ((this.eventId == null && other.eventId != null) || (this.eventId != null && !this.eventId.equals(other.eventId))) {
            return false;
        }
        return true;
    }
    
}

```

```java
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author alexander.escalona
 */
@Entity
@Table(name = "PRIORITY")
@NamedQueries({
    @NamedQuery(name = "Priority.findAll", query = "SELECT p FROM Priority p"),
    @NamedQuery(name = "Priority.findBypriorityId", query = "SELECT p FROM Priority p WHERE p.priorityId = :priorityId"),
    @NamedQuery(name = "Priority.findBypriority", query = "SELECT p FROM Priority p WHERE p.priority = :priority"),
    @NamedQuery(name = "Priority.findByenabled", query = "SELECT p FROM Priority p WHERE p.enabled = :enabled")})
@XmlRootElement
public class Priority implements Serializable {

    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "priorityId")
    private BigDecimal priorityId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "priority")
    private String priority;
    @Basic(optional = false)
    @NotNull
    @Column(name = "enabled")
    private Character enabled;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "priorityFk")
    private Collection<Decision> decisions;

    public Priority() {
    }

    public Priority(BigDecimal priorityId) {
        this.priorityId = priorityId;
    }

    public Priority(BigDecimal priorityId, String priority, Character enabled) {
        this.priorityId = priorityId;
        this.priority = priority;
        this.enabled = enabled;
    }

    public BigDecimal getPriorityId() {
        return priorityId;
    }

    public void setPriorityId(BigDecimal priorityId) {
        this.priorityId = priorityId;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Character getEnabled() {
        return enabled;
    }

    public void setEnabled(Character enabled) {
        this.enabled = enabled;
    }

    @XmlTransient
    public Collection<Decision> getDecisions() {
        return decisions;
    }

    public void setDecisions(Collection<Decision> decisions) {
        this.decisions = decisions;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (priorityId != null ? priorityId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Priority)) {
            return false;
        }
        Priority other = (Priority) object;
        if ((this.priorityId == null && other.priorityId != null) || (this.priorityId != null && !this.priorityId.equals(other.priorityId))) {
            return false;
        }
        return true;
    }
}

```

```java
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

/**
 *
 * @author alexander.escalona
 */
@Entity
@Table(name = "TERM")
@NamedQueries({
    @NamedQuery(name = "Term.findAll", query = "SELECT p FROM Term p"),
    @NamedQuery(name = "Term.findBytermId", query = "SELECT p FROM Term p WHERE p.termId = :termId"),
    @NamedQuery(name = "Term.findBystartDate", query = "SELECT p FROM Term p WHERE p.startDate = :startDate"),
    @NamedQuery(name = "Term.findByfinishDate", query = "SELECT p FROM Term p WHERE p.finishDate = :finishDate")})
public class Term implements Serializable {

    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "termId")
    private BigDecimal termId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "startDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;
    @Basic(optional = false)
    @NotNull
    @Column(name = "finishDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date finishDate;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "termFk")
    private Collection<Decision> decisions;

    public Term() {
    }

    public Term(BigDecimal termId) {
        this.termId = termId;
    }

    public Term(BigDecimal termId, Date startDate, Date finishDate) {
        this.termId = termId;
        this.startDate = startDate;
        this.finishDate = finishDate;
    }

    public BigDecimal getTermId() {
        return termId;
    }

    public void setTermId(BigDecimal termId) {
        this.termId = termId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }

    public Collection<Decision> getDecisions() {
        return decisions;
    }

    public void setDecisions(Collection<Decision> decisions) {
        this.decisions = decisions;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (termId != null ? termId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Term)) {
            return false;
        }
        Term other = (Term) object;
        if ((this.termId == null && other.termId != null) || (this.termId != null && !this.termId.equals(other.termId))) {
            return false;
        }
        return true;
    }
    
}

```

```java
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author alexander.escalona
 */
@Entity
@Table(name = "ATTACHMENT")
@NamedQueries({
    @NamedQuery(name = "Attachment.findAll", query = "SELECT p FROM Attachment p"),
    @NamedQuery(name = "Attachment.findByattachmentId", query = "SELECT p FROM Attachment p WHERE p.attachmentId = :attachmentId"),
    @NamedQuery(name = "Attachment.findByMimeType", query = "SELECT p FROM Attachment p WHERE p.mimeType = :mimeType")})
public class Attachment implements Serializable {

    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "attachmentId")
    private BigDecimal attachmentId;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Column(name = "content")
    private Serializable content;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "MIME_TYPE")
    private String mimeType;
    @JoinColumn(name = "decisionFk", referencedColumnName = "decisionId")
    @ManyToOne
    private Decision decisionFk;

    public Attachment() {
    }

    public Attachment(BigDecimal attachmentId) {
        this.attachmentId = attachmentId;
    }

    public Attachment(BigDecimal attachmentId, Serializable content, String mimeType) {
        this.attachmentId = attachmentId;
        this.content = content;
        this.mimeType = mimeType;
    }

    public BigDecimal getattachmentId() {
        return attachmentId;
    }

    public void setattachmentId(BigDecimal attachmentId) {
        this.attachmentId = attachmentId;
    }

    public Serializable getcontent() {
        return content;
    }

    public void setcontent(Serializable content) {
        this.content = content;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Decision getDecisionFk() {
        return decisionFk;
    }

    public void setDecisionFk(Decision decisionFk) {
        this.decisionFk = decisionFk;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (attachmentId != null ? attachmentId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Attachment)) {
            return false;
        }
        Attachment other = (Attachment) object;
        if ((this.attachmentId == null && other.attachmentId != null) || (this.attachmentId != null && !this.attachmentId.equals(other.attachmentId))) {
            return false;
        }
        return true;
    }
    
}

```

**[Back to top](#table-of-contents)**

### Data Transfer Object
```java
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author alexander.escalona
 */
public class DecisionDTO {
    private BigDecimal decisionId;
    private String folio;
    private String topic;
    private Long takenDate;
    private String event;
    private Long startDate;
    private Long finishDate;
    private String state;
    private String priority;
    private String description;
    private Long attachments;

    public DecisionDTO() {
    }
    
    public DecisionDTO(BigDecimal decisionId, String folio, String topic, Date takenDate, String event, Date startDate, Date finishDate, String state, String priority, String description, Long attachments) {
        this.decisionId = decisionId;
        this.folio = folio;
        this.topic = topic;
        this.takenDate = takenDate.getTime();
        this.event = event;
        this.startDate = startDate.getTime();
        this.finishDate = finishDate.getTime();
        this.state = state;
        this.priority = priority;
        this.description = description;
        this.attachments = attachments;
    }
    
    public BigDecimal getDecisionId() {
        return decisionId;
    }

    public void setDecisionId(BigDecimal decisionId) {
        this.decisionId = decisionId;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Long getTakenDate() {
        return takenDate;
    }

    public void setTakenDate(Long takenDate) {
        this.takenDate = takenDate;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public Long getStartDate() {
        return startDate;
    }

    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }

    public Long getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(Long finishDate) {
        this.finishDate = finishDate;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getAttachments() {
        return attachments;
    }

    public void setAttachments(Long attachments) {
        this.attachments = attachments;
    }
}
```

**[Back to top](#table-of-contents)**

### Entity Serializers
```java
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import static rest.ws.gson.GsonUtils.*;
import static rest.gson.common.MessageReservedWord.*;
import rest.ws.gson.serializer.entity.AbstractEntitySerializer;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.UriBuilder;

/**
 *
 * @author alexander.escalona
 */
public class DecisionSerializer extends AbstractEntitySerializer<Decision>{

    @Override
    public JsonElement serialize(Decision resource, Type type, JsonSerializationContext jsc) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("decisionId", resource.getDecisionId());
        jsonObject.addProperty("folio", resource.getFolio());
        jsonObject.addProperty("emissionDate", resource.getEmissionDate().getTime());
        jsonObject.addProperty("topic", resource.getTopic());
        jsonObject.addProperty("description", resource.getDescription());
        jsonObject.addProperty("filed", resource.getFiled());
        jsonObject.addProperty("readed", resource.getReaded());
        
        jsonObject.add("eventFk", jsc.serialize(resource.getEventFk()));
        jsonObject.add("stateFk", jsc.serialize(resource.getStateFk()));
        jsonObject.add("priorityFk", jsc.serialize(resource.getPriorityFk()));
        
        jsonObject.add(LINKS, serializeLinkList(generateLinks(resource), jsc));
        return jsonObject;
    }

    @Override
    public List<Link> generateLinks(Decision resource) {
        URI uri = uriInfo.getBaseUri();
        UriBuilder basePath = UriBuilder.fromPath("").scheme(uri.getScheme()).host(uri.getHost());
        if(uri.getPort() != -1)
            basePath.port(uri.getPort());
        basePath = basePath.path(servletContext.getContextPath()).
                path(ApplicationConfig.PATH).path(DecisionResource.PATH);
        List<Link> link = new ArrayList<>();

        UriBuilder self = basePath.clone().
                path(resource.getDecisionId().toPlainString());
        link.add(new Link.LinkBuilder(self.build().toString()).
                serializationMode(OBJECT_PROPERTY_SERIALIZATION_MODE).
                build());
        return link;
    }
    
}

```

```java
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import rest.gson.common.Link;
import rest.ws.gson.serializer.entity.AbstractEntitySerializer;
import java.lang.reflect.Type;
import java.util.List;

/**
 *
 * @author alexander.escalona
 */
public class StateSerializer extends AbstractEntitySerializer<State>{

    @Override
    public JsonElement serialize(State t, Type type, JsonSerializationContext jsc) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("stateId", t.getStateId());
        jsonObject.addProperty("state", t.getState());
        return jsonObject;
    }

    @Override
    public List<Link> generateLinks(State t) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}

```

```java
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import rest.gson.common.Link;
import rest.ws.gson.serializer.entity.AbstractEntitySerializer;
import java.lang.reflect.Type;
import java.util.List;

/**
 *
 * @author alexander.escalona
 */
public class EventSerializer extends AbstractEntitySerializer<Event>{

    @Override
    public JsonElement serialize(Event t, Type type, JsonSerializationContext jsc) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("eventId", t.getEventId());
        jsonObject.addProperty("event", t.getEvent());
        return jsonObject;
    }

    @Override
    public List<Link> generateLinks(Event t) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}

```

```java
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import rest.gson.common.Link;
import rest.ws.gson.serializer.entity.AbstractEntitySerializer;
import java.lang.reflect.Type;
import java.util.List;

/**
 *
 * @author alexander.escalona
 */
public class PrioritySerializer extends AbstractEntitySerializer<Priority>{

    @Override
    public JsonElement serialize(Priority t, Type type, JsonSerializationContext jsc) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("priorityId", t.getPriorityId());
        jsonObject.addProperty("priority", t.getPriority());
        return jsonObject;
    }

    @Override
    public List<Link> generateLinks(Priority t) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}

```

```java
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import rest.gson.common.Link;
import rest.ws.gson.serializer.entity.AbstractEntitySerializer;
import java.lang.reflect.Type;
import java.util.List;

/**
 *
 * @author alexander.escalona
 */
public class TermSerializer extends AbstractEntitySerializer<Term>{

    @Override
    public JsonElement serialize(Term t, Type type, JsonSerializationContext jsc) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("termId", t.getTermId());
        jsonObject.addProperty("startDate", t.getStartDate().getTime());
        jsonObject.addProperty("finishDate", t.getFinishDate().getTime());
        return jsonObject;
    }

    @Override
    public List<Link> generateLinks(Term resource) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}

```

**[Back to top](#table-of-contents)**

### Entity Merge Deserializers
```java
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import rest.ws.gson.deserializer.merge.AbstractMergeDeserializer;
import java.lang.reflect.Type;
import java.util.Date;

/**
 *
 * @author alexander.escalona
 */
public class MergeDecisionDeserializer extends AbstractMergeDeserializer<Decision>{

    @Override
    public Decision deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {
        Decision entity = deserializer.deserialize(je, type, jdc);
        JsonObject jsonObject = je.getAsJsonObject();
        if(jsonObject.has("folio")) entity.setFolio(jsonObject.get("folio").getAsString());
        if(jsonObject.has("emissionDate")) entity.setEmissionDate(new Date(jsonObject.get("emissionDate").getAsLong()));
        if(jsonObject.has("topic")) entity.setTopic(jsonObject.get("topic").getAsString());
        if(jsonObject.has("description")) entity.setDescription(jsonObject.get("description").getAsString());
        if(jsonObject.has("filed")) entity.setFiled(jsonObject.get("filed").getAsCharacter());
        if(jsonObject.has("readed")) entity.setReaded(jsonObject.get("readed").getAsCharacter());
        if(jsonObject.has("stateFk")){
            State state = mergeSubEntity(State.class, 
                            jsonObject.get("stateFk"), jdc);
            if(state != null)
                entity.setStateFk(state);
        }
        if(jsonObject.has("eventFk")){
            Event event = mergeSubEntity(Event.class, 
                            jsonObject.get("eventFk"), jdc);
            if(event != null)
                entity.setEventFk(event);
        }
        if(jsonObject.has("priorityFk")){
            Priority priority = mergeSubEntity(Priority.class, 
                            jsonObject.get("priorityFk"), jdc);
            if(priority != null)
                entity.setPriorityFk(priority);
        }
        if(jsonObject.has("termFk")){
            Term range = mergeSubEntity(Term.class,
                    jsonObject.get("termFk"), jdc);
            if(range != null)
                entity.setTermFk(range);
        }
        return entity;
    }
    
}

```

```java
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import rest.ws.gson.deserializer.merge.AbstractMergeDeserializer;
import java.lang.reflect.Type;

/**
 *
 * @author alexander.escalona
 */
public class MergeStateDeserializer extends AbstractMergeDeserializer<State>{

    @Override
    public State deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {
        State entity = deserializer.deserialize(je, type, jdc);
        JsonObject jsonObject = je.getAsJsonObject();
        if(jsonObject.has("state")) entity.setState(jsonObject.get("state").getAsString());
        return entity;
    }
    
}

```

```java
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import rest.ws.gson.deserializer.merge.AbstractMergeDeserializer;
import java.lang.reflect.Type;

/**
 *
 * @author alexander.escalona
 */
public class MergeEventDeserializer extends AbstractMergeDeserializer<Event>{

    @Override
    public Event deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {
        Event entity = deserializer.deserialize(je, type, jdc);
        JsonObject jsonObject = je.getAsJsonObject();
        if(jsonObject.has("event")) entity.setEvent(jsonObject.get("event").getAsString());
        return entity;
    }
    
}

```

```java
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import rest.ws.gson.deserializer.merge.AbstractMergeDeserializer;
import java.lang.reflect.Type;

/**
 *
 * @author alexander.escalona
 */
public class MergePriorityDeserializer extends AbstractMergeDeserializer<Priority>{

    @Override
    public Priority deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {
        Priority entity = deserializer.deserialize(je, type, jdc);
        JsonObject jsonObject = je.getAsJsonObject();
        if(jsonObject.has("priority")) entity.setPriority(jsonObject.get("priority").getAsString());
        return entity;
    }
    
}

```

```java
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import rest.ws.gson.deserializer.merge.AbstractMergeDeserializer;
import java.lang.reflect.Type;
import java.util.Date;

/**
 *
 * @author alexander.escalona
 */
public class MergeTermDeserializer extends AbstractMergeDeserializer<Term>{

    @Override
    public Term deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {
        Term entity = deserializer.deserialize(je, type, jdc);
        final JsonObject jsonObject = je.getAsJsonObject();
        if(jsonObject.has("startDate"))
            entity.setStartDate(new Date(jsonObject.get("startDate").getAsLong()));
        if(jsonObject.has("finishDate"))
            entity.setFinishDate(new Date(jsonObject.get("finishDate").getAsLong()));
        return entity;
    }
    
}

```

**[Back to top](#table-of-contents)**

### Service
```java
import java.util.Set;
import javax.ws.rs.core.Application;

/**
 *
 * @author alexander.escalona
 */
@javax.ws.rs.ApplicationPath("webresources")
public class ApplicationConfig extends Application {
    public static final String PATH = "webresources";
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method.
     * It is automatically populated with
     * all resources defined in the project.
     * If required, comment out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(DecisionResource.class);
    }
    
}

```

```java
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import static rest.ws.gson.GsonUtils.*;
import static rest.gson.common.MessageReservedWord.*;
import rest.gson.common.order.OrderByJsonDataBuilder;
import rest.ws.dao.DAOFactory;
import rest.ws.gson.deserializer.request.MessageRequestFilterJsonDataBuilder;
import rest.ws.gson.message.Message;
import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author alexander.escalona
 */
@Stateless
@Path("decisions")
public class DecisionResource {
    public static final String PATH = "decisions";
    @PersistenceContext(unitName = "PAD_DecisionResourcePU")
    private EntityManager entityManager;
    private @Context ServletContext servletContext;

    public DecisionResource() {
        try {
            //Register EntitySerializers
            registerEntitySerializer(Decision.class, DecisionSerializer.class);
            registerEntitySerializer(State.class, StateSerializer.class);
            registerEntitySerializer(Event.class, EventSerializer.class);
            registerEntitySerializer(Priority.class, PrioritySerializer.class);
            registerEntitySerializer(Term.class, TermSerializer.class);
            
            //Register EntityMergeDeserializers
            registerEntityMergeDeserializer(Decision.class, MergeDecisionDeserializer.class);
            registerEntityMergeDeserializer(State.class, MergeStateDeserializer.class);
            registerEntityMergeDeserializer(Event.class, MergeEventDeserializer.class);
            registerEntityMergeDeserializer(Priority.class, MergePriorityDeserializer.class);
            registerEntityMergeDeserializer(Term.class, MergeTermDeserializer.class);
            
            //Register Entity Primary Key
            registerEntityPrimaryKeyName(Decision.class, "decisionId");
            registerEntityPrimaryKeyName(State.class, "stateId");
            registerEntityPrimaryKeyName(Event.class, "eventId");
            registerEntityPrimaryKeyName(Priority.class, "priorityId");
            registerEntityPrimaryKeyName(Term.class, "termId");
            
            //Register Data Transfer Objects
            registerDtoClass(DecisionDTO.class);
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(DecisionResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String crud(@Context UriInfo uriInfo, String request){
        Message response = deserializeJson2Message(request, uriInfo,
                servletContext, entityManager);
        return serializeMessage(uriInfo, servletContext, response);
    }
    
    @PUT
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String edit(@Context UriInfo uriInfo, String request){
        return buildResponse(uriInfo, servletContext,
                entityManager,
                buildMessageRequestAsJson(UPDATE, Decision.class, request));
    }
    
    @DELETE
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public String delete(@Context UriInfo uriInfo, @PathParam("id") BigDecimal id){
        String data = buildMessageRequestSearchByIdJsonData(Decision.class, id.toPlainString());
        return buildResponse(uriInfo, servletContext,
                entityManager, buildMessageRequestAsJson(DELETE, Decision.class, data));
    }
    
    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public String find(@Context UriInfo uriInfo, @PathParam("id") BigDecimal id) {
        return serializeT2Message(uriInfo, servletContext, Decision.class.getCanonicalName(),
                DAOFactory.createEntityDAO(Decision.class, entityManager).find(id));
    }
    
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String findAll(@Context UriInfo uriInfo) {
        return serializeListT2Message(uriInfo, servletContext, Decision.class.getCanonicalName(), 
                DAOFactory.createEntityDAO(Decision.class, entityManager).findAll());
    }
    
    @GET
    @Path("filter")
    @Produces({MediaType.APPLICATION_JSON})
    public String filter(@Context UriInfo uriInfo,
            @QueryParam("filter") String filter,
            @QueryParam("orderBy") String orderBy,
            @QueryParam("pagination") String pagination){
        
        String baseQuery = "SELECT p FROM Decision p ";
        
        String concatWith = (filter == null || filter.trim().isEmpty()) ? null : " WHERE ";
        
        String data = new MessageRequestFilterJsonDataBuilder(baseQuery).
                concatWith(concatWith).
                filter(filter).
                orderBy(orderBy).
                pagination(pagination).
                paginationEnabled(true).
                build();
        
        String request = buildMessageRequestAsJson(RETRIEVE, Decision.class, data);
        return buildResponse(uriInfo, servletContext, entityManager, request);
    }
    
    
    @GET
    @Path("{from}/{to}")
    @Produces({MediaType.APPLICATION_JSON})
    public String findRange(@Context UriInfo uriInfo, @PathParam("from") Integer from, @PathParam("to") Integer to) {
        return serializeListT2Message(uriInfo, servletContext, Decision.class.getCanonicalName(), 
                DAOFactory.createEntityDAO(Decision.class, entityManager).findRange(new int[]{from, to}));
    }

    @GET
    @Path("count")
    @Produces({MediaType.APPLICATION_JSON})
    public String countREST() {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        final Gson gson = gsonBuilder.create();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("count", DAOFactory.createEntityDAO(Decision.class, entityManager).count());
        return gson.toJson(jsonObject);
    }
    
    @GET
    @Path("listDto")
    @Produces({MediaType.APPLICATION_JSON})
    public String listDto(@Context UriInfo uriInfo,
            @QueryParam("filter") String filter){
        
        String baseQuery = String.format("SELECT new %s("
                + "p.decisionId,"
                + " p.folio,"
                + " p.topic,"
                + " p.emissionDate,"
                + " p.eventFk.event,"
                + " p.termFk.startDate,"
                + " p.termFk.finishDate, "
                + " p.stateFk.state,"
                + " p.priorityFk.priority,"
                + " p.description,"
                + " count(select c.attachmentId from Attachment c where c.decisionFk.decisionId = p.decisionId))"
                + " FROM Decision p",
                DecisionDTO.class.getCanonicalName());
        
        String orderBy = new OrderByJsonDataBuilder().
            addOrder("p.termFk.finishDate").
            addOrder("p.priorityFk.priorityId").
            build();
        
        
        String concatWith = (filter == null || filter.trim().isEmpty()) ? null : " WHERE ";
        String data = new MessageRequestFilterJsonDataBuilder(baseQuery).
                concatWith(concatWith).
                filter(filter).
                orderBy(orderBy).
                groupBy("p.decisionId,"
                + " p.folio,"
                + " p.topic,"
                + " p.emissionDate,"
                + " p.eventFk.event,"
                + " p.termFk.startDate,"
                + " p.termFk.finishDate, "
                + " p.stateFk.state,"
                + " p.priorityFk.priority,"
                + " p.description, "
                + " p.priorityFk.priorityId"
                ).
                paginationEnabled(false).
                build();
        String request = buildMessageRequestAsJson(RETRIEVE, DecisionDTO.class, data);
        return buildResponse(uriInfo, servletContext, entityManager, request);
    }
}

```

**[Back to top](#table-of-contents)**

## GsonForRestfulWebService Class Diagram
![Class diagram](/GsonForRestfulWebService/GsonForRestfulWebServiceClassRelation.jpg?raw=true "GsonForRestfulWebService Class Diagram")

**[Back to top](#table-of-contents)**