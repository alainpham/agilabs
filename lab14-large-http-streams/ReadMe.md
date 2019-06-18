Camel Spring Boot Project lab14-large-http-streams
===========================

To build this project use

    mvn install

To run this project with Maven use

    mvn spring-boot:run

For more help see the Apache Camel documentation

    http://camel.apache.org/


For testing
    curl http://localhost:8090/camel/api-docs
    curl http://localhost:8090/camel/ping

Acces Swagger UI with definition
    http://localhost:8090/webjars/swagger-ui/2.1.0/index.html?url=/camel/api-docs

    curl http://localhost:8090/camel/restsvc/ping

To deploy on Openshift, make sure you are connected to your Openshift instance and project with the oc command.

    mvn -P ocp fabric8:deploy

To launch a raw big file

	curl --request PUT  --data-binary @PATH_TO_YOUR_BIG_FILE http://localhost:8123  -v


You can test big CSV files with streaming split tokenizer and Bindy parsing
	1/ Generate a 700MB File : launch class GenerateCSVData
	2/ curl --request PUT  --data-binary @700MB.csv http://localhost:8090/camel/csv  -v
	
You can test big XML files with streaming split xtokenizer
	1/ Generate a 700MB File : launch class GenerateXMLData
	curl --request PUT  --data-binary @700MB.xml http://localhost:8090/camel/xml  -v
    curl --request POST -H "Content-Type: application/octet-stream"  --data-binary @700MB.xml http://localhost:8090/camel/xml  -v

Or if you prefer post data

	curl --request POST -H "Content-Type: application/octet-stream"  --data-binary @PATH_TO_YOUR_BIG_FILE http://localhost:8123  -v