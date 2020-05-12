# AMQ Streams (Strimzi + Kafka) on OCP workshop

This workshop aims at showing attendees how to do basic deploy/manage, secure, and monitor operations on AMQ Streams ( Strimzi & Kafka ).

- [AMQ Streams (Strimzi + Kafka) on OCP workshop](#amq-streams-strimzi--kafka-on-ocp-workshop)
  - [Prerequisites](#prerequisites)
  - [AMQ Streams components and basic concepts](#amq-streams-components-and-basic-concepts)
    - [Custom Resource Definitions](#custom-resource-definitions)
    - [Cluster Operator](#cluster-operator)
    - [Topic Operator](#topic-operator)
    - [User Operator](#user-operator)
  - [Lab 01 - Install and deploy AMQ Streams Kafka Clusters](#lab-01---install-and-deploy-amq-streams-kafka-clusters)
    - [Cluster Management](#cluster-management)
    - [Testing environment](#testing-environment)
    - [Topic Management](#topic-management)
  - [Lab 02 -Secure Broker/Client communications](#lab-02--secure-brokerclient-communications)
    - [Access broker from outside the cluster](#access-broker-from-outside-the-cluster)
    - [Activate authentication and authorization on the broker](#activate-authentication-and-authorization-on-the-broker)
    - [Kafkdrop](#kafkdrop)
  - [Lab 03 -Resiliency with Mirror Maker](#lab-03--resiliency-with-mirror-maker)
  - [Lab 04 - Monitoring Kafka Clusters](#lab-04---monitoring-kafka-clusters)
    - [Exposing kafka metrics](#exposing-kafka-metrics)
    - [Deploy prometheus](#deploy-prometheus)
    - [Deploy grafana](#deploy-grafana)
    - [Configure dashboard](#configure-dashboard)
  - [Deleting stuff (Instructor only)](#deleting-stuff-instructor-only)
- [End to end demo](#end-to-end-demo)
- [End to end demo OCP 4.3](#end-to-end-demo-ocp-43)

## Prerequisites

- Openshift Container Platform 3.11
- oc client 3.11
- amq-streams-1.3.0-ocp-install-examples.zip from [access.redhat.com](https://access.redhat.com/jbossnetwork/restricted/softwareDownload.html?softwareId=74481)

## AMQ Streams components and basic concepts 

### Custom Resource Definitions

Custom resources definition (CRD) is a powerful feature introduced in Kubernetes 1.7 which enables users to add their own/custom objects to the Kubernetes cluster and use it like any other native Kubernetes objects.

For AMQ Streams we will be creating our own resources to maintain the topology and configurations of our clusters.

### Cluster Operator

Responsible for deploying and managing Apache Kafka clusters within OpenShift cluster. 

![Cluster Operator](https://access.redhat.com/webassets/avalon/d/Red_Hat_AMQ-7.3-Using_AMQ_Streams_on_OpenShift_Container_Platform-en-US/images/a48fd4be1526fc37853a46ddfdaf9daa/cluster-operator.png)

The Cluster Operator can be configured to watch for more OpenShift projects or Kubernetes namespaces. Cluster Operator watches the following resources that are defined by Custom Resource Definitions (CRDs) :

- A Kafka resource for the Kafka cluster.
- A KafkaConnect resource for the Kafka Connect cluster.
- A KafkaConnectS2I resource for the Kafka Connect cluster with Source2Image support.
- A KafkaMirrorMaker resource for the Kafka Mirror Maker instance.

### Topic Operator

Responsible for managing Kafka topics within a Kafka cluster running within OpenShift cluster. 

![Topic Operator](https://access.redhat.com/webassets/avalon/d/Red_Hat_AMQ-7.3-Using_AMQ_Streams_on_OpenShift_Container_Platform-en-US/images/58c0e59c4f691d4e5f50f9a23417c916/topic_operator.png)

 The role of the Topic Operator is to keep a set of KafkaTopic OpenShift resources describing Kafka topics in-sync with corresponding Kafka topics.

Specifically:

- if a KafkaTopic is created, the operator will create the topic it describes
- if a KafkaTopic is deleted, the operator will delete the topic it describes
- if a KafkaTopic is changed, the operator will update the topic it describes 

And also, in the other direction:

- if a topic is created within the Kafka cluster, the operator will create a KafkaTopic describing it
- if a topic is deleted from the Kafka cluster, the operator will delete the KafkaTopic describing it
- if a topic in the Kafka cluster is changed, the operator will update the KafkaTopic describing it 

### User Operator

Responsible for managing Kafka users within a Kafka cluster running within OpenShift cluster. 

- if a KafkaUser is created, the User Operator will create the user it describes
- if a KafkaUser is deleted, the User Operator will delete the user it describes
- if a KafkaUser is changed, the User Operator will update the user it describes 

Unlike the Topic Operator, the User Operator does not sync any changes from the Kafka cluster with the OpenShift resources. Unlike the Kafka topics which might be created by applications directly in Kafka, it is not expected that the users will be managed directly in the Kafka cluster in parallel with the User Operator, so this should not be needed.

The User Operator allows you to declare a KafkaUser as part of your application’s deployment. When the user is created, the credentials will be created in a Secret. Your application needs to use the user and its credentials for authentication and to produce or consume messages.

In addition to managing credentials for authentication, the User Operator also manages authorization rules by including a description of the user’s rights in the KafkaUser declaration. 

## Lab 01 - Install and deploy AMQ Streams Kafka Clusters

Login to Openshift Cluster with oc client and make sure you are cluster-admin. 

```
oc login https://OCP-MASTER:8443 --token=XXXXXX
oc get clusterrolebindings | grep cluster-admin.*`oc whoami`
```

Expected result 

```
cluster-admin-0    /cluster-admin    YOUR_USER
```          

Unzip the downloaded script package 

```
unzip amq-streams-1.3.0-ocp-install-examples.zip
```
### Cluster Management

We are going to install the cluster operator in the project named `amq-streams-userXX`. Set the environment variable to your name for the lab.

```
SUFFIX=userXX
AMQSTREAMSPROJECT=amq-streams-$SUFFIX

echo $AMQSTREAMSPROJECT
```

Create a new project in Openshift.

```
oc new-project $AMQSTREAMSPROJECT
```

We are now replacing with the right values in the install scripts to assign the appropriate role bindings to the accounts in the project we are deploying the cluster operator in.

```
sed -i "s/namespace: .*/namespace: $AMQSTREAMSPROJECT/" install/cluster-operator/*RoleBinding*.yaml
```

We are now replacing ClusterRoleBinding names as we want eventually to deploy several cluster operators within a single OCP instance.

```
sed -i -E "0,/name:.*/s/(name: strimzi-cluster-operator)(.*)/\1-$SUFFIX/" install/cluster-operator/021-ClusterRoleBinding*.yaml

sed -i -E "0,/name:.*/s/(name: strimzi-cluster-operator-kafka-broker-delegation)(.*)/\1-$SUFFIX/" install/cluster-operator/030-ClusterRoleBinding*.yaml

```

(Next step is optional : Alternative if images are already in the local registry and avoid repulling)
```
sed -i "s/registry.redhat.io\/amq7\//docker-registry.default.svc:5000\/openshift\//" install/cluster-operator/050-Deployment-strimzi-cluster-operator.yaml

sed -i "s/registry.access.redhat.com\/amq7\//docker-registry.default.svc:5000\/openshift\//" install/cluster-operator/050-Deployment-strimzi-cluster-operator.yaml
```
(end of optional step)

Now install the cluster operator into your project. Next step creates all the CRDs and the RoleBindings for the cluster operator to function. Finally it will deploy the cluster operator into the project.

```
oc apply -f install/cluster-operator -n $AMQSTREAMSPROJECT
oc apply -f examples/templates/cluster-operator -n $AMQSTREAMSPROJECT
oc apply -f examples/templates/topic-operator  -n $AMQSTREAMSPROJECT
```

Deploy a cluster by creating a Kafka CRD. 
Review this file `examples/kafka/kafka-persistent.yaml`
Then run the following command to create it

```
oc apply -f kafka-persistent.yaml
```
Observe the create of different components in this order:
- Zookeeper cluster
- Kafka cluster
- Entity Operator to manage topics and users

### Testing environment

To test everything run a consumer:

```
oc run kafka-consumer -ti --image=registry.redhat.io/amq7/amq-streams-kafka-23:1.3.0 --rm=true --restart=Never -- bin/kafka-console-consumer.sh --bootstrap-server my-cluster-kafka-bootstrap:9092 --topic my-topic --from-beginning
```

Alternatively from the local registry

```
oc run kafka-consumer -ti --image=docker-registry.default.svc:5000/openshift/amq-streams-kafka-23:1.3.0 --rm=true --restart=Never -- bin/kafka-console-consumer.sh --bootstrap-server my-cluster-kafka-bootstrap:9092 --topic my-topic --from-beginning
```

Run a producer

```
oc run kafka-producer -ti --image=registry.redhat.io/amq7/amq-streams-kafka-23:1.3.0 --rm=true --restart=Never -- bin/kafka-console-producer.sh --broker-list my-cluster-kafka-bootstrap:9092 --topic my-topic
```

Alternatively from the local registry

```
oc run kafka-producer -ti --image=docker-registry.default.svc:5000/openshift/amq-streams-kafka-23:1.3.0 --rm=true --restart=Never -- bin/kafka-console-producer.sh --broker-list my-cluster-kafka-bootstrap:9092 --topic my-topic
```

Now let's send some ascii Art :)

```
 /\     /\
{  `---'  }
{  O   O  }
~~>  V  <~~
 \  \|/  /
  `-----'____
  /     \    \_
 {       }\  )_\_   _
 |  \_/  |/ /  \_\_/ )
  \__/  /(_/     \__/
    (__/
```

Play around with increasing and decreasing number of instances.

### Topic Management

Create a topic using the template and enter the following values. Check in Resources->Other Resources-> Kafka Topic. To see the new definition

```
Name of the Kafka cluster
my-cluster

Name of the topic
lines

Number of partitions
2

Number of replicas
2

Topic config
{ "retention.ms":"7200000" ,"segment.bytes": "1073741824"}
```

(Alternatively per command line)

```
oc apply -f lines-topic.yml
```
(end of alternative)

Check if the topic config.

```
oc rsh my-cluster-kafka-0
bin/kafka-topics.sh --zookeeper localhost:2181 --topic lines --describe
exit
```

Expected result

```
Topic:lines     PartitionCount:2        ReplicationFactor:2     Configs:segment.bytes=1073741824,message.format.version=2.1-IV2,retention.ms=7200000
        Topic: lines    Partition: 0    Leader: 0       Replicas: 0,1   Isr: 0,1
        Topic: lines    Partition: 1    Leader: 1       Replicas: 1,2   Isr: 1,2
```

Let's increase the number of partitions to 10 
```
oc apply -f lines-topic-scaled.yml
```

```
oc rsh my-cluster-kafka-0
bin/kafka-topics.sh --zookeeper localhost:2181 --topic lines --describe
exit
```

Expected results

```
Topic:lines     PartitionCount:10       ReplicationFactor:2     Configs:segment.bytes=1073741824,message.format.version=2.1-IV2,retention.ms=7200000
        Topic: lines    Partition: 0    Leader: 0       Replicas: 0,1   Isr: 0,1
        Topic: lines    Partition: 1    Leader: 1       Replicas: 1,2   Isr: 1,2
        Topic: lines    Partition: 2    Leader: 2       Replicas: 2,0   Isr: 2,0
        Topic: lines    Partition: 3    Leader: 0       Replicas: 0,2   Isr: 0,2
        Topic: lines    Partition: 4    Leader: 1       Replicas: 1,0   Isr: 1,0
        Topic: lines    Partition: 5    Leader: 2       Replicas: 2,1   Isr: 2,1
        Topic: lines    Partition: 6    Leader: 0       Replicas: 0,1   Isr: 0,1
        Topic: lines    Partition: 7    Leader: 1       Replicas: 1,2   Isr: 1,2
        Topic: lines    Partition: 8    Leader: 2       Replicas: 2,0   Isr: 2,0
        Topic: lines    Partition: 9    Leader: 0       Replicas: 0,2   Isr: 0,2
```

## Lab 02 -Secure Broker/Client communications

### Access broker from outside the cluster

To access the broker from the outside you need to setup a secure tls communication between the client and the broker.

Add external route to the my-cluster Kafka Resource

```
    listeners:
      external:
        type: route
      plain: {}
      tls: {}
```

Or do it by command

```
oc apply -f kafka-persistent-external.yaml
```

Watch the modifications roll out.

Extract secrets and import into a trust store to trust the broker public key.

```
oc extract secret/my-cluster-cluster-ca-cert --keys=ca.crt --to=- >execs/producer/config/certificate.crt
oc extract secret/my-cluster-cluster-ca-cert --keys=ca.crt --to=- >execs/consumer/config/certificate.crt
oc extract secret/my-cluster-cluster-ca-cert --keys=ca.crt --to=- >exec-camel/producer/certificate.crt
oc extract secret/my-cluster-cluster-ca-cert --keys=ca.crt --to=- >exec-camel/consumer/certificate.crt

rm execs/consumer/config/trust.p12
rm execs/producer/config/trust.p12
rm exec-camel/consumer/trust.p12
rm exec-camel/producer/trust.p12
keytool -importcert -keystore execs/producer/config/trust.p12 -storetype PKCS12 -alias root -storepass password -file execs/producer/config/certificate.crt -noprompt
keytool -importcert -keystore execs/consumer/config/trust.p12 -storetype PKCS12 -alias root -storepass password -file execs/consumer/config/certificate.crt -noprompt

keytool -importcert -keystore exec-camel/producer/trust.p12 -storetype PKCS12 -alias root -storepass password -file exec-camel/producer/certificate.crt -noprompt
keytool -importcert -keystore exec-camel/consumer/trust.p12 -storetype PKCS12 -alias root -storepass password -file exec-camel/consumer/certificate.crt -noprompt
```

Get the route of your clustervi

```
oc get route my-cluster-kafka-bootstrap -o 'jsonpath={.spec.host}'
```

Configure it in the file `execs/producer/config/application.properties`

```
vi execs/producer/config/application.properties
vi execs/consumer/config/application.properties


mp.messaging.incoming.events.bootstrap.servers=<YOUR_ROUTE>:443
mp.messaging.incoming.events.security.protocol=SSL
```

Run the consumer. FYI, this an app built with Quarkus

```
cd execs/consumer ; java -jar quarkus-kafka-consumer-1.0-SNAPSHOT-runner.jar ; cd -
```

Run a producer to test it

```
oc run kafka-producer -ti --image=registry.redhat.io/amq7/amq-streams-kafka-23:1.3.0 --rm=true --restart=Never -- bin/kafka-console-producer.sh --broker-list my-cluster-kafka-bootstrap:9092 --topic my-topic
```
### Activate authentication and authorization on the broker

Edit the Kafka Resource of my-cluster

```
    listeners:
      external:
        type: route
        authentication:
          type: scram-sha-512        
      plain: {}
      tls: 
        authentication:
          type: scram-sha-512  
```

or run the following command line

```
oc apply -f kafka-persistent-secure.yaml
```

Create related ACLs for readers and writers

```
oc apply -f secure-reader.yml
oc apply -f secure-writer.yml
```

Create a secure consumer. Check all the configs necessary in the yaml file.

```
oc apply -f quarkus-kafka-consumer-secure.yml
```


Try Running you client from outside of Openshift and see that it's not working anymore

```
cd execs ; java -jar quarkus-kafka-consumer-1.0-SNAPSHOT-runner.jar ; cd -
```

Get the password from the secret

```
oc get secret secure-topic-reader -o yaml | grep password | sed -E 's/.*password: (.*)/\1/' | base64 -d
oc get secret secure-topic-writer -o yaml | grep password | sed -E 's/.*password: (.*)/\1/' | base64 -d

```

Now change it config

```
vi execs/config/application.properties


mp.messaging.incoming.events.bootstrap.servers=<YOUR_ROUTE>:443
mp.messaging.incoming.events.security.protocol=SASL_SSL
mp.messaging.incoming.events.sasl.jaas.config=org.apache.kafka.common.security.scram.ScramLoginModule required username="secure-topic-reader" password="XXX";
```

Rerun the app

```
cd execs ; java -jar quarkus-kafka-consumer-1.0-SNAPSHOT-runner.jar ; cd -
```

Run a producer to test it

```
oc run kafka-producer -ti --image=registry.redhat.io/amq7/amq-streams-kafka-23:1.3.0 --rm=true --restart=Never -- bin/kafka-console-producer.sh --broker-list my-cluster-kafka-bootstrap:9092 --topic my-topic
```

### Kafkdrop

```
oc new-app obsidiandynamics/kafdrop -e "KAFKA_BROKERCONNECT=my-cluster-kafka-bootstrap:9092" -e SERVER_SERVLET_CONTEXTPATH="/" -e JVM_OPTS="-Xms32M -Xmx512M"
oc expose dc/kafdrop --port=9000
oc expose svc kafdrop
```

## Lab 03 -Resiliency with Mirror Maker

Browse templates and create a mirror maker from one user to another

Use full service dns names
i.e. :
```
my-cluster-kafka-bootstrap.amq-streams-userXX.svc.cluster.local
```

Run a producer to test it out

```
oc run kafka-producer -ti --image=registry.redhat.io/amq7/amq-streams-kafka-23:1.3.0 --rm=true --restart=Never -- bin/kafka-console-producer.sh --broker-list my-cluster-kafka-bootstrap:9092 --topic my-topic
```

## Lab 04 - Monitoring Kafka Clusters

### Exposing kafka metrics
Expose metrics on the cluster

```
oc apply -f kafka-persistent-metrics.yaml
```

### Deploy prometheus

```
sed -E "s/userXX/$SUFFIX/" prometheus.yaml | oc apply -f -
oc expose svc prometheus
```

### Deploy grafana

```
oc apply -f grafana.yaml
oc expose svc grafana
```

### Configure dashboard

Login to grafana with admin/admin. Get the URL with the following command

```
oc get route grafana -o 'jsonpath={.spec.host}'
```

Create the Prometheus datasource. Go to the `Gear' icon on the left and then pick Datasources.

Choose Prometheus as data source type and then fill following values:

```
name: prometheus
url: http://prometheus:9090
```
Save and test

Now import the following dashboard for Kafka

```
https://raw.githubusercontent.com/strimzi/strimzi-kafka-operator/0.12.1/metrics/examples/grafana/strimzi-kafka.json
```

And similarly for Zookeeper

```
https://raw.githubusercontent.com/strimzi/strimzi-kafka-operator/0.12.1/metrics/examples/grafana/strimzi-zookeeper.json
```

## Deleting stuff (Instructor only)

```

for i in {1..2}
do

SUFFIX=user$i
AMQSTREAMSPROJECT=amq-streams-$SUFFIX

oc delete kafka my-cluster -n amq-streams-user0$i -n $AMQSTREAMSPROJECT
oc delete clusterrolebinding strimzi-cluster-operator-$SUFFIX -n $AMQSTREAMSPROJECT
oc delete clusterrolebinding strimzi-cluster-operator-kafka-broker-delegation-$SUFFIX -n $AMQSTREAMSPROJECT
oc delete project $AMQSTREAMSPROJECT
done

echo "deleting ClusterRoles"
oc delete -f install/cluster-operator/*ClusterRole-*

oc delete clusterrole strimzi-cluster-operator-global strimzi-cluster-operator-namespaced strimzi-entity-operator strimzi-kafka-broker strimzi-topic-operator

echo "deleting CRDs"
oc delete -f install/cluster-operator/*Crd*

oc delete crd  kafkabridges.kafka.strimzi.io kafkaconnects.kafka.strimzi.io kafkaconnects2is.kafka.strimzi.io kafkamirrormakers.kafka.strimzi.io kafkas.kafka.strimzi.io kafkatopics.kafka.strimzi.io kafkausers.kafka.strimzi.io

```


# End to end demo

Create project
```
SUFFIX=me
AMQSTREAMSPROJECT=strimzi-kafka

oc new-project $AMQSTREAMSPROJECT
```

Change config files to create CRDS & Role Bindings

```
sed -i "s/namespace: .*/namespace: $AMQSTREAMSPROJECT/" install/cluster-operator/*RoleBinding*.yaml

sed -i "s/registry.access.redhat.com\/amq7\//docker-registry.default.svc:5000\/openshift\//" install/cluster-operator/050-Deployment-strimzi-cluster-operator.yaml
```

Create the cluster operator

```
oc apply -f install/cluster-operator -n $AMQSTREAMSPROJECT
```

Create app templates for Graphical Deployments
```
oc apply -f examples/templates/cluster-operator -n $AMQSTREAMSPROJECT
oc apply -f examples/templates/topic-operator  -n $AMQSTREAMSPROJECT
```

Create a cluster

```
oc apply -f kafka-persistent.yaml

```

Test

```
oc run kafka-consumer -ti --image=docker-registry.default.svc:5000/openshift/amq-streams-kafka:1.1.0-kafka-2.1.1 --rm=true --restart=Never -- bin/kafka-console-consumer.sh --bootstrap-server my-cluster-kafka-bootstrap:9092 --topic my-topic --from-beginning


oc run kafka-producer -ti --image=docker-registry.default.svc:5000/openshift/amq-streams-kafka:1.1.0-kafka-2.1.1 --rm=true --restart=Never -- bin/kafka-console-producer.sh --broker-list my-cluster-kafka-bootstrap:9092 --topic my-topic

```

# End to end demo OCP 4.3

1. Install AMQ Streams operator
2. `oc new-project amq-streams`
3. `oc apply -f kafka-persistent-metrics.yaml`
4. `oc apply -f secure-reader.yml`
5. `oc apply -f secure-writer.yml`
6. Run clients

```
oc extract secret/my-cluster-cluster-ca-cert --keys=ca.crt --to=- >exec-camel/producer/certificate.crt

oc extract secret/my-cluster-cluster-ca-cert --keys=ca.crt --to=- >exec-camel/consumer/certificate.crt

rm exec-camel/consumer/trust.p12
rm exec-camel/producer/trust.p12

keytool -importcert -keystore exec-camel/producer/trust.p12 -storetype PKCS12 -alias root -storepass password -file exec-camel/producer/certificate.crt -noprompt

keytool -importcert -keystore exec-camel/consumer/trust.p12 -storetype PKCS12 -alias root -storepass password -file exec-camel/consumer/certificate.crt -noprompt

```

Get route

```
oc get route my-cluster-kafka-bootstrap -o 'jsonpath={.spec.host}'
```

Get the password from the secrets

```
oc get secret secure-topic-reader -o yaml | grep password | sed -E 's/.*password: (.*)/\1/' | base64 -d
oc get secret secure-topic-writer -o yaml | grep password | sed -E 's/.*password: (.*)/\1/' | base64 -d

```

7. Launch Consumer and producer


8. Install Kafdrop

```
oc new-app obsidiandynamics/kafdrop -e "KAFKA_BROKERCONNECT=my-cluster-kafka-bootstrap:9092" -e SERVER_SERVLET_CONTEXTPATH="/" -e JVM_OPTS="-Xms32M -Xmx512M"
oc expose dc/kafdrop --port=9000
oc expose svc kafdrop
```

Test scaling lines topic

9. Install monitoring

```
oc apply -f 11-prometheus.yaml
oc apply -f 12-grafana.yaml
oc expose svc grafana
```

10. Create connection Import dashboards