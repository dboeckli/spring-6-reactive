# Spring 6 Reactive
Welcome to the "Spring Framework 6: Beginner to Guru" project! This project is designed to help you explore and understand the latest features of Spring Framework 6 through practical code examples. 
Here's a quick guide to get you started and contributing:

## Getting Started:
Server runs on port 8082. Requires the auth server running on port 9000.
The IntelliJ Project runner is starting both server at one (via docker-compose file).

## Project Structure:
`pom.xml`: This is your main Maven configuration file. It manages dependencies, plugins, and build settings.
`src` Directory: Contains your main Java source code and resources, as well as test code.
`restRequests` Directory: Houses resources for REST requests, including authentication HTTP requests and HTTP client configurations.

## Urls

- openapi api-docs: http://localhost:8082/v3/api-docs
- openapi gui: http://localhost:8082/swagger-ui/index.html
- openapi-yaml: http://localhost:8082/v3/api-docs.yaml

## Docker

### create image
```shell
.\mvnw clean package spring-boot:build-image
```
or just run
```shell
.\mvnw clean install
```

### run image

Hint: remove the daemon flag -d to see what is happening, else it run in background

```shell
docker run --name reactive -d -p 8082:8080 -e SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI=http://auth-server:9000 -e SERVER_PORT=8080 --link auth-server:auth-server spring-6-reactive:0.0.1-SNAPSHOT
 
docker stop reactive
docker rm reactive
docker start reactive
docker logs reactive
```

### Recommended Versions
| Recommended             | Reference                                                                                                                                                     | Notes                                                                                                                                                                                                                  |
|-------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Oracle Java 21 JDK      | [Download](https://www.oracle.com/java/technologies/downloads/#java21) | Java 17 or higher is required for Spring Framework 6. Java 21 is recommended for the course.                                                                                                                           |
| IntelliJ 2024 or Higher | [Download](https://www.jetbrains.com/idea/download/)                                                                                                          | Ultimate Edition recommended. Students can get a free 120 trial license [here](https://github.com/springframeworkguru/spring5webapp/wiki/Which-IDE-to-Use%3F#how-do-i-get-the-free-120-day-trial-to-intellij-ultimate) |
| Maven 3.9.6 or higher   | [Download](https://maven.apache.org/download.cgi)                                                                                                             | [Installation Instructions](https://maven.apache.org/install.html)                                                                                                                                                     |
| Gradle 8.7 or higher    | [Download](https://gradle.org/install/)                                                                                                                       |                                                                                                                                                                     |
| Git 2.39 or higher      | [Download](https://git-scm.com/downloads)                                                                                                                     |                                                                                                                                                                                                                        | 
| Git GUI Clients         | [Downloads](https://git-scm.com/downloads/guis)                                                                                                               | Not required. But can be helpful if new to Git. SourceTree is a good option for Mac and Windows users.                                                                                                                 |

## All Spring Framework Guru Courses
### Spring Framework 6
* [Spring Framework 6 - Beginner to Guru](https://www.udemy.com/course/spring-framework-6-beginner-to-guru/?referralCode=2BD0B7B7B6B511D699A9)
* [Spring AI: Beginner to Guru](https://www.udemy.com/course/spring-ai-beginner-to-guru/?referralCode=EF8DB31C723FFC8E2751)
* [Hibernate and Spring Data JPA: Beginner to Guru](https://www.udemy.com/course/hibernate-and-spring-data-jpa-beginner-to-guru/?referralCode=251C4C865302C7B1BB8F)
* [API First Engineering with Spring Boot](https://www.udemy.com/course/api-first-engineering-with-spring-boot/?referralCode=C6DAEE7338215A2CF276)
* [Introduction to Kafka with Spring Boot](https://www.udemy.com/course/introduction-to-kafka-with-spring-boot/?referralCode=15118530CA63AD1AF16D)
* [Spring Security: Beginner to Guru](https://www.udemy.com/course/spring-security-core-beginner-to-guru/?referralCode=306F288EB78688C0F3BC)
