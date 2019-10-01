
# Table of contents

- [Table of contents](#table-of-contents)
- [PoC Environment Preparation](#poc-environment-preparation)
  - [Import images](#import-images)
    - [Add trusted certificate for openshift image registry manipulations on distant machines](#add-trusted-certificate-for-openshift-image-registry-manipulations-on-distant-machines)
    - [From this point we will set the image registry url](#from-this-point-we-will-set-the-image-registry-url)
    - [Fuse images with Skopeo](#fuse-images-with-skopeo)
    - [AMQ Broker images with Skopeo](#amq-broker-images-with-skopeo)
    - [AMQ Streams Strimzi images with Skopeo](#amq-streams-strimzi-images-with-skopeo)


# PoC Environment Preparation

## Import images

List of images to be imported. Replace LOCAL with your own local repo ie : `docker-registry-default.apps.192.168.0.1.nip.io`

|Image from RH registry                                   |Local Openshift Registry Name                    |
|---------------------------------------------------------|-------------------------------------------------|
|docker://registry.redhat.io/fuse7/fuse-java-openshift:1.1|docker://LOCAL/openshift/fuse7-java-openshift:1.1|
|docker://registry.redhat.io/fuse7/fuse-console:1.1       |docker://LOCAL/openshift/fuse7-console:1.1       |
|docker://registry.redhat.io/amq-broker-7/amq-broker-73-openshift:7.3|docker://LOCAL/openshift/amq-broker-73-openshift:7.3|
|docker://registry.redhat.io/amq7/amq-streams-cluster-operator:1.1.0|docker://LOCAL/openshift/amq-streams-cluster-operator:1.1.0|
|docker://registry.redhat.io/amq7/amq-streams-topic-operator:1.1.0|docker://LOCAL/openshift/amq-streams-topic-operator:1.1.0|
|docker://registry.redhat.io/amq7/amq-streams-user-operator:1.1.0|docker://LOCAL/openshift/amq-streams-user-operator:1.1.0|
|docker://registry.redhat.io/amq7/amq-streams-kafka-init:1.1.0|docker://LOCAL/openshift/amq-streams-kafka-init:1.1.0|
|docker://registry.redhat.io/amq7/amq-streams-zookeeper-stunnel:1.1.0|docker://LOCAL/openshift/amq-streams-zookeeper-stunnel:1.1.0|
|docker://registry.redhat.io/amq7/amq-streams-kafka-stunnel:1.1.0|docker://LOCAL/openshift/amq-streams-kafka-stunnel:1.1.0|
|docker://registry.redhat.io/amq7/amq-streams-entity-operator-stunnel:1.1.0|docker://LOCAL/openshift/amq-streams-entity-operator-stunnel:1.1.0|
|docker://registry.redhat.io/amq7/amq-streams-zookeeper:1.1.0-kafka-2.1.1|docker://LOCAL/openshift/amq-streams-zookeeper:1.1.0-kafka-2.1.1|
|docker://registry.redhat.io/amq7/amq-streams-kafka:1.1.0-kafka-2.1.1|docker://LOCAL/openshift/amq-streams-kafka:1.1.0-kafka-2.1.1|
|docker://registry.redhat.io/amq7/amq-streams-kafka-connect:1.1.0-kafka-2.1.1|docker://LOCAL/openshift/amq-streams-kafka-connect:1.1.0-kafka-2.1.1|
|docker://registry.redhat.io/amq7/amq-streams-kafka-connect-s2i:1.1.0-kafka-2.1.1|docker://LOCAL/openshift/amq-streams-kafka-connect-s2i:1.1.0-kafka-2.1.1|
|docker://registry.redhat.io/amq7/amq-streams-kafka-mirror-maker:1.1.0-kafka-2.1.1|docker://LOCAL/openshift/amq-streams-kafka-mirror-maker:1.1.0-kafka-2.1.1|

### Add trusted certificate for openshift image registry manipulations on distant machines
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

### Fuse images with Skopeo

```
srcreg="docker://registry.redhat.io/"
tag="1.1"
ns="fuse7/"
origpfx=fuse-
targetpfx=fuse7-
imglist="java-openshift console"

#Full list below
#imglist="java-openshift karaf-openshift eap-openshift console"

for img in $imglist
do
 echo $srcreg$ns$origpfx$img:$tag
 echo "|$srcreg$ns$origpfx$img:$tag|docker://LOCAL/openshift/$targetpfx$img:$tag|"
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
tag="7.3"
ns="amq-broker-7/"
imglist="amq-broker-73-openshift"

for img in $imglist
do
 echo $srcreg$ns$img:$tag
 echo "|$srcreg$ns$img:$tag|docker://LOCAL/openshift/$img:$tag|"
 skopeo copy --screds $user:$pass $srcreg$ns$img:$tag oci:./target:$ns$img:$tag
done

for img in $imglist
do
 echo docker://$REGISTRY/openshift/$img:$tag
 skopeo --insecure-policy copy --dest-creds=$ocuser:$octoken oci:./target:$ns$img:$tag docker://$REGISTRY/openshift/$img:$tag
done

```

Template images for AMQ 7 Broker

```
for template in amq-broker-73-basic.yaml \
amq-broker-73-ssl.yaml \
amq-broker-73-custom.yaml \
amq-broker-73-persistence.yaml \
amq-broker-73-persistence-ssl.yaml \
amq-broker-73-persistence-clustered.yaml \
amq-broker-73-persistence-clustered-ssl.yaml;
 do
 oc replace --force -f \
https://raw.githubusercontent.com/jboss-container-images/jboss-amq-7-broker-openshift-image/73-7.3.0.GA/templates/${template} -n openshift
 done
```

### AMQ Streams Strimzi images with Skopeo

Credentials to registry.redhat.io are required here to use these enterprise images.

```
srcreg="docker://registry.redhat.io/"
tag="1.1.0"
ns="amq7/"
imglist="amq-streams-cluster-operator amq-streams-topic-operator amq-streams-user-operator amq-streams-kafka-init amq-streams-zookeeper-stunnel amq-streams-kafka-stunnel amq-streams-entity-operator-stunnel"

for img in $imglist
do
 echo $srcreg$ns$img:$tag
 echo "|$srcreg$ns$img:$tag|docker://LOCAL/openshift/$img:$tag|"
 skopeo copy --screds $user:$pass $srcreg$ns$img:$tag oci:./target:$ns$img:$tag
done

for img in $imglist
do
 echo docker://$REGISTRY/openshift/$img:$tag
 skopeo --insecure-policy copy --dest-creds=$ocuser:$octoken oci:./target:$ns$img:$tag docker://$REGISTRY/openshift/$img:$tag
done
```

```
srcreg="docker://registry.redhat.io/"
tag="1.1.0-kafka-2.1.1"
ns="amq7/"
imglist="amq-streams-zookeeper amq-streams-kafka amq-streams-kafka-connect amq-streams-kafka-connect-s2i amq-streams-kafka-mirror-maker "

for img in $imglist
do
 echo $srcreg$ns$img:$tag
 echo "|$srcreg$ns$img:$tag|docker://LOCAL/openshift/$img:$tag|"
 skopeo copy --screds $user:$pass $srcreg$ns$img:$tag oci:./target:$ns$img:$tag
done

for img in $imglist
do
 echo docker://$REGISTRY/openshift/$img:$tag
 skopeo --insecure-policy copy --dest-creds=$ocuser:$octoken oci:./target:$ns$img:$tag docker://$REGISTRY/openshift/$img:$tag
done
```