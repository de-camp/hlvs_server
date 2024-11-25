FROM openjdk:17-jdk-slim

ADD build/libs/simple.war /app.war

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app.war"]