# Stage 1: Build and JRE creation stage
FROM maven:3-eclipse-temurin-17-alpine
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
RUN "$JAVA_HOME/bin/jlink" \
    --add-modules java.base,java.xml,java.naming,java.desktop,java.rmi,jdk.crypto.ec \
    --strip-debug \
    --no-man-pages \
    --no-header-files \
    --compress=2 \
    --output /custom-jre

# Stage 2: Final stage
FROM alpine:3.21
ENV JAVA_HOME=/opt/java/openjdk
ENV PATH=$JAVA_HOME/bin:${PATH}
WORKDIR /app

# Copy custom JRE
COPY --from=0 /custom-jre $JAVA_HOME

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring

# Set environment variables
ENV TZ=Europe/Moscow
ENV JAVA_TOOL_OPTIONS="-Xmx512m"

# Copy application files
COPY --from=0 /workspace/app/target/VifaniaNotificationBot-1.0-SNAPSHOT.jar ./app.jar
COPY --from=0 /workspace/app/target/lib /app/lib

# Use non-root user
USER spring:spring

# Run the application with specific main class
EXPOSE 8080/tcp

ENTRYPOINT ["java", "-cp", "/app/app.jar:/app/lib/*", "org.scheduler.TelegramMessageScheduler"]
