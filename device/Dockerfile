#FROM maven:3.6.3-openjdk-11 AS builder
#
#COPY ./src/ /root/src
#COPY ./pom.xml /root/
#
#WORKDIR /root
#
#RUN mvn package -Dmaven.test.skip=true
#RUN java -Djarmode=layertools -jar /root/target/ds-2020-0.0.1-SNAPSHOT.jar list
#RUN java -Djarmode=layertools -jar /root/target/ds-2020-0.0.1-SNAPSHOT.jar extract
#RUN ls -l /root
#
#FROM openjdk:11.0.6-jre
#
#ENV TZ=UTC
##ENV DB_IP=localhost
#ENV DB_IP=host.docker.internal
#
#
#ENV DB_PORT=3306
#ENV DB_USER=root
#ENV DB_PASSWORD=root
#ENV DB_DBNAME=device_db
#
#COPY --from=builder /root/dependencies/ ./
#COPY --from=builder /root/snapshot-dependencies/ ./
#
#RUN sleep 10
#COPY --from=builder /root/spring-boot-loader/ ./
#COPY --from=builder /root/application/ ./
#ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher","-XX:+UseContainerSupport -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -XX:MaxRAMFraction=1 -Xms512m -Xmx512m -XX:+UseG1GC -XX:+UseSerialGC -Xss512k -XX:MaxRAM=72m"]



FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY target/ds-2020-0.0.1-SNAPSHOT.jar /app/app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/app/app.jar", "--server.port=8081"]



