# lab01-basic-service


This project shows you how to build a basic service using Fuse.

* Install archetypes

		git clone https://github.com/alainpham/archetypes.git
		cd archetypes
		mvn install
	
* Create a Fuse project
	
		mvn archetype:generate -DarchetypeGroupId=org.apache.camel -DarchetypeArtifactId=camel-archetype-spring-boot-fuse-7-bom -DarchetypeVersion=7
	
* Add this route to camelcontext

```
<route id="choice">
    <from  uri="servlet:choice"/>
    <choice>
        <when>
            <xpath>/msg = 'random'</xpath>
            <setBody >
                <simple>random(0,100)</simple>
            </setBody>
        </when>
        <otherwise>
            <setBody id="_setBody2">
                <constant>0</constant>
            </setBody>
        </otherwise>
    </choice>
    <log message="Hello ${body}"/>
</route>
```
* Run the service
	
		mvn spring-boot:run

* Test the service

```
curl http://localhost:8090/camel/ping
curl http://localhost:8090/camel/restsvc/ping
curl -X POST -d '<msg>random</msg>' -H "Content-Type: application/xml"  http://localhost:8090/camel/choice
```

* Access swagger

		http://localhost:8090/webjars/swagger-ui/3.24.3/index.html?url=/camel/api-docs

* Deploy on openshift

```
oc login -u <USER>
oc project <yourProject>
oc new-project fuse 
mvn -P ocp fabric8:deploy

curl -X POST -d '<msg>random</msg>' -H "Content-Type: application/xml"  http://lab01-basic-service-fuse.app.88.198.65.4.nip.io/camel/choice
```

This project shows you how to build a basic service using Fuse.

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
curl http://localhost:8090/camel/ping
```


## Acces Swagger UI with definition

```
http://localhost:8090/webjars/swagger-ui/3.24.3/index.html?url=/camel/api-docs
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