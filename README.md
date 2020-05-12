
# Table of contents

- [Table of contents](#table-of-contents)
- [PoC Environment Preparation](#poc-environment-preparation)
  - [Import images](#import-images)
    - [Add trusted certificate for openshift image registry manipulations on distant machines](#add-trusted-certificate-for-openshift-image-registry-manipulations-on-distant-machines)
    - [From this point we will set the image registry url](#from-this-point-we-will-set-the-image-registry-url)
    - [Fuse images with Skopeo](#fuse-images-with-skopeo)
    - [Apicurito images with Skopeo](#apicurito-images-with-skopeo)
    - [Apicurito Fuse images with Skopeo](#apicurito-fuse-images-with-skopeo)
    - [AMQ Broker images with Skopeo](#amq-broker-images-with-skopeo)
    - [AMQ Interconnect images with Skopeo](#amq-interconnect-images-with-skopeo)
    - [AMQ Streams Strimzi images with Skopeo](#amq-streams-strimzi-images-with-skopeo)
    - [AMQ Online Images with Skopeo](#amq-online-images-with-skopeo)
    - [Monitoring images with Skopeo + oauth](#monitoring-images-with-skopeo--oauth)
    - [RH SSO images with Skopeo](#rh-sso-images-with-skopeo)
    - [Fuse online images with Skopeo](#fuse-online-images-with-skopeo)
- [Memory consumption estimation](#memory-consumption-estimation)
  - [Complete download](#complete-download)
  - [Complete upload](#complete-upload)


# PoC Environment Preparation


## Import images

List of images to be imported. Replace LOCAL with your own local repo ie : `docker-registry-default.apps.192.168.0.1.nip.io`

|Image from RH registry                                   					|Local Openshift Registry Name                    					|
|---------------------------------------------------------------------------|-------------------------------------------------------------------|
|docker://registry.redhat.io/fuse7/fuse-java-openshift:1.5					|docker://LOCAL/openshift/fuse7-java-openshift:1.5					|
|docker://registry.redhat.io/fuse7/fuse-console:1.5       					|docker://LOCAL/openshift/fuse7-console:1.5       					|
|docker://registry.redhat.io/amq7/amq-broker:7.5          					|docker://LOCAL/openshift/amq-broker:7.5          					|
|docker://registry.redhat.io/amq7/amq-interconnect:1.6    					|docker://LOCAL/openshift/amq-interconnect:1.6    					|
|docker://registry.redhat.io/amq7/amq-online-1-standard-controller:1.3		|docker://LOCAL/openshift/amq-online-1-standard-controller:1.3		|
|docker://registry.redhat.io/amq7/amq-online-1-agent:1.3					|docker://LOCAL/openshift/amq-online-1-agent:1.3					|
|docker://registry.redhat.io/amq7/amq-online-1-broker-plugin:1.3			|docker://LOCAL/openshift/amq-online-1-broker-plugin:1.3			|
|docker://registry.redhat.io/amq7/amq-online-1-topic-forwarder:1.3			|docker://LOCAL/openshift/amq-online-1-topic-forwarder:1.3			|
|docker://registry.redhat.io/amq7/amq-online-1-mqtt-gateway:1.3				|docker://LOCAL/openshift/amq-online-1-mqtt-gateway:1.3				|
|docker://registry.redhat.io/amq7/amq-online-1-mqtt-lwt:1.3					|docker://LOCAL/openshift/amq-online-1-mqtt-lwt:1.3					|
|docker://registry.redhat.io/amq7/amq-online-1-address-space-controller:1.3	|docker://LOCAL/openshift/amq-online-1-address-space-controller:1.3	|
|docker://registry.redhat.io/amq7/amq-online-1-api-server:1.3				|docker://LOCAL/openshift/amq-online-1-api-server:1.3				|
|docker://registry.redhat.io/amq7/amq-online-1-controller-manager:1.3		|docker://LOCAL/openshift/amq-online-1-controller-manager:1.3		|
|docker://registry.redhat.io/amq7/amq-online-1-none-auth-service:1.3		|docker://LOCAL/openshift/amq-online-1-none-auth-service:1.3		|
|docker://registry.redhat.io/amq7/amq-online-1-auth-plugin:1.3				|docker://LOCAL/openshift/amq-online-1-auth-plugin:1.3				|
|docker://registry.redhat.io/amq7/amq-online-1-console-init:1.3				|docker://LOCAL/openshift/amq-online-1-console-init:1.3				|
|docker://registry.redhat.io/amq7/amq-online-1-console-httpd:1.3			|docker://LOCAL/openshift/amq-online-1-console-httpd:1.3			|
|docker://registry.redhat.io/amq7/amq-streams-operator:1.3.0				|docker://LOCAL/openshift/amq-streams-operator:1.3.0				|
|docker://registry.redhat.io/amq7/amq-streams-bridge:1.3.0					|docker://LOCAL/openshift/amq-streams-bridge:1.3.0					|
|docker://registry.redhat.io/amq7/amq-streams-kafka-23:1.3.0				|docker://LOCAL/openshift/amq-streams-kafka-23:1.3.0				|

### Add trusted certificate for openshift image registry manipulations on distant machines
```
oc extract -n default secrets/registry-certificates --keys=registry.crt
sudo cp registry.crt /etc/pki/ca-trust/source/anchors/registry-openshift-ca.crt
sudo update-ca-trust extract
```
(On archlinux)

```
sudo cp registry.crt /etc/ca-certificates/trust-source/anchors/registry-openshift-ca.crt
sudo trust extract-compat
```

### From this point we will set the image registry url

We set the registry url and get an auth token to access the internal Openshift Registry (must be as cluster-admin) and we also set the crediantials for registry.redhat.io with user and pass.

```
REGISTRY="$(oc get route docker-registry -n default -o 'jsonpath={.spec.host}')"
ocuser=`oc whoami`
octoken=`oc whoami -t`

user=usr
pass=pwd

echo $REGISTRY
echo $ocuser:$octoken
```

### Fuse images with Skopeo

```
srcreg="docker://registry.redhat.io/"
tag="1.5"
ns="fuse7/"
origpfx=fuse-
targetpfx=fuse7-
imglist="java-openshift console"

#Full list below
#imglist="java-openshift karaf-openshift eap-openshift console"

for img in $imglist
do
 echo $srcreg$ns$origpfx$img:$tag
 skopeo copy --screds $user:$pass $srcreg$ns$origpfx$img:$tag oci:./target:$ns$origpfx$img:$tag
done

for img in $imglist
do
 echo docker://$REGISTRY/openshift/$targetpfx$img:$tag
 skopeo --insecure-policy copy --dest-creds=$ocuser:$octoken oci:./target:$ns$origpfx$img:$tag docker://$REGISTRY/openshift/$targetpfx$img:$tag
done

for img in $imglist
do
 echo $srcreg$ns$origpfx$img:$tag
 skopeo --insecure-policy copy --screds $user:$pass --dest-creds=$ocuser:$octoken $srcreg$ns$origpfx$img:$tag docker://$REGISTRY/openshift/$targetpfx$img:$tag
done

```

### Apicurito images with Skopeo

Credentials to registry.redhat.io are required here to use these enterprise images.

```
srcreg="docker://registry.redhat.io/"
tag="1.5"
ns="fuse7/"
origpfx=fuse-
targetsfx=-ui
imglist="apicurito"


for img in $imglist
do
 echo $srcreg$ns$origpfx$img:$tag
 skopeo --insecure-policy  copy --screds $user:$pass --dest-creds=$ocuser:$octoken  $srcreg$ns$origpfx$img:$tag docker://$REGISTRY/openshift/$img$targetsfx:$tag
done


for img in $imglist
do
 echo $srcreg$ns$origpfx$img:$tag
 skopeo copy --screds $user:$pass $srcreg$ns$origpfx$img:$tag oci:./target:$ns$origpfx$img:$tag
done

for img in $imglist
do
 echo docker://$REGISTRY/openshift/$img$targetsfx:$tag
 skopeo --insecure-policy copy --dest-creds=$ocuser:$octoken oci:./target:$ns$origpfx$img:$tag docker://$REGISTRY/openshift/$img$targetsfx:$tag
done



```

### Apicurito Fuse images with Skopeo

Credentials to registry.redhat.io are required here to use these enterprise images.

```
srcreg="docker://registry.redhat.io/"
tag="1.5"
ns="fuse7/"
origpfx=fuse-
targetpfx=fuse-
imglist="apicurito-generator"

for img in $imglist
do
 echo $srcreg$ns$origpfx$img:$tag
 skopeo  --insecure-policy copy --screds $user:$pass --dest-creds=$ocuser:$octoken  $srcreg$ns$origpfx$img:$tag docker://$REGISTRY/openshift/$targetpfx$img:$tag
done


for img in $imglist
do
 echo $srcreg$ns$origpfx$img:$tag
 skopeo copy --screds $user:$pass $srcreg$ns$origpfx$img:$tag oci:./target:$ns$origpfx$img:$tag
done

for img in $imglist
do
 echo docker://$REGISTRY/openshift/$targetpfx$img:$tag
 skopeo --insecure-policy copy --dest-creds=$ocuser:$octoken oci:./target:$ns$origpfx$img:$tag docker://$REGISTRY/openshift/$targetpfx$img:$tag
done
```

### AMQ Broker images with Skopeo

Credentials to registry.redhat.io are required here to use these enterprise images.

```
srcreg="docker://registry.redhat.io/"
tag="7.5"
ns="amq7/"
imglist="amq-broker"

for img in $imglist
do
 echo $srcreg$ns$img:$tag
 skopeo --insecure-policy copy --screds $user:$pass --dest-creds=$ocuser:$octoken  $srcreg$ns$img:$tag docker://$REGISTRY/openshift/$img:$tag
done


for img in $imglist
do
 echo $srcreg$ns$img:$tag
 skopeo copy --screds $user:$pass $srcreg$ns$img:$tag oci:./target:$ns$img:$tag
done

for img in $imglist
do
 echo docker://$REGISTRY/openshift/$img:$tag
 skopeo --insecure-policy copy --dest-creds=$ocuser:$octoken oci:./target:$ns$img:$tag docker://$REGISTRY/openshift/$img:$tag
done

```

### AMQ Interconnect images with Skopeo

Credentials to registry.redhat.io registry are required here to use these enterprise images.

```
srcreg="docker://registry.redhat.io/"
tag="1.6"
ns="amq7/"
imglist="amq-interconnect"


for img in $imglist
do
 echo $srcreg$ns$img:$tag
 skopeo  --insecure-policy copy --screds $user:$pass --dest-creds=$ocuser:$octoken  $srcreg$ns$img:$tag docker://$REGISTRY/openshift/$img:$tag
done


for img in $imglist
do
 echo $srcreg$ns$img:$tag
 skopeo copy --screds $user:$pass $srcreg$ns$img:$tag oci:./target:$ns$img:$tag
done

for img in $imglist
do
 echo docker://$REGISTRY/openshift/$img:$tag
 skopeo --insecure-policy copy --dest-creds=$ocuser:$octoken oci:./target:$ns$img:$tag docker://$REGISTRY/openshift/$img:$tag
done
```

### AMQ Streams Strimzi images with Skopeo

Credentials to registry.redhat.io are required here to use these enterprise images.

```
srcreg="docker://registry.redhat.io/"
tag="1.3.0"
ns="amq7/"
imglist="amq-streams-operator amq-streams-bridge amq-streams-kafka-23"


for img in $imglist
do
 echo $srcreg$ns$img:$tag
 skopeo  --insecure-policy copy --screds $user:$pass --dest-creds=$ocuser:$octoken  $srcreg$ns$img:$tag docker://$REGISTRY/openshift/$img:$tag
done


for img in $imglist
do
 echo $srcreg$ns$img:$tag
 skopeo copy --screds $user:$pass $srcreg$ns$img:$tag oci:./target:$ns$img:$tag
done

for img in $imglist
do
 echo docker://$REGISTRY/openshift/$img:$tag
 skopeo --insecure-policy copy --dest-creds=$ocuser:$octoken oci:./target:$ns$img:$tag docker://$REGISTRY/openshift/$img:$tag
done
```

### AMQ Online Images with Skopeo

```
srcreg="docker://registry.redhat.io/"
tag="1.3"
ns="amq7/"
imglist="amq-online-1-standard-controller amq-online-1-agent amq-online-1-broker-plugin amq-online-1-topic-forwarder amq-online-1-mqtt-gateway amq-online-1-mqtt-lwt amq-online-1-address-space-controller amq-online-1-api-server amq-online-1-controller-manager amq-online-1-none-auth-service amq-online-1-auth-plugin amq-online-1-console-init amq-online-1-console-httpd"


for img in $imglist
do
 echo $srcreg$ns$img:$tag
 skopeo  --insecure-policy copy --screds $user:$pass  --dest-creds=$ocuser:$octoken  $srcreg$ns$img:$tag docker://$REGISTRY/openshift/$img:$tag
done


for img in $imglist
do
 echo $srcreg$ns$img:$tag
 skopeo copy --screds $user:$pass $srcreg$ns$img:$tag oci:./target:$ns$img:$tag
done

for img in $imglist
do
 echo docker://$REGISTRY/openshift/$img:$tag
 skopeo --insecure-policy copy --dest-creds=$ocuser:$octoken oci:./target:$ns$img:$tag docker://$REGISTRY/openshift/$img:$tag
done
```
### Monitoring images with Skopeo + oauth

Credentials to registry.redhat.io registry are required here to use these enterprise images.

```
srcreg="docker://registry.redhat.io/"
tag="v3.11"
ns="openshift3/"
imglist="prometheus grafana oauth-proxy"


for img in $imglist
do
 echo $srcreg$ns$img:$tag
 skopeo --insecure-policy  copy --screds $user:$pass  --dest-creds=$ocuser:$octoken $srcreg$ns$img:$tag docker://$REGISTRY/openshift/$img:$tag
done


for img in $imglist
do
 echo $srcreg$ns$img:$tag
 skopeo copy --screds $user:$pass $srcreg$ns$img:$tag oci:./target:$ns$img:$tag
done

for img in $imglist
do
 echo docker://$REGISTRY/openshift/$img:$tag
 skopeo --insecure-policy copy --dest-creds=$ocuser:$octoken oci:./target:$ns$img:$tag docker://$REGISTRY/openshift/$img:$tag
done
```

### RH SSO images with Skopeo

Credentials to registry.redhat.io registry are required here to use these enterprise images.

```
srcreg="docker://registry.redhat.io/"
tag="1.0"
ns="redhat-sso-7/"
imglist="sso73-openshift"


for img in $imglist
do
 echo $srcreg$ns$img:$tag
 skopeo  --insecure-policy copy --screds $user:$pass --dest-creds=$ocuser:$octoken $srcreg$ns$img:$tag docker://$REGISTRY/openshift/$img:$tag
done


for img in $imglist
do
 echo $srcreg$ns$img:$tag
 skopeo copy --screds $user:$pass $srcreg$ns$img:$tag oci:./target:$ns$img:$tag
done

for img in $imglist
do
 echo docker://$REGISTRY/openshift/$img:$tag
 skopeo --insecure-policy copy --dest-creds=$ocuser:$octoken oci:./target:$ns$img:$tag docker://$REGISTRY/openshift/$img:$tag
done
```

### Fuse online images with Skopeo

/!\ WIP doesn't work as offline install at the moment

```
srcreg="docker://registry.redhat.io/"
ns="fuse7/"
imglist="fuse-ignite-server:1.4-14 fuse-ignite-ui:1.4-6 fuse-ignite-meta:1.4-13 fuse-ignite-s2i:1.4-13 fuse-online-operator:1.4-11"

for img in $imglist
do
 echo $srcreg$ns$img:$tag
 skopeo copy --screds $user:$pass $srcreg$ns$img oci:./target:$ns$img
done

skopeo copy --screds $user:$pass docker://registry.access.redhat.com/openshift3/prometheus:v3.9.25 oci:./target:openshift3/prometheus:v3.9.25

skopeo copy --screds $user:$pass docker://registry.redhat.io/openshift4/ose-oauth-proxy:4.1 oci:./target:openshift4/ose-oauth-proxy:4.1

skopeo copy --screds $user:$pass docker://registry.redhat.io/fuse7-tech-preview/fuse-postgres-exporter:1.4-4 oci:./target:fuse7-tech-preview/fuse-postgres-exporter:1.4-4

skopeo copy --screds $user:$pass docker://registry.redhat.io/fuse7-tech-preview/data-virtualization-server-rhel7:1.4-15 oci:./target:fuse7-tech-preview/data-virtualization-server-rhel7:1.4-15

skopeo copy --screds $user:$pass docker://registry.access.redhat.com/jboss-amq-6/amq63-openshift:1.3 oci:./target:jboss-amq-6/amq63-openshift:1.3


for img in $imglist
do
 echo docker://$REGISTRY/openshift/$img:$tag
 skopeo --insecure-policy copy --dest-creds=$ocuser:$octoken oci:./target:$ns$img docker://$REGISTRY/openshift/$img
done
```

# Memory consumption estimation

| Products    | Components             | NB Instances | Limit Per Instance | Total |
|-------------|------------------------|--------------|--------------------|-------|
| Fuse        | Fuse Console           | 1            | 256                | 256   |
|             | Fuse projects          | 4            | 256                | 1024  |
| AMQ Streams | ClusterOperator        | 1            | 256                | 256   |
|             | Zookeeper              | 3            | 256                | 768   |
|             | KafkaBroker            | 3            | 256                | 768   |
| AMQ Broker  | Operator               | 1            | 256                | 256   |
|             | BrokerInstance         | 2            | 256                | 512   |
| AMQ Online  | EnMasseOperator        | 1            | 128                | 128   |
|             | AddressSpaceController | 1            | 512                | 512   |
|             | admin                  | 1            | 512                | 512   |
|             | api-server             | 1            | 512                | 512   |
|             | BrokerInstance         | 1            | 512                | 512   |
|             | QRouterD               | 1            | 512                | 512   |
| TOTAL       |                        |              |                    | 6528  |


## Complete download

```
srcreg="docker://registry.redhat.io/"
tag="1.5"
ns="fuse7/"
origpfx=fuse-
targetpfx=fuse7-
imglist="java-openshift console"

#Full list below
#imglist="java-openshift karaf-openshift eap-openshift console"

for img in $imglist
do
 echo $srcreg$ns$origpfx$img:$tag
 skopeo copy --screds $user:$pass $srcreg$ns$origpfx$img:$tag oci:./target:$ns$origpfx$img:$tag
done

srcreg="docker://registry.redhat.io/"
tag="1.5"
ns="fuse7/"
origpfx=fuse-
targetsfx=-ui
imglist="apicurito"

for img in $imglist
do
 echo $srcreg$ns$origpfx$img:$tag
 skopeo copy --screds $user:$pass $srcreg$ns$origpfx$img:$tag oci:./target:$ns$origpfx$img:$tag
done

srcreg="docker://registry.redhat.io/"
tag="1.5"
ns="fuse7/"
origpfx=fuse-
targetpfx=fuse-
imglist="apicurito-generator"

for img in $imglist
do
 echo $srcreg$ns$origpfx$img:$tag
 skopeo copy --screds $user:$pass $srcreg$ns$origpfx$img:$tag oci:./target:$ns$origpfx$img:$tag
done

srcreg="docker://registry.redhat.io/"
tag="7.5"
ns="amq7/"
imglist="amq-broker"

for img in $imglist
do
 echo $srcreg$ns$img:$tag
 skopeo copy --screds $user:$pass $srcreg$ns$img:$tag oci:./target:$ns$img:$tag
done



srcreg="docker://registry.redhat.io/"
tag="1.6"
ns="amq7/"
imglist="amq-interconnect"

for img in $imglist
do
 echo $srcreg$ns$img:$tag
 skopeo copy --screds $user:$pass $srcreg$ns$img:$tag oci:./target:$ns$img:$tag
done



srcreg="docker://registry.redhat.io/"
tag="1.3.0"
ns="amq7/"
imglist="amq-streams-operator amq-streams-bridge amq-streams-kafka-23"

for img in $imglist
do
 echo $srcreg$ns$img:$tag
 skopeo copy --screds $user:$pass $srcreg$ns$img:$tag oci:./target:$ns$img:$tag
done



srcreg="docker://registry.redhat.io/"
tag="1.3"
ns="amq7/"
imglist="amq-online-1-standard-controller amq-online-1-agent amq-online-1-broker-plugin amq-online-1-topic-forwarder amq-online-1-mqtt-gateway amq-online-1-mqtt-lwt amq-online-1-address-space-controller amq-online-1-api-server amq-online-1-controller-manager amq-online-1-none-auth-service amq-online-1-auth-plugin amq-online-1-console-init amq-online-1-console-httpd"

for img in $imglist
do
 echo $srcreg$ns$img:$tag
 skopeo copy --screds $user:$pass $srcreg$ns$img:$tag oci:./target:$ns$img:$tag
done

srcreg="docker://registry.redhat.io/"
tag="v3.11"
ns="openshift3/"
imglist="prometheus grafana oauth-proxy"

for img in $imglist
do
 echo $srcreg$ns$img:$tag
 skopeo copy --screds $user:$pass $srcreg$ns$img:$tag oci:./target:$ns$img:$tag
done

srcreg="docker://registry.redhat.io/"
tag="1.0"
ns="redhat-sso-7/"
imglist="sso73-openshift"

for img in $imglist
do
 echo $srcreg$ns$img:$tag
 skopeo copy --screds $user:$pass $srcreg$ns$img:$tag oci:./target:$ns$img:$tag
done


```

/!\WIP does not work at the moment
```

srcreg="docker://registry.redhat.io/"
ns="fuse7/"
imglist="fuse-ignite-server:1.4-14 fuse-ignite-ui:1.4-6 fuse-ignite-meta:1.4-13 fuse-ignite-s2i:1.4-13 fuse-online-operator:1.4-11"

for img in $imglist
do
 echo $srcreg$ns$img:$tag
 skopeo copy --screds $user:$pass $srcreg$ns$img oci:./target:$ns$img
done

skopeo copy --screds $user:$pass docker://registry.access.redhat.com/openshift3/prometheus:v3.9.25 oci:./target:openshift3/prometheus:v3.9.25

skopeo copy --screds $user:$pass docker://registry.redhat.io/openshift4/ose-oauth-proxy:4.1 oci:./target:openshift4/ose-oauth-proxy:4.1

skopeo copy --screds $user:$pass docker://registry.redhat.io/fuse7-tech-preview/fuse-postgres-exporter:1.4-4 oci:./target:fuse7-tech-preview/fuse-postgres-exporter:1.4-4

skopeo copy --screds $user:$pass docker://registry.redhat.io/fuse7-tech-preview/data-virtualization-server-rhel7:1.4-15 oci:./target:fuse7-tech-preview/data-virtualization-server-rhel7:1.4-15

skopeo copy --screds $user:$pass docker://registry.access.redhat.com/jboss-amq-6/amq63-openshift:1.3 oci:./target:jboss-amq-6/amq63-openshift:1.3

```

## Complete upload

```

srcreg="docker://registry.redhat.io/"
tag="1.5"
ns="fuse7/"
origpfx=fuse-
targetpfx=fuse7-
imglist="java-openshift console"

for img in $imglist
do
 echo docker://$REGISTRY/openshift/$targetpfx$img:$tag
 skopeo --insecure-policy copy --dest-creds=$ocuser:$octoken oci:./target:$ns$origpfx$img:$tag docker://$REGISTRY/openshift/$targetpfx$img:$tag
done

srcreg="docker://registry.redhat.io/"
tag="1.5"
ns="fuse7/"
origpfx=fuse-
targetsfx=-ui
imglist="apicurito"

for img in $imglist
do
 echo docker://$REGISTRY/openshift/$img$targetsfx:$tag
 skopeo --insecure-policy copy --dest-creds=$ocuser:$octoken oci:./target:$ns$origpfx$img:$tag docker://$REGISTRY/openshift/$img$targetsfx:$tag
done

srcreg="docker://registry.redhat.io/"
tag="1.5"
ns="fuse7/"
origpfx=fuse-
targetpfx=fuse-
imglist="apicurito-generator"

for img in $imglist
do
 echo docker://$REGISTRY/openshift/$targetpfx$img:$tag
 skopeo --insecure-policy copy --dest-creds=$ocuser:$octoken oci:./target:$ns$origpfx$img:$tag docker://$REGISTRY/openshift/$targetpfx$img:$tag
done

srcreg="docker://registry.redhat.io/"
tag="7.5"
ns="amq7/"
imglist="amq-broker"

for img in $imglist
do
 echo docker://$REGISTRY/openshift/$img:$tag
 skopeo --insecure-policy copy --dest-creds=$ocuser:$octoken oci:./target:$ns$img:$tag docker://$REGISTRY/openshift/$img:$tag
done

srcreg="docker://registry.redhat.io/"
tag="1.6"
ns="amq7/"
imglist="amq-interconnect"

for img in $imglist
do
 echo docker://$REGISTRY/openshift/$img:$tag
 skopeo --insecure-policy copy --dest-creds=$ocuser:$octoken oci:./target:$ns$img:$tag docker://$REGISTRY/openshift/$img:$tag
done

srcreg="docker://registry.redhat.io/"
tag="1.3.0"
ns="amq7/"
imglist="amq-streams-operator amq-streams-bridge amq-streams-kafka-23"

for img in $imglist
do
 echo docker://$REGISTRY/openshift/$img:$tag
 skopeo --insecure-policy copy --dest-creds=$ocuser:$octoken oci:./target:$ns$img:$tag docker://$REGISTRY/openshift/$img:$tag
done

srcreg="docker://registry.redhat.io/"
tag="1.3"
ns="amq7/"
imglist="amq-online-1-standard-controller amq-online-1-agent amq-online-1-broker-plugin amq-online-1-topic-forwarder amq-online-1-mqtt-gateway amq-online-1-mqtt-lwt amq-online-1-address-space-controller amq-online-1-api-server amq-online-1-controller-manager amq-online-1-none-auth-service amq-online-1-auth-plugin amq-online-1-console-init amq-online-1-console-httpd"

for img in $imglist
do
 echo docker://$REGISTRY/openshift/$img:$tag
 skopeo --insecure-policy copy --dest-creds=$ocuser:$octoken oci:./target:$ns$img:$tag docker://$REGISTRY/openshift/$img:$tag
done

srcreg="docker://registry.redhat.io/"
tag="v3.11"
ns="openshift3/"
imglist="prometheus grafana oauth-proxy"

for img in $imglist
do
 echo docker://$REGISTRY/openshift/$img:$tag
 skopeo --insecure-policy copy --dest-creds=$ocuser:$octoken oci:./target:$ns$img:$tag docker://$REGISTRY/openshift/$img:$tag
done

srcreg="docker://registry.redhat.io/"
tag="1.0"
ns="redhat-sso-7/"
imglist="sso73-openshift"

for img in $imglist
do
 echo docker://$REGISTRY/openshift/$img:$tag
 skopeo --insecure-policy copy --dest-creds=$ocuser:$octoken oci:./target:$ns$img:$tag docker://$REGISTRY/openshift/$img:$tag
done

```



```
srcreg="docker://registry.redhat.io/"
ns="fuse7/"
imglist="fuse-ignite-server:1.4-14 fuse-ignite-ui:1.4-6 fuse-ignite-meta:1.4-13 fuse-ignite-s2i:1.4-13 fuse-online-operator:1.4-11"

for img in $imglist
do
 echo docker://$REGISTRY/openshift/$img:$tag
 skopeo --insecure-policy copy --dest-creds=$ocuser:$octoken oci:./target:$ns$img docker://$REGISTRY/openshift/$img
done

skopeo --insecure-policy copy --dest-creds=$ocuser:$octoken oci:./target:openshift3/prometheus:v3.9.25 docker://$REGISTRY/openshift/prometheus:v3.9.25

skopeo --insecure-policy copy --dest-creds=$ocuser:$octoken oci:./target:openshift4/ose-oauth-proxy:4.1 docker://$REGISTRY/openshift/ose-oauth-proxy:4.1

skopeo --insecure-policy copy --dest-creds=$ocuser:$octoken oci:./target:fuse7-tech-preview/fuse-postgres-exporter:1.4-4 docker://$REGISTRY/openshift/fuse-postgres-exporter:1.4-4


skopeo --insecure-policy copy --dest-creds=$ocuser:$octoken oci:./target:fuse7-tech-preview/data-virtualization-server-rhel7:1.4-15 docker://$REGISTRY/openshift/data-virtualization-server-rhel7:1.4-15

skopeo --insecure-policy copy --dest-creds=$ocuser:$octoken oci:./target:jboss-amq-6/amq63-openshift:1.3 docker://$REGISTRY/openshift/amq63-openshift:1.3

```


## Complete Download Upload Direct

```
srcreg="docker://registry.redhat.io/"
tag="1.5"
ns="fuse7/"
origpfx=fuse-
targetpfx=fuse7-
imglist="java-openshift console"

for img in $imglist
do
 echo $srcreg$ns$origpfx$img:$tag
 skopeo --insecure-policy copy --screds $user:$pass --dest-creds=$ocuser:$octoken $srcreg$ns$origpfx$img:$tag docker://$REGISTRY/openshift/$targetpfx$img:$tag
done


srcreg="docker://registry.redhat.io/"
tag="1.5"
ns="fuse7/"
origpfx=fuse-
targetsfx=-ui
imglist="apicurito"


for img in $imglist
do
 echo $srcreg$ns$origpfx$img:$tag
 skopeo --insecure-policy  copy --screds $user:$pass --dest-creds=$ocuser:$octoken  $srcreg$ns$origpfx$img:$tag docker://$REGISTRY/openshift/$img$targetsfx:$tag
done

srcreg="docker://registry.redhat.io/"
tag="1.5"
ns="fuse7/"
origpfx=fuse-
targetpfx=fuse-
imglist="apicurito-generator"

for img in $imglist
do
 echo $srcreg$ns$origpfx$img:$tag
 skopeo  --insecure-policy copy --screds $user:$pass --dest-creds=$ocuser:$octoken  $srcreg$ns$origpfx$img:$tag docker://$REGISTRY/openshift/$targetpfx$img:$tag
done

srcreg="docker://registry.redhat.io/"
tag="7.5"
ns="amq7/"
imglist="amq-broker"

for img in $imglist
do
 echo $srcreg$ns$img:$tag
 skopeo --insecure-policy copy --screds $user:$pass --dest-creds=$ocuser:$octoken  $srcreg$ns$img:$tag docker://$REGISTRY/openshift/$img:$tag
done

srcreg="docker://registry.redhat.io/"
tag="1.6"
ns="amq7/"
imglist="amq-interconnect"


for img in $imglist
do
 echo $srcreg$ns$img:$tag
 skopeo  --insecure-policy copy --screds $user:$pass --dest-creds=$ocuser:$octoken  $srcreg$ns$img:$tag docker://$REGISTRY/openshift/$img:$tag
done


srcreg="docker://registry.redhat.io/"
tag="1.3.0"
ns="amq7/"
imglist="amq-streams-operator amq-streams-bridge amq-streams-kafka-23"


for img in $imglist
do
 echo $srcreg$ns$img:$tag
 skopeo  --insecure-policy copy --screds $user:$pass --dest-creds=$ocuser:$octoken  $srcreg$ns$img:$tag docker://$REGISTRY/openshift/$img:$tag
done

srcreg="docker://registry.redhat.io/"
tag="1.3"
ns="amq7/"
imglist="amq-online-1-standard-controller amq-online-1-agent amq-online-1-broker-plugin amq-online-1-topic-forwarder amq-online-1-mqtt-gateway amq-online-1-mqtt-lwt amq-online-1-address-space-controller amq-online-1-api-server amq-online-1-controller-manager amq-online-1-none-auth-service amq-online-1-auth-plugin amq-online-1-console-init amq-online-1-console-httpd"


for img in $imglist
do
 echo $srcreg$ns$img:$tag
 skopeo  --insecure-policy copy --screds $user:$pass  --dest-creds=$ocuser:$octoken  $srcreg$ns$img:$tag docker://$REGISTRY/openshift/$img:$tag
done

srcreg="docker://registry.redhat.io/"
tag="v3.11"
ns="openshift3/"
imglist="prometheus grafana oauth-proxy"


for img in $imglist
do
 echo $srcreg$ns$img:$tag
 skopeo --insecure-policy  copy --screds $user:$pass  --dest-creds=$ocuser:$octoken $srcreg$ns$img:$tag docker://$REGISTRY/openshift/$img:$tag
done

srcreg="docker://registry.redhat.io/"
tag="1.0"
ns="redhat-sso-7/"
imglist="sso73-openshift"


for img in $imglist
do
 echo $srcreg$ns$img:$tag
 skopeo  --insecure-policy copy --screds $user:$pass --dest-creds=$ocuser:$octoken $srcreg$ns$img:$tag docker://$REGISTRY/openshift/$img:$tag
done

```

