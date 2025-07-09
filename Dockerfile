# ---------- Stage 1: Maven Build ----------
FROM maven:3.9.5-eclipse-temurin-21-alpine AS builder
WORKDIR /app

# Copy only necessary files first (for layer caching)
COPY pom.xml .
COPY src ./src

# Build the application JAR
RUN mvn clean package -DskipTests

# ---------- Stage 2: Run the JAR ----------
# Use Eclipse Temurin JDK as base image (OpenJDK from Adoptium)
FROM amazoncorretto:21-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the executable JAR file into the container
COPY --from=builder /app/target/*.jar app.jar

# Expose port (match your Spring Boot server.port)
EXPOSE 8080

# Run the entrypoint file
ENTRYPOINT ["java", "-jar", "app.jar"]