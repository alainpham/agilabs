Test it

curl http://lab02-service-composition-mock-fuse-fuse.app.88.198.65.4.nip.io/camel/customer/wsebrook0%40privacy.gov.au



Camel Spring Boot Project lab02-service-composition-mock-fuse
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