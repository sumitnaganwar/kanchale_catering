FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /build

COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B -DskipTests package

FROM openjdk:17-jdk-slim AS runtime
WORKDIR /app

RUN groupadd --system spring && useradd --system --gid spring spring \
    && mkdir -p /app/uploads \
    && chown -R spring:spring /app

ENV UPLOAD_DIR=/app/uploads
EXPOSE 8080

COPY --from=build --chown=spring:spring /build/target/catering-0.0.1-SNAPSHOT.jar /app/app.jar
USER spring:spring

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
