# Test Salesforce auth

```
curl -X POST \
  https://test.salesforce.com/services/oauth2/token \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -d 'client_id=YOURCLIENTID&client_secret=YOURSECRET&grant_type=password&username=YOURUSER&password=YOURPASSWORDANDTOKEN'
```
# Generate salesfroce instance classes

```
mvn camel-salesforce:generate -DcamelSalesforce.clientId=YOURCLIENTID -DcamelSalesforce.clientSecret=YOURSECRET -DcamelSalesforce.userName=YOURUSER -DcamelSalesforce.password='YOURPASSWORDANDTOKEN'
```

# For testing

1. Download KAFKA
		
2. Start Zookeeper

		bin/zookeeper-server-start.sh config/zookeeper.properties
		
3. Start Kafka Broker

		bin/kafka-server-start.sh config/server.properties
	
4. Create a topic called test
		
		bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic account
	
5. Run the App
    	
    	mvn spring-boot:run

6. View rest operations with swagger ui    
    	
    	http://localhost:8090/webjars/swagger-ui/2.1.0/index.html?url=/camel/api-docs#/
    
    
7. Delete Topic and recreate to start from scratch
    	
		bin/kafka-topics.sh --zookeeper localhost:2181 --delete --topic test

# Create topic on amq streams

        oc project amq-streams
        oc apply -f kafkatopic.yml

