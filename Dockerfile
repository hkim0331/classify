FROM openjdk:8-alpine

COPY target/uberjar/classify.jar /classify/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/classify/app.jar"]
