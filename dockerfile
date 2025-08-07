# 1) Etapa de compilación con Maven + JDK 21 válido
FROM maven:3-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY . .
RUN mvn clean package -DskipTests -B

# 2) Etapa de ejecución sobre un JDK 21 ligero
FROM openjdk:21-jdk-slim
WORKDIR /app

# Copia del JAR compilado
COPY --from=builder /app/target/*.jar app.jar

# Volumen para persistir logs
VOLUME ["/app/logs"]

# Healthcheck contra el endpoint de Spring Boot Actuator
HEALTHCHECK --interval=30s --timeout=5s --start-period=10s \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
