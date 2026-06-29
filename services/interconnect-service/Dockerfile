FROM eclipse-temurin:21-jre-alpine
RUN apk add --no-cache curl
WORKDIR /app
COPY target/interconnect-service-1.0.0.jar app.jar
EXPOSE 9002
ENTRYPOINT ["java", "-jar", "app.jar"]
