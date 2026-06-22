FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY target/outgoing-service-*.jar app.jar
EXPOSE 8110
ENTRYPOINT ["java", "-jar", "app.jar"]