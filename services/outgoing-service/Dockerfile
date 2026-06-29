FROM eclipse-temurin:21-jre-alpine
RUN apk add --no-cache curl
WORKDIR /app
COPY target/outgoing-service-*.jar app.jar
EXPOSE 8110
ENTRYPOINT ["java", "-jar", "app.jar"]