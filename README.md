
# Table of contents

- [Table of contents](#table-of-contents)
- [PoC Environment Preparation](#poc-environment-preparation)
  - [Import images](#import-images)
    - [Add trusted certificate for okd image registry manipulations on distant machines](#add-trusted-certificate-for-okd-image-registry-manipulations-on-distant-machines)
    - [From this point we will set the image registry url](#from-this-point-we-will-set-the-image-registry-url)
    - [Downloading Fuse images with Skopeo](#downloading-fuse-images-with-skopeo)
    - [Downloading AMQ Broker images with Skopeo](#downloading-amq-broker-images-with-skopeo)
    - [Downloading AMQ Interconnect images with Skopeo](#downloading-amq-interconnect-images-with-skopeo)
    - [Downloading AMQ Streams Strimzi images with Skopeo](#downloading-amq-streams-strimzi-images-with-skopeo)
    - [Downloading AMQ Online Images with Skopeo](#downloading-amq-online-images-with-skopeo)
- [Memory consumption estimation](#memory-consumption-estimation)


# PoC Environment Preparation

## Import images

List of images to be imported. Replace LOCAL with your own local repo ie : `docker-registry-default.apps.192.168.0.1.nip.io`

|Image from RH registry                                   |Local Openshift Registry Name                    |
|---------------------------------------------------------|-------------------------------------------------|
|docker://registry.redhat.io/fuse7/fuse-java-openshift:1.4|docker://LOCAL/openshift/fuse7-java-openshift:1.4|
|docker://registry.redhat.io/fuse7/fuse-console:1.4       |docker://LOCAL/openshift/fuse7-console:1.4       |
|docker://registry.redhat.io/amq7/amq-broker:7.4          |docker://LOCAL/openshift/amq-broker:7.4          |
|docker://registry.redhat.io/amq7/amq-interconnect:1.5    |docker://LOCAL/openshift/amq-interconnect:1.5    |
|docker://registry.redhat.io/amq7/amq-online-1-standard-controller:1.2|docker://LOCAL/openshift/amq-online-1-standard-controller:1.2|
|docker://registry.redhat.io/amq7/amq-online-1-agent:1.2|docker://LOCAL/openshift/amq-online-1-agent:1.2|
|docker://registry.redhat.io/amq7/amq-online-1-broker-plugin:1.2|docker://LOCAL/openshift/amq-online-1-broker-plugin:1.2|
|docker://registry.redhat.io/amq7/amq-online-1-topic-forwarder:1.2|docker://LOCAL/openshift/amq-online-1-topic-forwarder:1.2|
|docker://registry.redhat.io/amq7/amq-online-1-mqtt-gateway:1.2|docker://LOCAL/openshift/amq-online-1-mqtt-gateway:1.2|
|docker://registry.redhat.io/amq7/amq-online-1-mqtt-lwt:1.2|docker://LOCAL/openshift/amq-online-1-mqtt-lwt:1.2|
|docker://registry.redhat.io/amq7/amq-online-1-address-space-controller:1.2|docker://LOCAL/openshift/amq-online-1-address-space-controller:1.2|
|docker://registry.redhat.io/amq7/amq-online-1-api-server:1.2|docker://LOCAL/openshift/amq-online-1-api-server:1.2|
|docker://registry.redhat.io/amq7/amq-online-1-controller-manager:1.2|docker://LOCAL/openshift/amq-online-1-controller-manager:1.2|
|docker://registry.redhat.io/amq7/amq-online-1-none-auth-service:1.2|docker://LOCAL/openshift/amq-online-1-none-auth-service:1.2|
|docker://registry.redhat.io/amq7/amq-online-1-auth-plugin:1.2|docker://LOCAL/openshift/amq-online-1-auth-plugin:1.2|
|docker://registry.redhat.io/amq7/amq-online-1-console-init:1.2|docker://LOCAL/openshift/amq-online-1-console-init:1.2|
|docker://registry.redhat.io/amq7/amq-online-1-console-httpd:1.2|docker://LOCAL/openshift/amq-online-1-console-httpd:1.2|
|docker://registry.redhat.io/amq7/amq-streams-operator:1.2.0|docker://LOCAL/openshift/amq-streams-operator:1.2.0|
|docker://registry.redhat.io/amq7/amq-streams-bridge:1.2.0|docker://LOCAL/openshift/amq-streams-bridge:1.2.0|
|docker://registry.redhat.io/amq7/amq-streams-kafka-22:1.2.0|docker://LOCAL/openshift/amq-streams-kafka-22:1.2.0|
### Add trusted certificate for okd image registry manipulations on distant machines
```
oc extract -n default secrets/registry-certificates --keys=registry.crt
cp registry.crt /etc/pki/ca-trust/source/anchors/registry-openshift-ca.crt
update-ca-trust extract
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

### Downloading Fuse images with Skopeo

```
srcreg="docker://registry.redhat.io/"
tag="1.4"
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
```

### Downloading AMQ Broker images with Skopeo

Credentials to registry.redhat.io are required here to use these enterprise images.

```
srcreg="docker://registry.redhat.io/"
tag="7.4"
ns="amq7/"
imglist="amq-broker"

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

### Downloading AMQ Interconnect images with Skopeo

Credentials to registry.redhat.io registry are required here to use these enterprise images.

```
srcreg="docker://registry.redhat.io/"
tag="1.5"
ns="amq7/"
imglist="amq-interconnect"

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

### Downloading AMQ Streams Strimzi images with Skopeo

Credentials to registry.redhat.io are required here to use these enterprise images.

```
srcreg="docker://registry.redhat.io/"
tag="1.2.0"
ns="amq7/"
imglist="amq-streams-operator amq-streams-bridge amq-streams-kafka-22"

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

### Downloading AMQ Online Images with Skopeo

```
srcreg="docker://registry.redhat.io/"
tag="1.2"
ns="amq7/"
imglist="amq-online-1-standard-controller amq-online-1-agent amq-online-1-broker-plugin amq-online-1-topic-forwarder amq-online-1-mqtt-gateway amq-online-1-mqtt-lwt amq-online-1-address-space-controller amq-online-1-api-server amq-online-1-controller-manager amq-online-1-none-auth-service amq-online-1-auth-plugin amq-online-1-console-init amq-online-1-console-httpd"

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
