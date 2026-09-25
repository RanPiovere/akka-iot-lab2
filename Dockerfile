FROM eclipse-temurin:17-jre-jammy

RUN useradd --create-home --uid 10001 akka
WORKDIR /opt/akka-iot

COPY target/scala-2.13/akka-iot-service.jar app.jar

USER akka
EXPOSE 25520

ENTRYPOINT ["java", "-Dfile.encoding=UTF-8", "-jar", "/opt/akka-iot/app.jar"]
