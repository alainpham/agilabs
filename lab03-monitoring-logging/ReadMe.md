# Install Grafana Dashboard


# Configure Prometheus to scrape apps

```
pass=REPLACE_PASSWORD

oc new-project apps-monitoring
oc create secret generic prom --from-file distrib/prometheus.yml -o yaml --dry-run | oc create -f -

echo $pass | htpasswd -c -i -s distrib/auth internal

oc create secret generic prometheus-htpasswd --from-file distrib/auth -o yaml --dry-run | oc create -f -
rm distrib/auth

oc create -f distrib/prometheus-standalone.yml
oc new-app prometheus
oc adm policy add-cluster-role-to-user cluster-reader system:serviceaccount:apps-monitoring:prom
```

#Install grafana

```
cat distrib/promconnect.yml | sed "s/PASSWORD/$pass/" > promconnect.yml
oc create secret generic grafana-datasources --from-file promconnect.yml -o yaml --dry-run | oc create -f -
rm promconnect.yml


oc create -f distrib/grafana.yml

oc new-app grafana

```

for deleting

```
oc delete all -l app=grafana
oc delete service grafana
oc delete serviceaccount grafana
oc delete clusterrolebinding grafana-cluster-reader
oc delete secret grafana-proxy
oc delete routes grafana
oc delete configmap grafana-config
oc replace -f distrib/grafana.yml

```

# Get Hystrix for viewing circuitbreaker
wget https://bintray.com/kennedyoliveira/maven/download_file?file_path=com%2Fgithub%2Fkennedyoliveira%2Fstandalone-hystrix-dashboard%2F1.5.6%2Fstandalone-hystrix-dashboard-1.5.6-all.jar

java -jar standalone-hystrix-dashboard-1.5.6-all.jar

oc project apps
oc create -f hystrix-dashboard-1.0.28-openshift.yml
oc expose service hystrix-dashboard --port=8080

view on openshift

http://hystrix-dashboard-apps.apps.88.198.65.4.nip.io/monitor/monitor.html?stream=http%3A%2F%2Flab02-service-composition-apps.apps.88.198.65.4.nip.io%2Fhystrix.stream

# Setup Elastic search with fuse index

1) In Elastic Search, create a index called fuse

```
curl "http://localhost:9200/fuse" -XPUT  -H 'Content-Type: application/json' -d'
{
  "mappings": {
    "event": {
      "properties": {
        "timestamp": {
          "type":   "date",
          "format": "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis"
        }
      }
    }
  }
}'
```


```
curl -k -H "Authorization: Bearer TOKEN" -XPUT "https://es.app.88.198.65.4.nip.io/fuse" -H 'Content-Type: application/json' -d'
{
  "mappings": {
    "event": {
      "properties": {
        "timestamp": {
          "type":   "date",
          "format": "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis"
        }
      }
    }
  }
}'

curl -k -H "Authorization: Bearer TOKEN"  -X GET  "https://es.app.88.198.65.4.nip.io/_cat/indices?v"

curl -k -H "Authorization: Bearer TOKEN"  -X GET  "https://localhost:4443"
curl -k -H "Authorization: Bearer TOKEN"  -X GET  "https://es.app.88.198.65.4.nip.io"

curl -k -H "Authorization: Bearer TOKEN"  -X GET  "https://logging-es.logging.svc:9200"


curl -k -H "Authorization: Bearer TOKEN" -X PUT "https://es.app.88.198.65.4.nip.io/fuse/event/1" -H 'Content-Type: application/json' -d'
{
  "msg" : "agilabs.Customer@775114e7",
  "executionTimeMs" : 3,
  "eventProducer" : "lab02-service-composition-mock-fuse",
  "state" : "success",
  "flowInstanceID" : "ID-aplinux-1543788487045-2-34",
  "timestamp" : 1543790227630
}
'
```


Auth to elastic search


