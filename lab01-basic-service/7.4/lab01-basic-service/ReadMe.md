# Spring Boot with camel and other useful things lab01-basic-service 

## To build this project use

```
mvn install
```

## To run this project with Maven use

```
mvn spring-boot:run
```


## For testing

```
curl http://localhost:8090/camel/api-docs

curl -X POST -d '<msg>random</msg>' -H "Content-Type: application/xml"  http://lab01-basic-service-apps.apps.88.198.65.4.nip.io/camel/choice

```


## Acces Swagger UI with definition

```
http://localhost:8090/webjars/swagger-ui/3.23.5/index.html?url=/camel/api-docs
```

## Call the ping rest operation
```
curl http://localhost:8090/camel/restsvc/ping
```

## Run local container with specific network and IP address


```
docker build -t lab01-basic-service .
docker run -d --net primenet --ip 172.18.0.10 --name lab01-basic-service lab01-basic-service
```