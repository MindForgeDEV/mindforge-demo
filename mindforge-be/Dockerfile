# Multi-stage Docker build for MindForge Backend
# This Dockerfile uses a pre-built JAR approach for faster builds
#
# Build process:
# 1. Build JAR locally: ./gradlew bootJar -x test
# 2. Build Docker image: docker build -t mindforge-be .
#
# This approach avoids timeout issues during Docker builds by
# leveraging local Gradle cache and dependency resolution.

FROM gcr.io/distroless/java21-debian12:nonroot

WORKDIR /app

# Copy pre-built JAR file (build locally first)
COPY build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
