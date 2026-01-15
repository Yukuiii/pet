FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# 设置时区为上海
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

RUN useradd -u 10001 -m app
COPY target/*.jar /app/app.jar
EXPOSE 8080
ENV JAVA_OPTS=""
USER app
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]

