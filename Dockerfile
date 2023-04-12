FROM bellsoft/liberica-openjdk-alpine:19.0.2
COPY target/*.jar /usr/local/lib/fb.jar
COPY target/*.env /usr/local/lib/version.env
EXPOSE 8080
USER root
ENV LANG     C.UTF-8
ENV LC_ALL   C.UTF-8
ENV LC_CTYPE C.UTF-8
ENV TZ=Europe/Moscow
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
RUN apk add --no-cache tzdata
ENTRYPOINT ["java","-jar","/usr/local/lib/fb.jar"]