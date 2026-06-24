FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY target/interconnect-service-1.0.0.jar app.jar
EXPOSE 9002
ENTRYPOINT ["java", "-jar", "app.jar"]
