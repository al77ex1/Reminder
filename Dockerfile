# Stage 1: Build and JRE creation stage
FROM maven:3-eclipse-temurin-17-alpine AS build
WORKDIR /workspace/app

# Create .m2 directory to cache dependencies
RUN mkdir -p /root/.m2

# Copy Maven settings and POM first
COPY pom.xml ./

# Download all dependencies and plugins
RUN --mount=type=cache,target=/root/.m2 mvn -B dependency:resolve-plugins dependency:resolve

# Copy source files and build
COPY ./src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -B -f ./pom.xml package -DskipTests

# Create custom JRE
WORKDIR /jre
RUN "$JAVA_HOME/bin/jlink" \
    --add-modules java.base,java.xml,java.naming,java.desktop,java.rmi,jdk.crypto.ec \
    --strip-debug \
    --no-man-pages \
    --no-header-files \
    --compress=2 \
    --output minimal-jre

# Stage 2: Final stage
FROM alpine:3.21
WORKDIR /app

# Copy custom JRE and application from build stage
COPY --from=build /jre/minimal-jre /opt/java/openjdk
COPY --from=build /workspace/app/target/VifaniaNotificationBot-1.0-SNAPSHOT.jar ./app.jar

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring && \
    mkdir -p /app/logs && chown -R spring:spring /app

# Set environment variables
ENV TZ=Europe/Moscow \
    PATH="/opt/java/openjdk/bin:${PATH}" \
    JAVA_TOOL_OPTIONS="-Xmx512m"

# Use non-root user
USER spring

# Run the application with specific main class
EXPOSE 8080/tcp

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
