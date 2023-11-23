FROM openjdk:17-jdk-alpine
ARG JAR-FILE=target/*.jar
COPY ./target/Medbot-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar","/app.jar"]