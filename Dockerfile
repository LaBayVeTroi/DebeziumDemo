FROM openjdk:8-jdk-alpine
WORKDIR /usr/app
COPY ./target/DebeziumDemo-1.0-SNAPSHOT-jar-with-dependencies.jar .
CMD java -jar /usr/app/DebeziumDemo-1.0-SNAPSHOT-jar-with-dependencies.jar