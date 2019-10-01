# lab02-service-composition
This project shows you how to build a basic service using Fuse.

* Install archetypes

```
git clone https://github.com/alainpham/archetypes.git
cd archetypes
mvn install
```

* Create a Fuse project
	
```
mvn archetype:generate -DarchetypeGroupId=org.apache.camel -DarchetypeArtifactId=camel-archetype-spring-boot-fuse-7-bom -DarchetypeVersion=7.1.0
```
 
* Create swagger definition on Apicurio
	
```
https://www.apicur.io/
```


* Import swagger definition for the target service and from the source service
	
```
mvn -P swaggergen generate-sources
```

## Implement the service composition

* add h2 depencency

```
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <version>1.4.192</version>
</dependency>
```

* add this config to property files

```
#Database configuration
spring.datasource.url = jdbc:h2:mem:mydb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.username = sa
spring.datasource.password = 
spring.datasource.driver-class-name = org.h2.Driver
spring.datasource.platform = h2
```

* Test service

```
curl -X GET --header "Accept: application/json" "http://localhost:8090/camel/customer?id=wsebrook0%40privacy.gov.au"

curl -X GET --header "Accept: application/json" "http://lab02-service-composition-fuse.app.88.198.65.4.nip.io/camel/customer?id=wsebrook0%40privacy.gov.au"

curl -X GET --header "Accept: application/json" "http://lab02-service-composition-apps.apps.88.198.65.4.nip.io/camel/customer?id=wsebrook0%40privacy.gov.au"

curl -X GET --header "Accept: application/json" "https://api-3scale-apicast-staging.amp.88.198.65.4.nip.io:443/camel/customer?id=wsebrook0%40privacy.gov.au" -k -H'user-key: 34c71375bf490ab8ad06519fe951edfe' 


```

* Add circuit breaking & throttling to protect some backends.

* Test with Gatling and observe the behaviors with cirbuitbreaker and without

* Deploy Hystrix dashboard on OCP

```
oc create -f http://repo1.maven.org/maven2/io/fabric8/kubeflix/hystrix-dashboard/1.0.28/hystrix-dashboard-1.0.28-openshift.yml

oc expose service hystrix-dashboard --port=8080 
```

