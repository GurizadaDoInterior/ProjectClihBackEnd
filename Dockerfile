FROM eclipse-temurin:24-jdk AS build
WORKDIR /workspace
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -B -DskipTests dependency:go-offline
COPY src/ src/
RUN ./mvnw -B -DskipTests package

FROM eclipse-temurin:24-jre
WORKDIR /app
RUN useradd --system --uid 10001 spring
COPY --from=build /workspace/target/clih-api-*.jar app.jar
USER spring
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
