# ============================================================
# Dockerfile para api-eureka (versión simple - usa JAR pre-compilado)
# Servicio: Eureka Server (Service Discovery)
# Puerto: 8761
# Java: 25
# Build context esperado: ./Project/DMH (carpeta padre)
# ============================================================

FROM eclipse-temurin:25-jre-alpine

# Metadatos
LABEL maintainer="DMH Team" \
      description="Eureka Server para service discovery de la billetera digital DMH" \
      version="0.0.1-SNAPSHOT"

# Crear usuario no-root para mayor seguridad
RUN addgroup -S dmh && adduser -S dmh -G dmh

WORKDIR /app

# Copiar el JAR ejecutable (Spring Boot fat JAR) directamente desde el target local
COPY api-eureka/target/api-eureka-0.0.1-SNAPSHOT.jar app.jar

# Cambiar ownership al usuario no-root
RUN chown -R dmh:dmh /app
USER dmh

# Exponer el puerto de Eureka Server
EXPOSE 8761

# Variables de entorno configurables
ENV JAVA_OPTS="" \
    SERVER_PORT=8761

# Healthcheck contra el endpoint de Eureka
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget -qO- http://localhost:8761/actuator/health || exit 1

# Punto de entrada
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar --server.port=${SERVER_PORT}"]
