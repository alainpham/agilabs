## Unzip package

```
unzip amq-online-install-1-2_1.zip
cd 
```

## Change configs

```
sed -i "s/registry.redhat.io\/amq7\//docker-registry.default.svc:5000\/openshift\//" install/bundles/amq-online/050-Deployment*

sed -i "s/registry.redhat.io\/amq7\//docker-registry.default.svc:5000\/openshift\//" install/bundles/amq-online/050-Deployment*
sed -i "s/openshift\/oauth-proxy.latest/docker-registry.default.svc:5000\/openshift\/oauth-proxy:v3.11/" install/bundles/amq-online/050-Deployment*
sed -i "s/registry.redhat.io\/redhat-sso-7\/sso73-openshift:latest/docker-registry.default.svc:5000\/openshift\/sso73-openshift:1.0/" install/bundles/amq-online/050-Deployment*



```

## Create project

```
oc new-project amq-online-infra
```

## Install bundles 

```
oc apply -f install/bundles/amq-online
```

## Install plans roles and auth service

```
oc apply -f install/components/example-plans
oc apply -f install/components/example-roles
oc apply -f install/components/example-authservices/standard-authservice.yaml
oc apply -f install/components/example-authservices/none-authservice.yaml
```

## Create an address space

Go to AMQ Online console and create an address space called `demo-as`


## Import certifacte

```
oc get addressspace demo-as -n amq-online-infra -o jsonpath='{.status.caCert}{"\n"}' | base64 --decode > ca.crt

keytool -import -trustcacerts -alias root -file ca.crt -storetype pkcs12 -keystore trust.pkcs12 -storepass password -noprompt
```

