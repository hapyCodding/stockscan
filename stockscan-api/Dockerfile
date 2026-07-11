# 빌드는 이미지 안에서 끝나므로 로컬 JDK 없이도 docker build 가능.
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app
COPY gradlew settings.gradle.kts build.gradle.kts ./
COPY gradle ./gradle
RUN ./gradlew --no-daemon dependencies >/dev/null 2>&1 || true
COPY src ./src
RUN ./gradlew --no-daemon clean bootJar

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/build/libs/stockscan-api-0.1.0.jar app.jar
EXPOSE 8080
# DS220+ 메모리(2GB)에 맞춰 힙 제한
ENV JAVA_OPTS="-Xms128m -Xmx320m"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
