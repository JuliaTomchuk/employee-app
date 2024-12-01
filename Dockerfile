FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY target/employee-0.0.1-SNAPSHOT.jar /app/application.jar
ENTRYPOINT ["java","-jar","/app/application.jar","--spring.profiles.active=docker"]