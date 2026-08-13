# Étape 1 : Construction de l'application avec Maven
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# On compile l'application en ignorant les tests pour aller plus vite
RUN mvn clean package -DskipTests

# Étape 2 : Exécution de l'application allégée
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# On récupère uniquement le fichier .jar généré à l'étape 1
COPY --from=build /app/target/*.jar app.jar

# On expose le port classique de Spring Boot
EXPOSE 8080

# Commande de démarrage
ENTRYPOINT ["java", "-jar", "app.jar"]