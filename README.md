# Finance Balance
Бэкенд приложения для учёта финансов.

[ТЗ](https://docs.google.com/document/d/1hSrP6bB5H2z6K19xh1zEHrUDgoEkbCol_JL8K90-u1c)

## Требования
* Java 25
* [Gradle v9.1.0+](https://gradle.org/releases/)
* Другие инфраструктурные объекты
    * PostgreSQL:18.0+

## Инструкция по сборке

#### С использованием докера
1. Выполнить `gradle clean build`
3. В композ добавить сервис:
    ```
      service_name:
        container_name: service_container_name
        build:
          dockerfile: path\to\Dockerfile
          context: path\to\root\dir
   ```

#### Без использования докера
В IntelliJ IDEA добавить конфигурацию для Spring Boot приложения.<br/>
В качестве SDK указать Java 25, Spring Boot Class = ru.rgrabelnikov.fbbackend.FbBackendApplication, Active Profiles = dev

## Инструкция по разворачиванию (TODO)
1. В БД создать пользователя и схему с помощью скрипта [create_schema.sql](db-setup/create_schema.sql).
2. Установить переменные окружения.
3. Запустить сервис.

## Swagger
Swagger UI доступен при активном профиле Spring `LOCAL` по [ссылке](http://localhost:8080/api/v1/finance-balance/webjars/swagger-ui/index.html).

## Описание переменных окружения

| Переменная                     | Обязательность | Описание                                                                                                                                                                                                           | Пример                                                                                               | Дефолтное значение                                                                                   |
|--------------------------------|----------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------|
| SPRING_R2DBC_URL               | Да             | URL Postgres                                                                                                                                                                                                       | r2dbc:postgresql://localhost:5432/fb-db                                                              |                                                                                                      |
| SPRING_R2DBC_USERNAME          | Да             | Пользователь Postgres                                                                                                                                                                                              | fb                                                                                                   |                                                                                                      |
| SPRING_R2DBC_PASSWORD          | Да             | Пароль Postgres                                                                                                                                                                                                    | fb-pwd                                                                                               |                                                                                                      |
| SPRING_FLYWAY_URL              | Да             | URL Postgres для миграций                                                                                                                                                                                          | jdbc:postgresql://localhost:5432/fb-db                                                               |                                                                                                      |
| SPRING_FLYWAY_USERNAME         | Да             | Пользователь Postgres для миграций                                                                                                                                                                                 | fb                                                                                                   |                                                                                                      |
| SPRING_FLYWAY_PASSWORD         | Да             | Пароль Postgres для миграций                                                                                                                                                                                       | fb-pwd                                                                                               |                                                                                                      |
| LOGGING_LEVEL_ROOT             | Нет            | Уровень логов логера Root                                                                                                                                                                                          | DEBUG                                                                                                | INFO                                                                                                 |
| LOGGING_LOG4J2_CONFIG_OVERRIDE | Да             | Переопределить расположение конфига log4j2                                                                                                                                                                         | classpath:log4j2.xml                                                                                 |                                                                                                      |
| FB_JWT_SECRET                  | Да             | Строка для формирования JWT                                                                                                                                                                                        | u6FWEJ8T1Ein65XffiaxyAc1dikva3HJSdVIDXvUCxaubnionZVHwal2SmozDCL5HJvep9Ypjik619zwS9GkJKGJwKWOT2QuEUVB | u6FWEJ8T1Ein65XffiaxyAc1dikva3HJSdVIDXvUCxaubnionZVHwal2SmozDCL5HJvep9Ypjik619zwS9GkJKGJwKWOT2QuEUVB |
| FB_JWT_EXPIRATION              | Да             | Время "протухания" токена ([поддерживаемые единицы](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config.typesafe-configuration-properties.conversion.durations)) | 6h                                                                                                   | 8h                                                                                                   |
