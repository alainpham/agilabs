# Spring Boot with camel and other useful things amqp-receiver 

## To build this project use

```
mvn install
```

## To run this project with Maven use

```
mvn spring-boot:run
```

## To deploy directly on openshift

```
mvn -P ocp fabric8:deploy
```

## For testing

```
curl http://localhost:8090/camel/api-docs
curl http://localhost:8090/camel/ping
```


## Acces Swagger UI with definition

```
http://localhost:8090/webjars/swagger-ui/index.html?url=/camel/api-docs
```

## Call the ping rest operation
```
curl http://localhost:8090/camel/restsvc/ping
```

## Run local container with specific network and IP address


```
docker stop amqp-receiver
docker rm amqp-receiver
docker rmi amqp-receiver
docker build -t amqp-receiver .
docker run -d --net primenet --ip 172.18.0.10 --name amqp-receiver amqp-receiver
```

Stop or launch multple instaces

```
NB_CONTAINERS=2
for (( i=0; i<$NB_CONTAINERS; i++ ))
do
   docker stop amqp-receiver-$i
   docker rm amqp-receiver-$i
done

docker rmi amqp-receiver
docker build -t amqp-receiver .

for (( i=0; i<$NB_CONTAINERS; i++ ))
do
    docker run -d --net primenet --ip 172.18.0.1$i --name amqp-receiver-$i -e SPRING_PROFILES_ACTIVE=dev amqp-receiver
done
```

## To release without deploying straight to an ocp cluster

```
mvn  -P ocp package
```

## To deploy using binary build on ocp

```
tar xzvf amqp-receiver-ocp.tar.gz
cd amqp-receiver
oc apply -f openshift.yml
oc start-build amqp-receiver-s2i --from-dir=deploy --follow
```