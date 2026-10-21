# Контекст сборки — корень репозитория (pom.xml лежит там, исходники в backend/).

FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml .
COPY backend ./backend
# Тесты пропускаем: DataInitTest — это SpringBootTest, которому нужны живые БД и MinIO.
RUN --mount=type=cache,target=/root/.m2 mvn -B -q -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /app
RUN useradd --system --uid 10001 --no-create-home app
COPY --from=build /build/target/boardgames-backend-*.jar /app/app.jar
USER app
EXPOSE 8080
# Параметры JVM можно задать переменной JAVA_TOOL_OPTIONS (например, -Xmx512m).
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
