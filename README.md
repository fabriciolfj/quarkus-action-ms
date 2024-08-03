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
````