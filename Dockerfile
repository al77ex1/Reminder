FROM openjdk:17-jdk-slim

# Указываем рабочую директорию внутри контейнера
WORKDIR /app

# Копируем JAR файл в контейнер
COPY out/artifacts/VifaniaNotificationBot_jar/VifaniaNotificationBot.jar /app/app.jar

ENV JAVA_TOOL_OPTIONS="-Xmx512m"

# Запуск приложения
CMD ["java", "-jar", "/app/app.jar"]