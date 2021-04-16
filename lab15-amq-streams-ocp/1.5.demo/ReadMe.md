 
# AMQ Streams demo

- [AMQ Streams demo](#amq-streams-demo)
  - [AMQ Streams components and basic concepts](#amq-streams-components-and-basic-concepts)
    - [Custom Resource Definitions](#custom-resource-definitions)
    - [Cluster Operator](#cluster-operator)
    - [Topic Operator](#topic-operator)
    - [User Operator](#user-operator)
  - [End to end demo OCP 4.x](#end-to-end-demo-ocp-4x)
    - [Install AMQ Streams operator through operator hub.](#install-amq-streams-operator-through-operator-hub)
    - [Create new project](#create-new-project)
    - [Create a persistent 3 broker, 3 zookeeper cluster](#create-a-persistent-3-broker-3-zookeeper-cluster)
    - [Create external routes for externale clients](#create-external-routes-for-externale-clients)
    - [Configure clients](#configure-clients)
    - [Change cluster to have metrics](#change-cluster-to-have-metrics)
  - [Troubleshooting OCP](#troubleshooting-ocp)
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

## End to end demo OCP 4.x


### Install AMQ Streams operator through operator hub.

Go to operator hub and install AMQ streams operator on all namespaces

### Create new project

```
oc new-project amq-streams
```

### Create a persistent 3 broker, 3 zookeeper cluster

```
oc apply -f 01-kafka-persistent.yaml
```

Review provisioned components.

Additionnaly deploy kafdrop

```
oc new-app obsidiandynamics/kafdrop -e "KAFKA_BROKERCONNECT=my-cluster-kafka-bootstrap:9092" -e SERVER_SERVLET_CONTEXTPATH="/" -e JVM_OPTS="-Xms32M -Xmx512M"
oc expose dc/kafdrop --port=9000
oc expose svc kafdrop
```

Create topics and change topics partitions

```
oc apply -f 01-01-lines-topic.yml
oc apply -f 01-02-lines-topic-scaled.yml
```

### Create external routes for externale clients

```
oc apply -f 02-kafka-persistent-external.yaml
```
Wait for pods to recreate

Create readers and subscribers

```
oc apply -f secure-reader.yml
oc apply -f secure-writer.yml
```

### Configure clients


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

Get passwords

```
oc get secret secure-topic-reader -o yaml | grep password | head -1 |  sed -E 's/.*password: (.*)/\1/'  | base64 -d
oc get secret secure-topic-writer -o yaml | grep password | head -1 |  sed -E 's/.*password: (.*)/\1/'  | base64 -d
```
### Change cluster to have metrics


```
oc apply -f 03-kafka-persistent-metrics.yaml
```

Adding prometheus & grafana

```
cat monitoring/11-prometheus-local.yaml | sed -E "s/TARGET_NAMESPACE/amq-streams/"| oc apply -f -
oc create configmap grafana-dashboards --from-file=monitoring/dashboards
oc apply -f monitoring/12-grafana.yaml


## Troubleshooting OCP

oc adm policy add-scc-to-user privileged -z default -n amq-streams