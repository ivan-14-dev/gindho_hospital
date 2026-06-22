FROM maven:3.9.9-eclipse-temurin-21-alpine AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests -Dmaven.test.skip=true

FROM eclipse-temurin:21-jre-alpine

RUN apk add --no-cache curl

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 9250

HEALTHCHECK --interval=30s --timeout=5s --retries=3 \
  CMD curl -sf http://localhost:9250/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]