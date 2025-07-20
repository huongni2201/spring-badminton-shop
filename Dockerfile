# Stage 1: Build with Maven
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Run jar with OpenJDK
FROM openjdk:21-jdk-slim
WORKDIR /app

COPY --from=build /app/target/badminton-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
