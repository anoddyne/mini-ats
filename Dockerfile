# Первый этап: сборка приложения
FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /build

# Копируем файлы с зависимостями для их кэширования
COPY pom.xml .
RUN mvn dependency:go-offline

# Копируем исходный код и собираем приложение
COPY src ./src
RUN mvn clean package -DskipTests

# Второй этап: запуск приложения
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Копируем собранный jar-файл из первого этапа
COPY --from=builder /build/target/*.jar app.jar

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]