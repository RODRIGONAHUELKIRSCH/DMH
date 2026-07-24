# ============================================================
# Dockerfile para api-user (versión simple - usa JAR pre-compilado)
# Servicio: User API (Spring Boot - Web MVC + JPA + Keycloak)
# Puerto: 8081
# Java: 25
# Build context esperado: ./Project/DMH (carpeta padre)
# ============================================================

FROM eclipse-temurin:25-jre-alpine

# Metadatos
LABEL maintainer="DMH Team" \
      description="Microservicio de usuarios para la billetera digital DMH" \
      version="0.0.1-SNAPSHOT"

# Crear usuario no-root para mayor seguridad
RUN addgroup -S dmh && adduser -S dmh -G dmh

WORKDIR /app

# Copiar el JAR ejecutable (Spring Boot fat JAR) directamente desde el target local
COPY api-user/target/api-user-0.0.1-SNAPSHOT.jar app.jar

# Cambiar ownership al usuario no-root
RUN chown -R dmh:dmh /app
USER dmh

# Exponer el puerto del servicio de usuarios
EXPOSE 8081

# Variables de entorno configurables
ENV JAVA_OPTS="" \
    SERVER_PORT=8081

# Healthcheck contra el endpoint de actuator
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget -qO- http://localhost:8081/actuator/health || exit 1

# Punto de entrada
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar --server.port=${SERVER_PORT}"]
