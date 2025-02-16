# Базовый образ с Gradle для сборки
FROM gradle:8.4-jdk17 AS builder
WORKDIR /app

# Копируем Gradle wrapper и файлы сборки
COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle build.gradle
COPY settings.gradle settings.gradle

# Загружаем зависимости (кэшируется)
RUN ./gradlew dependencies --no-daemon

# Копируем исходный код и собираем JAR
COPY src src
RUN ./gradlew clean build -x test --no-daemon

# Финальный минимальный образ с JRE
FROM eclipse-temurin:17-jre
WORKDIR /app

# Копируем собранный JAR из builder-а
COPY --from=builder /app/build/libs/*.jar backend.jar

# Запуск приложения
ENTRYPOINT ["java", "-jar", "backend.jar"]
