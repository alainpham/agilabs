# Spring Boot with camel and other useful things kafka-producer 

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
docker stop kafka-producer
docker rm kafka-producer
docker rmi kafka-producer
docker build -t kafka-producer .
docker run -d --net primenet --ip 172.18.0.10 --name kafka-producer kafka-producer
```

Stop or launch multple instaces

```
NB_CONTAINERS=2
for (( i=0; i<$NB_CONTAINERS; i++ ))
do
   docker stop kafka-producer-$i
   docker rm kafka-producer-$i
done

docker rmi kafka-producer
docker build -t kafka-producer .

for (( i=0; i<$NB_CONTAINERS; i++ ))
do
    docker run -d --net primenet --ip 172.18.0.1$i --name kafka-producer-$i -e SPRING_PROFILES_ACTIVE=dev kafka-producer
done
```

## To release without deploying straight to an ocp cluster

```
mvn  -P ocp package
```

## To deploy using binary build on ocp

```
tar xzvf kafka-producer-ocp.tar.gz
cd kafka-producer
oc apply -f openshift.yml
oc start-build kafka-producer-s2i --from-dir=deploy --follow
```