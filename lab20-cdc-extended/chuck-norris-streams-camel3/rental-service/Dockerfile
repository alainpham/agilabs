# FROM registry.access.redhat.com/ubi8/ubi-minimal:latest
# RUN microdnf install java-1.8.0-openjdk-headless && microdnf clean all
# COPY target/*.jar app.jar
# ENTRYPOINT ["java","-jar","/app.jar"]
# EXPOSE 8080

FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
EXPOSE 8080