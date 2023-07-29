FROM openjdk:17-alpine

RUN mkdir -p /opt/nicerpricer/backend
WORKDIR /opt/nicerpricer/backend
ADD build/libs/backend-0.0.1-SNAPSHOT.jar /opt/nicerpricer/backend/nicerpricer.jar

ENTRYPOINT ["java", "-jar", "nicerpricer.jar"]