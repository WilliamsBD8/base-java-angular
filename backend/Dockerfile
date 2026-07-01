FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY .mvn .mvn
COPY mvnw .
COPY pom.xml .

RUN chmod +x mvnw
RUN for i in 1 2 3; do ./mvnw -B dependency:resolve dependency:resolve-plugins && break || sleep 10; done

COPY src src
RUN for i in 1 2 3; do ./mvnw -B compile -DskipTests && break || sleep 10; done

EXPOSE 8080

CMD ["./mvnw", "spring-boot:run"]