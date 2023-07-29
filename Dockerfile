FROM openjdk:17-oracle

COPY target/SpringQRBot-0.0.1-SNAPSHOT.jar myapp.jar

ENTRYPOINT ["java","-jar","/myapp.jar"]