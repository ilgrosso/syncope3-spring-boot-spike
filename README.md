## Introduction

This sample application runs initialization of Apache Syncope persistence Spring beans and also starts an H2 database instance containing 2 domains: `Master` and `Two`.
`Master` domain contains 5 users and `Two` domain a single user.

### How to run

To run application just run:
`mvn clean verify`
and then from the root of the project
`java -jar target/syncope-persistence-jpa-spring-boot.jar`

It will start a REST server listening on 8080 port, exposing 2 APIs: `/users` to list first 10 users (belonging to a specific domain) and `/users/save` to save and user (onto a specific domain and default realm `/`). 

#### Sample API requests

`http://localhost:8080/users?domain=Master`

Response: 

```
[
    "1417acbe-cbf6-4277-9372-e75e04f97000/rossini",
    "74cd8ece-715a-44a4-a736-e17b46c4e7e6/verdi",
    "823074dc-d280-436d-a7dd-07399fae48ec/puccini",
    "b3cbc78d-32e6-4bd4-92e0-bbe07566a2ee/vivaldi",
    "c9b2dec2-00a7-4855-97c0-d854842b4b24/bellini"
]
```

`http://localhost:8080/users/save?domain=Two`

with payload a string representing the username (no other properties could be specified since this is not the goal of this spike project).
