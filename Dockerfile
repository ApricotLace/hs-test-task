FROM openjdk:8u181-alpine3.8

WORKDIR /

COPY target/hs-test-task-standalone.jar hs-test-task.jar
EXPOSE 7000

CMD java -jar hs-test-task.jar
