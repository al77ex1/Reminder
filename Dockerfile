# Stage 1: Build
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /workspace/app

# Copy Maven settings and POM first
COPY pom.xml ./

# Download all dependencies and plugins
RUN --mount=type=cache,target=/root/.m2 mvn -B dependency:resolve-plugins dependency:resolve

# Copy source files and build
COPY ./src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -B -f ./pom.xml package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Create logs directory
RUN mkdir -p /app/logs

# Copy the JAR file and dependencies from the build stage
COPY --from=build /workspace/app/target/VifaniaNotificationBot-1.0-SNAPSHOT.jar ./app.jar
COPY --from=build /workspace/app/target/lib /app/lib

ENV JAVA_TOOL_OPTIONS="-Xmx512m"

EXPOSE 8080/tcp

ENTRYPOINT ["java", "-cp", "/app/app.jar:/app/lib/*", "org.scheduler.TelegramMessageScheduler"]
