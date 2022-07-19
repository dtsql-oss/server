# build stage
FROM maven:3-openjdk-17 AS dependencies

WORKDIR /src

COPY pom.xml pom.xml
COPY service/pom.xml service/
COPY storage/pom.xml storage/
COPY infrastructure/pom.xml infrastructure/
COPY implementation/pom.xml implementation/
COPY test-util/pom.xml test-util/
COPY client/pom.xml client/

RUN mvn -B dependency:go-offline

FROM dependencies as build

COPY . /src

RUN mvn -Dcheckstyle.skip -DskipTests -DcreateChecksum=true clean package

# production stage
FROM openjdk:17 as release

COPY --from=build /src/service/target/service-*.jar /service-app.jar

ENTRYPOINT ["java", "-jar", "/service-app.jar"]