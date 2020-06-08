FROM openjdk:14-jdk-alpine
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
VOLUME cache
VOLUME src/main/resources/static
ENTRYPOINT ["java","-jar","/app.jar"]
EXPOSE 8080