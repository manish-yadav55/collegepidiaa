# Stage 1: Build the application using Maven with Amazon Corretto
FROM maven:3.9.0-amazoncorretto-17 AS builder
WORKDIR /app

# Copy pom.xml to download dependencies first
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the source code and build the JAR
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Create the final runtime image
FROM amazoncorretto:17-alpine
WORKDIR /app

# Copy the built JAR from the builder stage
COPY --from=builder /app/target/collegepidia-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

#ENTRYPOINT ["java", "-Dserver.port=$PORT", "-jar", "app.jar"]
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:-8080} -jar app.jar"]
