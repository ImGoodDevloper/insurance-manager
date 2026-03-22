FROM maven:3.9.14-eclipse-temurin-17-alpine AS build

WORKDIR /springboot-app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17.0.18_8-jre-noble
WORKDIR /springboot-app

COPY --from=build /springboot-app/target/insurance-manage-service-0.0.1-SNAPSHOT.jar app.jar

#目前還不會用到時區，但還是先設定一下
ENV TZ=Asia/Taipei
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

EXPOSE 8080

ENTRYPOINT ["java", "-Xmx512m", "-Xms256m", "-jar", "app.jar"]