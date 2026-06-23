FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copier le POM parent et les modules requis pour résoudre les dépendances
COPY pom.xml .
COPY services/common/pom.xml services/common/
COPY services/common/src services/common/src
COPY services/setup-service/pom.xml services/setup-service/
COPY services/setup-service/src services/setup-service/src

# Build via reactor Maven pour que le parent POM soit résolu localement
RUN mvn -f pom.xml -pl services/setup-service -am clean package \
    -DskipTests -Dmaven.test.skip=true -B -e -Dorg.slf4j.simpleLogger.showDateTime=true

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN apk add --no-cache curl ca-certificates && \
    curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl" && \
    install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl && \
    rm kubectl

COPY --from=build /app/services/setup-service/target/setup-service-1.0.0.jar app.jar
EXPOSE 9002
ENTRYPOINT ["java", "-jar", "app.jar"]