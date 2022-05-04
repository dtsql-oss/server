# build stage
FROM openjdk:17-jdk-alpine AS build

COPY . /src

WORKDIR /src
RUN ./mvnw -Dcheckstyle.skip -DskipTests -DcreateChecksum=true clean package

# production stage
FROM openjdk:17-jdk-alpine

COPY --from=build /src/service/target/service-*.jar /service-app.jar

ENTRYPOINT ["java", "-jar", "/service-app.jar"]