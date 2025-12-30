## Базовый образ с JDK 21
#FROM eclipse-temurin:21-jdk
#
## Рабочая директория внутри контейнера
#WORKDIR /app
#
## Копируем собранный jar
#COPY target/bank-service-0.0.1-SNAPSHOT.jar app.jar
#
## Открываем порт приложения
#EXPOSE 8080
#
## Команда для запуска
#ENTRYPOINT ["java","-Dspring.profiles.active=dev","-Dlogging.level.root=DEBUG","-jar","app.jar"]
#
