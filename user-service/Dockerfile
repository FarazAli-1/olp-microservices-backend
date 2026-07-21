FROM eclipse-temurin:17-jre-alpine

# this creates a folder named /app inside the container and move into it
WORKDIR /app

# this takes the .jar file Maven just built from your computer and copy it into the container as app.jar
COPY target/*.jar app.jar

# this is the document that this container will communicate on port 8081.
EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]