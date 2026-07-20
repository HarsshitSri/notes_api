# Notes API — Module Help

Spring Boot application module for the [Notes API](../README.md) project.

## Package name

The Spring Initializr artifact name `com.Harshit.note-app` is not a valid Java package. This module uses `com.Harshit.note_app` instead.

## Requirements

- Java 21
- Maven 3.8+ (system install; the `.mvn/` wrapper directory is not present in this repository)

## Run locally

From this directory (`note-app/`):

```bash
# H2 in-memory database (no external DB required)
mvn spring-boot:run -Dspring-boot.run.profiles=h2

# PostgreSQL (default profile — requires a running PostgreSQL instance)
mvn spring-boot:run
```

See the [root README](../README.md) for environment variables, API usage, Docker setup, and testing.

## Reference documentation

* [Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot 4.1 Maven Plugin](https://docs.spring.io/spring-boot/4.1.0/maven-plugin)
* [Spring Boot 4.1 — Web](https://docs.spring.io/spring-boot/4.1.0/reference/web/servlet.html)
* [Spring Boot 4.1 — Spring Data JPA](https://docs.spring.io/spring-boot/4.1.0/reference/data/sql.html#data.sql.jpa-and-spring-data)
* [Spring Security reference](https://docs.spring.io/spring-security/reference/)

## Guides

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)
* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
