FROM openjdk:8-jre
VOLUME /tmp
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","${JAVA_OPTS}","-jar","/app.jar"]
