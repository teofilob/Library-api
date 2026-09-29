FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /workspace
COPY pom.xml .
COPY src ./src
RUN mvn -B -ntp clean verify

FROM eclipse-temurin:25-jre-noble
WORKDIR /app
RUN groupadd --system library && useradd --system --gid library library \
    && mkdir /data && chown library:library /data
COPY --from=build /workspace/target/Library-0.0.1-SNAPSHOT.jar /app/library.jar
USER library
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/library.jar"]
