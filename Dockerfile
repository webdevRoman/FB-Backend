# syntax=docker/dockerfile:1
#
# Multi-stage build для Finance Balance Backend (Spring Boot / Gradle / Java 25).
#
# Dockerfile лежит в корне проекта, все пути ниже указаны относительно корня репозитория —
# сборка выполняется из него же (build context должен содержать build.gradle, gradle/wrapper и src):
#   docker build -t fb-backend:1.0.0 .
#   docker run --rm -p 8080:8080 fb-backend:1.0.0

# --------------------------------------------------------------------------------------------------
# Stage 1: dev — полный JDK (не -slim/-alpine): установка зависимостей, компиляция и сборка jar.
# Всё, что нужно только для сборки (JDK, Gradle, test/dev зависимости), остаётся в этой стадии.
# --------------------------------------------------------------------------------------------------
FROM eclipse-temurin:25-jdk AS dev

WORKDIR /workspace

# 1. Копируем ТОЛЬКО файлы, описывающие зависимости (объявление плагина Gradle Wrapper
#    находится в build.gradle, сам wrapper — в gradle/wrapper).
#    Пока эти файлы не меняются, следующий ниже слой с установленными зависимостями
#    берётся из кэша Docker. Если скопировать весь проект одной инструкцией,
#    любое изменение в src инвалидировало бы слой с зависимостями.
COPY gradlew settings.gradle build.gradle ./
COPY gradle/wrapper/ gradle/wrapper/

# 2. Установка (разрешение) зависимостей в Gradle-кэш — исходники для этого не нужны.
RUN chmod +x gradlew \
    && ./gradlew --no-daemon --console=plain dependencies

# 3. Только теперь копируем остальной исходный код: изменения кода пересобирают лишь этот слой.
COPY config/ config/
COPY src/ src/

# 4. Компиляция и сборка исполняемого Spring Boot jar (тесты не запускаются,
#    *-plain.jar в расчёт не берётся).
RUN ./gradlew --no-daemon --console=plain clean bootJar \
    && jar="$(find build/libs -name '*.jar' ! -name '*-plain.jar' | head -n 1)" \
    && cp -v "$jar" /workspace/app.jar

# --------------------------------------------------------------------------------------------------
# Stage 2: prod — компактный JRE-alpine. Никаких dev/test зависимостей и инструментов сборки:
# из стадии dev забирается только собранный jar (COPY --from=dev), а не весь build context.
# --------------------------------------------------------------------------------------------------
FROM eclipse-temurin:25-jre-alpine AS prod

ENV LANG=C.UTF-8
ENV LC_ALL=C.UTF-8
ENV TZ=Europe/Moscow
# Единственный runtime-пакет: таймзоны (нужны логам и расписаниям).
RUN apk add --no-cache tzdata \
    && ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone \
    && addgroup -S app \
    && adduser -S -G app app

WORKDIR /app
# ./logs нужен файловому appender'у из log4j2.xml и должен быть доступен
# непривилегированному пользователю, под которым запускается приложение.
RUN mkdir -p /app/logs && chown -R app:app /app
COPY --from=dev --chown=app:app /workspace/app.jar /app/app.jar

# Порт берётся из src/main/resources/application.yml (server.port=8080).
EXPOSE 8080

USER app

# JAVA_OPTS позволяет переопределить JVM-настройки при запуске контейнера.
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0"

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
