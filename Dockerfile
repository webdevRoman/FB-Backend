# syntax=docker/dockerfile:1
#
# docker build -t fb-backend:1.0.0 .
# docker run --rm -p 8080:8080 fb-backend:1.0.0

# Stage 1: dev - full JDK: dependencies installation, compilation and jar build
FROM eclipse-temurin:25-jdk AS dev

WORKDIR /workspace

COPY gradlew settings.gradle build.gradle ./
COPY gradle/wrapper/ gradle/wrapper/

RUN chmod +x gradlew \
    && ./gradlew --no-daemon --console=plain dependencies

COPY config/ config/
COPY src/ src/

RUN ./gradlew --no-daemon --console=plain clean bootJar \
    && jar="$(find build/libs -name '*.jar' ! -name '*-plain.jar' | head -n 1)" \
    && cp -v "$jar" /workspace/app.jar

# Stage 2: prod - compact JRE-alpine. No dev/test dependencies, takes jar from dev stage
FROM eclipse-temurin:25-jre-alpine AS prod

ENV LANG=C.UTF-8
ENV LC_ALL=C.UTF-8
ENV TZ=Europe/Moscow

RUN apk add --no-cache tzdata \
    && ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone \
    && addgroup -S app \
    && adduser -S -G app app

WORKDIR /app

RUN mkdir -p /app/logs && chown -R app:app /app
COPY --from=dev --chown=app:app /workspace/app.jar /app/app.jar

EXPOSE 8080

USER app

ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0"

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
