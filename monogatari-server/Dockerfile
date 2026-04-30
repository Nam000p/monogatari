FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

COPY src/main/resources/keystore.p12 src/main/resources/keystore.p12

RUN mkdir -p /app/uploads && chmod 777 /app/uploads

EXPOSE 8443
ENTRYPOINT ["java", "-jar", "app.jar"]