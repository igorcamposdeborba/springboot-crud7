FROM ubuntu:20.04
RUN apt-get update
RUN apt-get install -y default-jre
COPY target/registration.jar /tmp/registration.jar
ENTRYPOINT ["java", "-jar", "/tmp/registration.jar"]