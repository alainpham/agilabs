```
unzip amq-online-install-1-2_1.zip
```

```
sed -i "s/registry.redhat.io\/amq7\//docker-registry.default.svc:5000\/openshift\//" install/bundles/amq-online/050-Deployment*

sed -i "s/registry.redhat.io\/amq7\//docker-registry.default.svc:5000\/openshift\//" install/bundles/amq-online/050-Deployment*
sed -i "s/openshift\/oauth-proxy.latest/docker-registry.default.svc:5000\/openshift\/oauth-proxy:v3.11/" install/bundles/amq-online/050-Deployment*
sed -i "s/registry.redhat.io\/redhat-sso-7\/sso73-openshift:latest/docker-registry.default.svc:5000\/openshift\/sso73-openshift:1.0/" install/bundles/amq-online/050-Deployment*



```

Optional
```
sed -i 's/amq-online-infra/amq-online/' install/bundles/amq-online/*.yaml
```
