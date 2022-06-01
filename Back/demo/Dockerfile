# define base docker image
FROM openjdk:11
LABEL maintainer="javaguides.net"
ADD target/demo-0.0.1-SNAPSHOT.jar demo.jar
ENTRYPOINT ["java", "-jar", "demo.jar"]