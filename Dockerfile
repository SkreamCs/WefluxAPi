FROM openjdk:17-slim

WORKDIR /app

COPY build/libs/Webflux-0.0.1-SNAPSHOT.jar /app/Webflux-0.0.1-SNAPSHOT.jar

EXPOSE 8080

CMD ["java", "-jar", "Webflux-0.0.1-SNAPSHOT.jar"]