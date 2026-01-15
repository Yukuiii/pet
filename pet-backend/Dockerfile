FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
COPY src ./src
RUN mvn -DskipTests package

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
RUN useradd -u 10001 -m app
COPY --from=build /workspace/target/*.jar /app/app.jar
EXPOSE 8080
ENV JAVA_OPTS=""
USER app
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]

