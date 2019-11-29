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
<route id="choice-svc">
    <from id="choice-svc-starter" uri="servlet:choice"/>
    <choice id="choice-on-input">
        <when id="if-random">
            <xpath>/msg ='random'</xpath>
            <bean id="call-random-bean" ref="myTransformer"/>
        </when>
        <otherwise id="choice-on-input-otherwise">
            <setBody id="choice-on-input-set-otherwise">
                <constant>0</constant>
            </setBody>
        </otherwise>
    </choice>
    <log id="log-choice-svc-content" message="${body}"/>
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

		http://localhost:8090/webjars/swagger-ui/2.1.0/index.html?url=/camel/api-docs

* Deploy on openshift

```
oc login -u <USER>
oc project <yourProject>
oc new-project fuse 
mvn -P ocp fabric8:deploy

curl -X POST -d '<msg>random</msg>' -H "Content-Type: application/xml"  http://lab01-basic-service-fuse.app.88.198.65.4.nip.io/camel/choice
```
