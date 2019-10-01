# AMQ Streams (Strimzi + Kafka) on OCP workshop

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
### Cluster Management

```
AMQSTREAMSPROJECT=amq-streams

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
oc run kafka-consumer -ti --image=registry.redhat.io/amq7/amq-streams-kafka-22:1.2.0 --rm=true --restart=Never -- bin/kafka-console-consumer.sh --bootstrap-server my-cluster-kafka-bootstrap:9092 --topic my-topic --from-beginning
```

Alternatively from the local registry

```
oc run kafka-consumer -ti --image=docker-registry.default.svc:5000/openshift/amq-streams-kafka-22:1.2.0 --rm=true --restart=Never -- bin/kafka-console-consumer.sh --bootstrap-server my-cluster-kafka-bootstrap:9092 --topic my-topic --from-beginning
```

Run a producer

```
oc run kafka-producer -ti --image=registry.redhat.io/amq7/amq-streams-kafka-22:1.2.0 --rm=true --restart=Never -- bin/kafka-console-producer.sh --broker-list my-cluster-kafka-bootstrap:9092 --topic my-topic
```

Alternatively from the local registry

```
oc run kafka-producer -ti --image=docker-registry.default.svc:5000/openshift/amq-streams-kafka-22:1.2.0 --rm=true --restart=Never -- bin/kafka-console-producer.sh --broker-list my-cluster-kafka-bootstrap:9092 --topic my-topic
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
oc extract secret/my-cluster-cluster-ca-cert --keys=ca.crt --to=- >execs/config/certificate.crt
oc extract secret/my-cluster-clients-ca-cert --keys=ca.crt --to=- >execs/config/certificate.crt

rm execs/config/trust.p12
keytool -importcert -keystore execs/config/trust.p12 -storetype PKCS12 -alias root -storepass password -file execs/config/certificate.crt -noprompt
```

Get the route of your cluster

```
oc get route my-cluster-kafka-bootstrap -o 'jsonpath={.spec.host}'
```

Configure it in the file `execs/config/application.properties`

```
vi execs/config/application.properties


mp.messaging.incoming.events.bootstrap.servers=<YOUR_ROUTE>:443
mp.messaging.incoming.events.security.protocol=SSL
```

Run the consumer. FYI, this an app built with Quarkus

```
cd execs ; java -jar quarkus-kafka-consumer-1.0-SNAPSHOT-runner.jar ; cd -
```

Run a producer to test it

```
oc run kafka-producer -ti --image=registry.redhat.io/amq7/amq-streams-kafka-22:1.2.0 --rm=true --restart=Never -- bin/kafka-console-producer.sh --broker-list my-cluster-kafka-bootstrap:9092 --topic my-topic
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
oc run kafka-producer -ti --image=registry.redhat.io/amq7/amq-streams-kafka-22:1.2.0 --rm=true --restart=Never -- bin/kafka-console-producer.sh --broker-list my-cluster-kafka-bootstrap:9092 --topic my-topic
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
oc run kafka-producer -ti --image=registry.redhat.io/amq7/amq-streams-kafka-22:1.2.0 --rm=true --restart=Never -- bin/kafka-console-producer.sh --broker-list my-cluster-kafka-bootstrap:9092 --topic my-topic
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

