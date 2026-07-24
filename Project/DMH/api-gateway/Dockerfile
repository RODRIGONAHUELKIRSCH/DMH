# ============================================================
# Dockerfile para api-gateway (versión simple - usa JAR pre-compilado)
# Servicio: API Gateway (Spring Cloud Gateway - WebFlux)
# Puerto: 8083
# Java: 25
# Build context esperado: ./Project/DMH (carpeta padre)
# ============================================================

FROM eclipse-temurin:25-jre-alpine

# Metadatos
LABEL maintainer="DMH Team" \
      description="API Gateway para la billetera digital DMH" \
      version="0.0.1-SNAPSHOT"

# Crear usuario no-root para mayor seguridad
RUN addgroup -S dmh && adduser -S dmh -G dmh

WORKDIR /app

# Copiar el JAR ejecutable (Spring Boot fat JAR) directamente desde el target local
COPY api-gateway/target/api-gateway-0.0.1-SNAPSHOT.jar app.jar

# Cambiar ownership al usuario no-root
RUN chown -R dmh:dmh /app
USER dmh

# Exponer el puerto del API Gateway
EXPOSE 8083

# Variables de entorno configurables
ENV JAVA_OPTS="" \
    SERVER_PORT=8083

# Healthcheck contra el endpoint de actuator
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget -qO- http://localhost:8083/actuator/health || exit 1

# Punto de entrada
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar --server.port=${SERVER_PORT}"]
