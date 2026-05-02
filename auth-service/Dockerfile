# lightweight JRE base image
FROM eclipse-temurin:17-jre-alpine

# Metadata
LABEL authors="Krishanu Goswami"

# Set working directory
WORKDIR /app

# Copy the built JAR file into the container
COPY target/auth-service-0.0.1-SNAPSHOT.jar app.jar

# Expose the application port
EXPOSE 8082

# Run the application
ENTRYPOINT ["java", "-Duser.timezone=UTC", "-jar", "app.jar"]