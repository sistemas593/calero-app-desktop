# Etapa 1: compilar el JAR
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY core core
COPY api api
RUN chmod +x gradlew && ./gradlew :api:bootJar --no-daemon

# Etapa 2: imagen final solo con JRE
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/api/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
