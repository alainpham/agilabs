# lab00-basic-online-service

## Install Fuse Online

```
unzip fuse-online-install-1.7.zip
```

```
sed -i "s/registry.redhat.io\/.*\//docker-registry.default.svc:5000\/openshift\//" fuse-online-install-1.7/resources/*
sed -i "s/registry.redhat.io\/.*\//docker-registry.default.svc:5000\/openshift\//" fuse-online-install-1.7/templates/*

sed -i "s/registry.access.redhat.com\/.*\//docker-registry.default.svc:5000\/openshift\//" fuse-online-install-1.7/resources/*
sed -i "s/registry.access.redhat.com\/.*\//docker-registry.default.svc:5000\/openshift\//" fuse-online-install-1.7/templates/*
```

```
oc new-project fuse-online
cd fuse-online-install-1.7
bash install_ocp.sh --setup
bash install_ocp.sh 
```

* Make sure topic is created

		oc apply -f kafkatopic.yml

* Log into Fuse Online
* Create a connection to kafka

		event-streams-kafka-bootstrap.amq-streams.svc:9092

* Create a connection to salesforce
* Create a createOrUpdate from kafka to salesforce
* Create a mapping using email address
* Create a webhook to kafka for testing
* Test by calling webhook with person data
* View logs and metrics.
* Observe custom objects popping up.
* Show hawtio and access to source

```
curl -X POST   https://i-post-person-fuse-online.app.88.198.65.4.nip.io/webhook/postPerson   -H 'Content-Type: application/json'   -d '{
  "firstName": "John@mail.com",
  "lastName": "DOE",
  "age": 21
}' -k
```

		curl --form client_id=CLIENTID --form client_secret=SECRET  --form grant_type=password --form username=USER --form password=PASSWD https://login.salesforce.com/services/oauth2/token


