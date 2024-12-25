# Use the official OpenJDK 21 image
FROM openjdk:21

# Set the working directory
WORKDIR /app

# Copy the built application JAR file into the container
COPY target/stockwise-backend-0.0.1-SNAPSHOT.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
