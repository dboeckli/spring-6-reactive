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
