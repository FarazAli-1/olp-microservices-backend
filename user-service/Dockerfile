# 1. Base Image: Start with a lightweight Linux OS that already has Java 17 installed.
FROM eclipse-temurin:17-jre-alpine

# 2. Workspace: Create a folder named /app inside the container and move into it.
WORKDIR /app

# 3. Copy: Take the .jar file Maven just built from your computer and copy it into the container as 'app.jar'.
COPY target/*.jar app.jar

# 4. Expose: Document that this container will communicate on port 8081.
EXPOSE 8081

# 5. Run: The actual terminal command to start your Spring Boot application when the container boots up.
ENTRYPOINT ["java", "-jar", "app.jar"]