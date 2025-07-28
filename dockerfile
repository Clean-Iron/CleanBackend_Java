# 1) Etapa de compilación
FROM maven:3.10.1-openjdk-21 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests -B

# 2) Etapa de ejecución
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

# 3) Volumen para persistir logs
VOLUME ["/app/logs"]

# 4) Healthcheck contra el endpoint Actuator
HEALTHCHECK --interval=30s --timeout=5s --start-period=10s \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
