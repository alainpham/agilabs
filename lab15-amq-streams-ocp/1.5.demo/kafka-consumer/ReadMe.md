# Spring Boot with camel and other useful things kafka-consumer 

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
docker stop kafka-consumer
docker rm kafka-consumer
docker rmi kafka-consumer
docker build -t kafka-consumer .
docker run -d --net primenet --ip 172.18.0.10 --name kafka-consumer kafka-consumer
```

Stop or launch multple instaces

```
NB_CONTAINERS=2
for (( i=0; i<$NB_CONTAINERS; i++ ))
do
   docker stop kafka-consumer-$i
   docker rm kafka-consumer-$i
done

docker rmi kafka-consumer
docker build -t kafka-consumer .

for (( i=0; i<$NB_CONTAINERS; i++ ))
do
    docker run -d --net primenet --ip 172.18.0.1$i --name kafka-consumer-$i -e SPRING_PROFILES_ACTIVE=dev kafka-consumer
done
```

## To release without deploying straight to an ocp cluster

```
mvn  -P ocp package
```

## To deploy using binary build on ocp

```
tar xzvf kafka-consumer-ocp.tar.gz
cd kafka-consumer
oc apply -f openshift.yml
oc start-build kafka-consumer-s2i --from-dir=deploy --follow
```