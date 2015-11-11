# poc-restaurants

## Solution architecture

System implementation follows definition. Should other restrictions be applied, another techniques may be preferred.

Two implementations of today's menu fetching is presented - one gives preference to Hibernate features (using filters),
thus hiding some data management code (will use **HS** term), another is clean JPA with Java 8, which looks optimal with 
respect to DB usage (avoid fetching eager collections) - will use **JS** term.

System uses current versions of:

* Java 8 (uses lambda expressions and streams)
* Spring Web/Spring MVC (4.2.2)
* Spring Security
* Spring JDBC/Spring JPA
* Hibernate (5.x)
* MySQL
* Maven as build system
* developed/tested under Apache Tomcat
* live demo (http://poc-rst.ulfr.ru) is running WildFly 9 (undertow servlet container)
 
Live demo login names:

* admin: a@a/test
* user 1: u1@a/test
* user 2: u2@a/test

Though "without frontend" is stated, three pages are provided to simplify multirole testing:

* index page with sample of data extraction
* login page allowing to login as either admin or user (this can be reached without by sending simple POST request)
* admin page allowing to flush db and put test data into it

Password stored as MD5 hashes for simplicity. Production system normally needs "salted" hashes.


### Main considerations

Dishes are added anew each day. Should we want to reuse dishes, mapping table must be introduced, containing
day-dish (if we want availability of same dish on different days) or day-price-dish (if we want same dish with different
price on different days) or day-restaurant-dish (if we want dish to be shared by restaurants) and vice versa.

REST API is used as base model, according to requirements. "Vote" activity is non-typical as REST API is data-centric,
but method tends to be more service API style, which is function-centric (JSONP, SOAP API). STill it is OK for REST API
in this particular case.

CRUD/HTTP mapping general approach is used when defining API:

* CREATE = POST (except for voting, which is mixed create/update)
* READ = GET (get all data)
* UPDATE = PUT (account password change)
* DELETE = DELETE (not used)


### System layers

#### Client browser

Client Browser / REST API test instrumentation can be used to perform requests 

#### Security layer

Spring Security layer (servlet filter chain) performs authentication and authorization of requests, configures
security context.

#### REST API layer 

REST API layer accepts HTTP requests performed by clients, applies security restrictions, then passes control to 
respective methods of DAO. Layer is responsible for security and data wrapping

#### DAO / BL layer

DAO layer combines business logic elements and data access. Data access (DB) is localized there, no DB requests are 
allowed from other components. Should business logic be improved, it should reside in DAO layer, or different layer
should be introduced. Layer is responsible for data integrity.

### Model

JPA/Hibernate annotations are defined on fields rather than getters to make code cleaner in terms of readability.

#### modules.restaurants.model.Restaurant

Represents restaurant. Entity is subject to return directly to caller, without mapping/re-wrapping.
Contains collection of dishes, which can be used for fetching list of today's dishes (**HS**)or
any arbitrary use (**JS**).

**HS** implementation is commented out. **JS** is actual. The following definition should be commented out for **HS** 
to compile:

```
          @Transient
          List<Dish> dishes;
```

Explanation is defined as JavaDoc.

In order to fetch current votes count for a restaurant, Formula column is used. This is Hibernate-specific and easier to
deal with. In order to increase performance DB can be denormalized by adding respective column and maintaining its value
by updates when a user votes, or by implementing trigger and stored procedure in DB to automate the process. Exact
solution is defined based on code style and/or architecture considerations.

#### modules.restaurants.model.Dish

Represents dish. Name is regular text field. Price is defined and BigDecimal according to currency best practices.
Valid on is specific date we want this particular dish on. Restaurant is id of restaurant.

Restaurant may be defined as @ManyToOne object, but this is heavier with respect to DB and would require special 
handling for serialization when sent to client.

#### modules.restaurants.model.Vote

Represents case of value-as-id, when we give preference to DB structure. Technically votes map account to restaurant.

According to ORM recommendations such table is used as key (VoteKey class).

Additional field to Vote object can be added, Date, if we want to keep history of all votes. Some queries should be 
updated as well in this case (e.g. fetching count of votes for restaurant).

#### modules.restaurants.model.Account
 
represents user account. This object is used to determine exact user we are working with (see AbstractController) 

### DAO

All DAO are inherited from Abstract DAO, wiring JPA Entity Manager and DataSource.

#### RestaurantsDao

Performs all activities on our object base, including maintenance (votes table truncation and test data deployment).

#### AccountDao
  
Used for account object fetching by AbstractController

### Controllers

All controllers are inherited from AbstractController providing method to get current user.

#### RestaurantsREST
 
REST API implementation for restaurant-related activities. JSON is used as data transfer protocol. URLs are:

* GET /rest/restaurants/ - list restaurants and dishes proposed today. Public.
* POST /rest/restaurants/{restaurantId}/dish - defines new dish, data is passed as JSON in request body. Admins only.
* GET /rest/restaurants/{restaurantId}/vote - votes for specific restaurant. User only.                              

Technical methods:

* /rest/restaurants/init - truncates tables and inserts test data for dishes

#### AccountREST

REST API implementation for accounts management

* GET /rest/account/ - list of accounts. Admin only.
* PUT /rest/account/password - reset password. User/Admin. Password is passed as {password=value} in request body.

#### RootController

MVC controller for serving pages.

### Web interface

Though not specified in requirements, it is added to simplify testing and login/logout procedure.

FreeMarker is used as template engine for data presentation.


