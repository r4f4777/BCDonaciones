FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/tfg-backend-0.0.1-SNAPSHOT.jar ./app.jar

# Diagnóstico
RUN jar tf app.jar | grep BcDonacionesApplication || echo "❌ Class not found in jar"

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
