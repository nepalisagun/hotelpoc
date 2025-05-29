# Build stage
FROM gradle:8.5-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle :booking-service:build --no-daemon

# Run stage
FROM eclipse-temurin:21-jre-jammy

# Add non-root user for security
RUN groupadd -r hotel && useradd -r -g hotel hotel

# Set working directory
WORKDIR /app

# Copy the built artifact from build stage
COPY --from=build /app/booking-service/build/libs/*.jar app.jar

# Create directory for SSL certificates and set permissions
RUN mkdir -p /app/certs && \
    chown -R hotel:hotel /app

# Switch to non-root user
USER hotel

# Set environment variables
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
ENV SPRING_PROFILES_ACTIVE=prod

# Expose the application port
EXPOSE 8082

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8082/actuator/health || exit 1

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
