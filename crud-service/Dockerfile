FROM eclipse-temurin:21-jdk-alpine as build
WORKDIR /workspace/app

COPY gradle gradle
COPY build.gradle settings.gradle gradlew ./
COPY crud-service/build.gradle crud-service/
COPY common-dto/build.gradle common-dto/

RUN ./gradlew dependencies

COPY . .
RUN ./gradlew :crud-service:build -x test

FROM eclipse-temurin:21-jre-alpine
VOLUME /tmp
COPY --from=build /workspace/app/crud-service/build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
