# Stage 1: Build
FROM maven:3-eclipse-temurin-17-alpine AS build
WORKDIR /workspace/app

# Copy Maven settings and POM first
COPY pom.xml ./

# Download all dependencies and plugins
RUN --mount=type=cache,target=/root/.m2 mvn -B dependency:resolve-plugins dependency:resolve

# Copy source files and build
COPY ./src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -B -f ./pom.xml package -DskipTests

# Stage 2: Create custom JRE
FROM eclipse-temurin:17-jdk-alpine AS jre-build
WORKDIR /jre

# Create a custom JRE
RUN apk add --no-cache binutils && \
    jlink \
    --add-modules java.base,java.xml,java.naming,java.desktop,java.rmi,jdk.crypto.ec \
    --strip-debug \
    --no-man-pages \
    --no-header-files \
    --compress=2 \
    --output minimal-jre

# Stage 3: Runtime
FROM alpine:3.19
WORKDIR /app

# Copy custom JRE from jre-build stage
COPY --from=jre-build /jre/minimal-jre /opt/java/openjdk

# Create a non-root user
RUN addgroup -S spring && adduser -S spring -G spring && \
    # Create logs directory with correct permissions
    mkdir -p /app/logs && chown -R spring:spring /app

# Set environment variables
ENV PATH="/opt/java/openjdk/bin:${PATH}" \
    JAVA_TOOL_OPTIONS="-Xmx512m"

# Copy application files
COPY --from=build /workspace/app/target/VifaniaNotificationBot-1.0-SNAPSHOT.jar ./app.jar
COPY --from=build /workspace/app/target/lib /app/lib

# Use non-root user
USER spring:spring

EXPOSE 8080/tcp

ENTRYPOINT ["java", "-cp", "/app/app.jar:/app/lib/*", "org.scheduler.TelegramMessageScheduler"]
