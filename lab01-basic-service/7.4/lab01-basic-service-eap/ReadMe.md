Red Hat Fuse on EAP Spring XML Web Application : lab01-basic-service-eap
==============================================

This is a template Apache Camel Spring application for Red Hat Fuse on EAP.

This project is setup to allow you to create a Apache Camel Spring application, which can be deployed to an application
server running Red Hat Fuse on EAP. An example Spring XML Camel Route has been created for you, together with an Arquillian
integration test.


mvn clean package wildfly:deploy

Acces to swagger def

http://localhost:8080/lab01-basic-service-eap/webjars/swagger-ui/3.23.5/index.html?url=/lab01-basic-service-eap/camel/api-docs

For testing

    curl http://localhost:8080/lab01-basic-service-eap/camel/api-docs
    curl http://localhost:8080/lab01-basic-service-eap/camel/ping

Prerequisites
=============

* Minimum of Java 1.8
* Maven 3.3 or greater
* JBoss EAP 7.1 application server.


Getting started
---------------

1. Install the Red Hat Fuse for EAP distribution on your application server

2. Configure a $JBOSS_HOME environment variable to point at your application server installation directory

3. Start the application server from the command line

For Linux:

    $JBOSS_HOME/bin/standalone.sh

For Windows:

    %JBOSS_HOME%\bin\standalone.bat


Building the application
------------------------

To build the application do:

    mvn clean install


Run Arquillian Tests
--------------------

By default, tests are configured to be skipped as Arquillian requires the use of a container.

If you already have a running application server, you can run integration tests with:

    mvn clean test -Parq-remote

Otherwise you can get Arquillian to start and stop the server for you (Note: you must have $JBOSS_HOME configured beforehand):

    mvn clean test -Parq-managed


Deploying the application
-------------------------

To deploy the application to a running application server do:

    mvn clean package wildfly:deploy

The server console should display lines like the following:

    (MSC service thread 1-16) Apache Camel (CamelContext: spring-context) is starting
    (MSC service thread 1-16) Camel context starting: spring-context
    (MSC service thread 1-6) Bound camel naming object: java:jboss/camel/context/spring-context
    (MSC service thread 1-16) Route: route4 started and consuming from: Endpoint[direct://start]
    (MSC service thread 1-16) Total 1 routes, of which 1 is started


Access the application
----------------------

The application will be available at http://localhost:8080/your-context-root?name=Kermit


Undeploying the application
---------------------------

    mvn wildfly:undeploy
