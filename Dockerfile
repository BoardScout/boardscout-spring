FROM eclipse-temurin:21-jdk AS builder

WORKDIR /workspace
COPY gradlew settings.gradle build.gradle ./
COPY gradle ./gradle
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon

COPY src ./src
RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:21-jre

WORKDIR /app
RUN apt-get update \
    && apt-get install --yes --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/* \
    && useradd --system --uid 10001 spring
COPY --from=builder /workspace/build/libs/*.jar app.jar
USER spring

EXPOSE 8080
HEALTHCHECK --interval=10s --timeout=3s --start-period=20s --retries=5 \
    CMD curl --fail --silent http://127.0.0.1:8080/api/v1/health > /dev/null || exit 1

ENTRYPOINT ["java", "-jar", "/app/app.jar"]

