FROM maven:3.9-eclipse-temurin-21-alpine AS build

WORKDIR /app

COPY pom.xml .
COPY src src

RUN mvn -q -DskipTests package

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# Keep heap small for Railway trial / low-memory containers
ENV JAVA_OPTS="-XX:MaxRAMPercentage=60.0 -Xms64m -Xmx256m"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
