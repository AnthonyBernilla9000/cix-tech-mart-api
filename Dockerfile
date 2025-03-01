# Usa una imagen oficial de OpenJDK 11 (Debian Slim es más ligero)
FROM openjdk:11-jdk-slim

# Define el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copia el archivo JAR a la imagen
COPY target/*.jar app.jar

# Expone el puerto que usa Spring Boot (8080)
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
