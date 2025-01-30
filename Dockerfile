FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /workspace/app

# Copy configuration files first to cache dependencies
COPY pom.xml ./

# Configure Maven repository and download dependencies
RUN mvn -f ./pom.xml dependency:resolve dependency:resolve-plugins

# Copy source files needed for compilation
COPY ./src ./src

# Build the application
RUN mvn -f ./pom.xml package -DskipTests

# Use a new stage for the runtime
FROM openjdk:17-jdk-slim
WORKDIR /app

# Create logs directory
RUN mkdir -p /app/logs

# Copy the JAR file and dependencies from the build stage
COPY --from=build /workspace/app/target/VifaniaNotificationBot-1.0-SNAPSHOT.jar ./app.jar
COPY --from=build /workspace/app/target/lib /app/lib

ENV JAVA_TOOL_OPTIONS="-Xmx512m"

EXPOSE 8080/tcp

ENTRYPOINT ["java", "-cp", "/app/app.jar:/app/lib/*", "org.scheduler.TelegramMessageScheduler"]
