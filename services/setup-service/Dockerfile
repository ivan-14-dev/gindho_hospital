FROM eclipse-temurin:21-jre-alpine
RUN apk add --no-cache curl
WORKDIR /app

RUN apk add --no-cache curl ca-certificates && \
    curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl" && \
    install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl && \
    rm kubectl

COPY target/*.jar app.jar
EXPOSE 9030
ENTRYPOINT ["java", "-jar", "app.jar"]