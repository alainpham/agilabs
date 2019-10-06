# Install stuff
```
BASEURL=https://raw.githubusercontent.com/jboss-fuse/application-templates/application-templates-2.1.fuse-740025-redhat-00003
oc create -n openshift -f ${BASEURL}/fuse-apicurito.yml
oc replace -f ${BASEURL}/fuse-apicurito.yml -n openshift

oc new-project devops

oc new-app apicurito -p ROUTE_HOSTNAME=apicurito-devops.apps.ocp.loc
oc new-app apicurito -p ROUTE_HOSTNAME=apicurito-devops.apps.88.198.65.4.nip.io

oc new-app jenkins-persistent -p MEMORY_LIMIT=1Gi -p VOLUME_CAPACITY=4Gi
oc adm policy add-cluster-role-to-user cluster-admin system:serviceaccount:devops:jenkins

```

Set cpu limit to 4cores for jenkins

# Installation of Microcks

```

oc create -f https://raw.githubusercontent.com/microcks/microcks/master/install/openshift/openshift-persistent-full-template.yml -n openshift

oc new-app --template=microcks-persistent --param=APP_ROUTE_HOSTNAME=microcks-devops.apps.88.198.65.4.nip.io --param=KEYCLOAK_ROUTE_HOSTNAME=keycloak-devops.apps.88.198.65.4.nip.io --param=OPENSHIFT_MASTER=https://console.88.198.65.4.nip.io:8443 --param=OPENSHIFT_OAUTH_CLIENT_NAME=microcks-client -n devops

oc new-app --template=microcks-persistent --param=APP_ROUTE_HOSTNAME=microcks-devops.apps.ocp.loc --param=KEYCLOAK_ROUTE_HOSTNAME=keycloak-devops.apps.ocp.loc --param=OPENSHIFT_MASTER=https://console.ocp.loc:8443 --param=OPENSHIFT_OAUTH_CLIENT_NAME=microcks-client -n devops
```

# To delete Microck from project

```
oc delete all -l app=microcks
oc delete configmap -l group=microcks
oc delete secrets -l group=microcks
oc delete persistentvolumeclaims  -l group=microcks
oc delete oauthclients.oauth.openshift.io -l group=microcks
```

# Demo 1 : Dynamic API mocks with Microcks

* Create a dynamic API called `Beer Catalog API` in version `0.1`and resource `beer`

```
curl -X POST 'http://microcks-devops.app.88.198.65.4.nip.io/dynarest/Beer%20Catalog%20API/0.1/beer' -H 'Content-type: application/json' -d '{"name": "Rodenbach", "country": "Belgium", "type": "Brown ale", "rating": 4.2}'

curl -X POST 'http://microcks-devops.app.88.198.65.4.nip.io/dynarest/Beer%20Catalog%20API/0.1/beer' -H 'Content-type: application/json' -d '{"name": "Westmalle Triple", "country": "Belgium", "type": "Trappist", "rating": 3.8}'

curl -X POST 'http://microcks-devops.app.88.198.65.4.nip.io/dynarest/Beer%20Catalog%20API/0.1/beer' -H 'Content-type: application/json' -d '{"name": "Weissbier", "country": "Germany", "type": "Wheat", "rating": 4.1}'

curl -X POST 'http://microcks-devops.apps.ocp.loc/dynarest/Beer%20Catalog%20API/0.1/beer' -H 'Content-type: application/json' -d '{"name": "Rodenbach", "country": "Belgium", "type": "Brown ale", "rating": 4.2}'

curl -X POST 'http://microcks-devops.apps.ocp.loc/dynarest/Beer%20Catalog%20API/0.1/beer' -H 'Content-type: application/json' -d '{"name": "Westmalle Triple", "country": "Belgium", "type": "Trappist", "rating": 3.8}'

curl -X POST 'http://microcks-devops.apps.ocp.loc/dynarest/Beer%20Catalog%20API/0.1/beer' -H 'Content-type: application/json' -d '{"name": "Weissbier", "country": "Germany", "type": "Wheat", "rating": 4.1}'
```

# Query mocks

```
curl -X GET 'http://microcks-devops.app.88.198.65.4.nip.io/dynarest/Beer%20Catalog%20API/0.1/beer/'
curl -X GET 'http://microcks-devops.apps.ocp.loc/dynarest/Beer%20Catalog%20API/0.1/beer/'
```

# Demo 2 : Create an API from contract and create mocks

* Design with an API with Apicurio
* Import `Beer Catalog API.json` into `https://apicurito-devops.apps.88.198.65.4.nip.io/`
* Review the API design
* Import api def into Postman
* Generate some test cases with Postman (`Beer Catalog API.postman_collection.json`)
* Add importer into Microcks and point it to the Postman file

```
https://raw.githubusercontent.com/lbroudoux/apicurio-test/master/mocks/Beer%20Catalog%20API.postman_collection.json
```

* Test api mocks

```
curl -X GET 'http://microcks.apps.laurent.openhybridcloud.io/rest/Beer%20Catalog%20API/0.9/beer/Weissbier'

curl -X GET 'http://microcks.apps.laurent.openhybridcloud.io/rest/Beer%20Catalog%20API/0.9/beer?page=0'

curl -X GET 'http://microcks.apps.laurent.openhybridcloud.io/rest/Beer%20Catalog%20API/0.9/beer/findByStatus/available'
```



# Demo 3 : Show how to implement the API

* Show Fuse project code generation
* Implement the Service
* Deploy the service
* Test the service using microcks

http://beer-api-apps.app.88.198.65.4.nip.io/camel/


