# quarkus-action-ms

- exemplo de build
````
quarkus ext add quarkus-container-image-docker

./mvnw clean package -DskipTests \
-Dquarkus.container-image.build=true \
-Dquarkus.container-image.push=true \
-Dquarkus.container-image.registry=quay.io \
-Dquarkus.container-image.group=fabricio211 \
-Dquarkus.container-image.name=reservation-service \
-Dquarkus.container-image.tag=1.0.0-SNAPSHOT

ou

 ./mvnw clean package -DskipTests -Dquarkus.container-image.build=true -Dquarkus.container-image.push=true
 
 ./mvnw clean package -Dquarkus.kubernetes.deploy=true
````

- para usar sandbox do openshi
```
oc project fabricio-jacob-dev
oc policy who-can get services
oc login --token=xxxx --server=https://xxxx.p1.openshiftapps.com:6443
oc adm policy add-role-to-user admin fabricio-jacob -n fabricio-jacob-dev
oc apply -f target/kubernetes/openshift.yml

https://console-openshift-console.apps.sandbox-m2.ll9k.p1.openshiftapps.com/q/graphql-ui/
```