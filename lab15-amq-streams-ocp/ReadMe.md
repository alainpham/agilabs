# AMQ Streams (Strimzi + Kafka) on OCP workshop

This workshop aims at showing attendees how to do basic deploy/manage, secure, and monitor operations on AMQ Streams ( Strimzi & Kafka ).

- [AMQ Streams (Strimzi + Kafka) on OCP workshop](#AMQ-Streams-Strimzi--Kafka-on-OCP-workshop)
  - [Prerequisites](#Prerequisites)
  - [AMQ Streams components and basic concepts](#AMQ-Streams-components-and-basic-concepts)
    - [Custom Resource Definitions](#Custom-Resource-Definitions)
    - [Cluster Operator](#Cluster-Operator)
    - [Topic Operator](#Topic-Operator)
    - [User Operator](#User-Operator)
  - [Lab 01 - Install and deploy AMQ Streams Kafka Clusters](#Lab-01---Install-and-deploy-AMQ-Streams-Kafka-Clusters)
  - [Lab 02 -Secure Broker/Client communications](#Lab-02--Secure-BrokerClient-communications)
  - [Lab 03 -Resiliency with Mirror Maker](#Lab-03--Resiliency-with-Mirror-Maker)
  - [Lab 04 - Monitoring Kafka Clusters](#Lab-04---Monitoring-Kafka-Clusters)

## Prerequisites

- Openshift Container Platform 3.11
- oc client 3.11
- amq-streams-1.1.1-ocp-install-examples.zip from [access.redhat.com](https://access.redhat.com/jbossnetwork/restricted/softwareDownload.html?softwareId=68771)

## AMQ Streams components and basic concepts 

### Custom Resource Definitions

Custom resources definition (CRD) is a powerful feature introduced in Kubernetes 1.7 which enables users to add their own/custom objects to the Kubernetes cluster and use it like any other native Kubernetes objects.

For AMQ Streams we will be creating our own resources to maintain the topology and configurations of our clusters.

### Cluster Operator

Responsible for deploying and managing Apache Kafka clusters within OpenShift cluster. 

![Cluster Operator](https://access.redhat.com/webassets/avalon/d/Red_Hat_AMQ-7.3-Using_AMQ_Streams_on_OpenShift_Container_Platform-en-US/images/5b357ee35dba180b3aea27d166e4e08a/cluster_operator.png)

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
unzip amq-streams-1.1.0-ocp-install-examples.zip
```

We are going to install the cluster operator in the project named `amq-streams-userXX`. Set the environment variable to your name for the lab.

```
SUFFIX=userXX
AMQSTREAMSPROJECT=amq-streams-$SUFFIX
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
sed -i -E "0,/name:.*/s/(name:.*)/\1-$SUFFIX/" install/cluster-operator/*ClusterRoleBinding*.yaml
```


(Next step is optional : Alternative if images are already in the local registry and avoid repulling)
```
sed -i "s/registry.access.redhat.com\/amq7\//docker-registry.default.svc:5000\/openshift\//" install/cluster-operator/050-Deployment-strimzi-cluster-operator.yaml
```
(end of optional step)

Now install the cluster operator into your project. Next step creates all the CRDs and the RoleBindings for the cluster operator to function. Finally it will deploy the cluster operator into the project.

```
oc apply -f install/cluster-operator -n $AMQSTREAMSPROJECT
oc apply -f examples/templates/cluster-operator -n $AMQSTREAMSPROJECT
```

Deploy a cluster by creating a Kafka CRD. 
Review this file `examples/kafka/kafka-persistent.yaml`
Then run the following command to create it

```
oc apply -f examples/kafka/kafka-persistent.yaml
```

Run a consumer

```
oc run kafka-consumer -ti --image=docker-registry.default.svc:5000/openshift/amq-streams-kafka:1.1.0-kafka-2.1.1 --rm=true --restart=Never -- bin/kafka-console-consumer.sh --bootstrap-server my-cluster-kafka-bootstrap.$AMQSTREAMSPROJECT.svc:9092 --topic my-topic --from-beginning
```

Run a producer

```
oc run kafka-producer -ti --image=docker-registry.default.svc:5000/openshift/amq-streams-kafka:1.1.0-kafka-2.1.1 --rm=true --restart=Never -- bin/kafka-console-producer.sh --broker-list my-cluster-kafka-bootstrap.$AMQSTREAMSPROJECT.svc:9092 --topic my-topic
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



## Lab 02 -Secure Broker/Client communications



## Lab 03 -Resiliency with Mirror Maker



## Lab 04 - Monitoring Kafka Clusters

