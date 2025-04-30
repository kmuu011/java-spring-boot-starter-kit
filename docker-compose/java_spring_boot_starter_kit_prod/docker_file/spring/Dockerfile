FROM gradle:8-jdk17 AS builder
WORKDIR /app

COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle
RUN ./gradlew --no-daemon buildDependentsClasses || true

COPY src ./src
RUN ./gradlew --no-daemon bootJar

FROM eclipse-temurin:17-jre-jammy
ARG JAR_FILE=/app/build/libs/*.jar

RUN addgroup --system appgroup && adduser --system appuser --ingroup appgroup

COPY --from=builder ${JAR_FILE} /app/app.jar
USER appuser

ENTRYPOINT ["java","-jar","/app/app.jar","--spring.profiles.active=prod"]
