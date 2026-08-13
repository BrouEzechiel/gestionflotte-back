# Étape 1 : Construction de l'application avec Maven
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src

# NOUVEAU : Limiter la RAM de Maven à 256 Mo pour ne pas faire planter Render
ENV MAVEN_OPTS="-Xmx256m"

# On compile l'application en ignorant les tests
RUN mvn clean package -DskipTests

# Étape 2 : Exécution de l'application allégée
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]