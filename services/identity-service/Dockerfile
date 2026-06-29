FROM eclipse-temurin:21-jre-alpine
RUN apk add --no-cache curl bash
WORKDIR /app
COPY target/*.jar app.jar
COPY ./.env .env
COPY docker-entrypoint.sh /usr/local/bin/
ENTRYPOINT ["docker-entrypoint.sh"]
EXPOSE 8080
