# ---- Build Stage ----
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /mindforge

COPY gradle gradle
COPY gradlew build.gradle.kts settings.gradle.kts ./
RUN ./gradlew dependencies --no-daemon || true

COPY src src
RUN ./gradlew bootJar --no-daemon

# ---- Runtime Stage ----
FROM gcr.io/distroless/java21-debian12:nonroot

WORKDIR /mindforge

COPY --from=builder /mindforge/build/libs/*.jar mindforge.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/mindforge/mindforge.jar"]
