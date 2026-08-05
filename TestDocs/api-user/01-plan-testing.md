# Plan de Pruebas — DMH Wallet (api-user) · Sprint 1

> **Proyecto:** DMH Wallet  
> **Microservicio:** `api-user` (Spring Boot + Keycloak)  
> **Alcance Sprint 1:** Registro, Login, Logout, Reset Password, Recovery Email (Send Verification) + endpoints auxiliares del módulo (SaveUser, GetUsers, GetEmail, GetUserNameEmail, DeleteUser).  
> **Versión del documento:** 3.0  
> **Owner:** Equipo de QA · Digital Money House  
> **Total de casos:** **44** (16 Smoke + 28 Regresión) · **25 capa Service (JUnit + Mockito)** + **19 capa API (RestAssured)**  

---

## 1. ¿Cómo escribir un caso de prueba?

Un caso de prueba (test case) es un conjunto de condiciones, datos de entrada, pasos de ejecución y resultados esperados que permiten validar un comportamiento específico del sistema. Para que sea útil, mantenible y ejecutable, debe elaborarse siguiendo una estructura clara y trazable.

### 1.1 Estructura recomendada

| Campo                  | Descripción                                                                      | Ejemplo                                                                          |
|------------------------|----------------------------------------------------------------------------------|----------------------------------------------------------------------------------|
| **ID**                 | Identificador único. Formato sugerido: `TC-[MÓDULO]-[NÚMERO]`                    | `TC-USER-001`                                                                    |
| **Título**             | Nombre claro y conciso del objetivo del caso.                                    | "Registro exitoso de usuario con todos los campos válidos"                       |
| **Descripción**        | Resumen del comportamiento bajo prueba.                                          | "Verifica que el endpoint `/api/user/register` cree un usuario en Keycloak y BD" |
| **Módulo / Feature**   | Agrupador funcional.                                                             | `api-user` → `Auth` → `Register`                                                 |
| **Prioridad**          | `Alta`, `Media`, `Baja`. Define el orden y la profundidad con la que se ejecuta. | `Alta`                                                                           |
| **Tipo**               | `Funcional`, `No funcional`, `Seguridad`, `Integración`, `E2E`.                  | `Funcional`                                                                      |
| **Suite**              | Clasificación: `Smoke`, `Regresión`, `Sanity`, `Exploratorio`.                   | `Smoke`                                                                          |
| **Capa**               | `Service` (JUnit/Mockito) o `Controller/API` (RestAssured).                      | `Service`                                                                        |
| **Precondiciones**     | Estado del sistema y/o datos necesarios antes de ejecutar.                       | "Servicio `api-user` levantado. Keycloak accesible. BD limpia."                  |
| **Datos de entrada**   | Payload, cabeceras, query params.                                                | `{"nombre":"Juan",...,"email":"juan@test.com","pwd":"Pass123!"}`                 |
| **Pasos**              | Secuencia numerada y reproducible.                                               | `1) POST /api/user/register  2) Validar response 200  3) Verificar BD`           |
| **Resultado esperado** | Comportamiento exacto que debe ocurrir (status, body, efectos colaterales).      | `200 OK`, body con `keycloakId`, usuario persistido en BD.                       |
| **Resultado real**     | Lo observado durante la ejecución (lo completa el QA al ejecutar).               | `200 OK`, body OK, usuario persistido.                                           |
| **Status**             | `Pendiente`, `En ejecución`, `Passed`, `Failed`, `Blocked`, `Skipped`.           | `Passed`                                                                         |
| **Evidencia**          | Capturas, logs, request/response, IDs de bug asociados.                          | Screenshot, log `app.log`, bug `BUG-123`.                                        |
| **Autor / Fecha**      | Quién lo escribió y cuándo.                                                      | `M. Pérez — 2026-08-01`                                                          |

### 1.2 Buenas prácticas al escribir casos de prueba

1. **Un objetivo por caso.** No mezclar dos validaciones distintas; si falla, debe saberse exactamente qué se rompió.
2. **Lenguaje claro y sin ambigüedad.** Evitar "verificar que funcione bien". Ser específico: "El campo `email` del body de respuesta coincide con el enviado".
3. **Datos de entrada realistas y controlados.** Usar emails únicos por ejecución (`timestamp` o UUID) para evitar colisiones.
4. **Independencia entre casos.** Cada caso debe poder ejecutarse de forma aislada (no depender del orden ni de un caso previo).
5. **Trazabilidad.** Cada caso debe enlazar a un **requerimiento / historia de usuario** y, cuando falle, a un **bug**.
6. **Cubrir caminos felices, alternativos y de error.** Para cada endpoint: caso positivo + casos negativos (400, 401, 404, 409, 500).
7. **Resultados esperados medibles.** Status code, valores exactos, mensajes, efectos en BD, llamadas a Keycloak.
8. **Determinismo.** Mismos datos + mismos pasos → mismo resultado (usar mocks cuando aplique).
9. **Versionado.** Mantener la planilla en Git para tener historial de cambios.
10. **Revisión por pares.** Otro QA o el dev debe validar el caso antes de ejecutarlo.
11. **Documentar, no duplicar.** Si el caso ya está implementado como test automatizado (JUnit/Mockito o RestAssured), **se documenta su comportamiento y su trazabilidad** — no se reescribe el código en otro archivo. Cada test vive en una sola clase de test.

---

## 2. ¿Cómo reportar un defecto (bug)?

Un defecto bien reportado reduce el tiempo de resolución y evita idas y vueltas entre QA y desarrollo. La regla de oro: **ser reproducible y específico**.

### 2.1 Estructura del reporte

| Campo                       | Descripción                                                                              |
|-----------------------------|------------------------------------------------------------------------------------------|
| **ID**                      | `BUG-[PROYECTO]-[NÚMERO]`. Ej: `BUG-DMH-042`.                                            |
| **Título**                  | Frase corta que resuma el problema. Formato: `[Módulo] Qué ocurre + impacto`.            |
| **Severidad**               | `Crítica` · `Alta` · `Media` · `Baja`.                                                   |
| **Prioridad**               | `Urgente` · `Alta` · `Media` · `Baja`.                                                   |
| **Módulo / Feature**        | Componente afectado.                                                                     |
| **Caso de prueba asociado** | `TC-USER-XXX` que detectó el bug.                                                        |
| **Entorno**                 | Ambiente (`dev`, `staging`, `prod`), versión del servicio, navegador (si aplica), URL.   |
| **Precondiciones**          | Datos o estado previo necesario para reproducir.                                         |
| **Pasos para reproducir**   | Lista numerada, concreta y reproducible.                                                 |
| **Resultado esperado**      | Lo que debería haber ocurrido.                                                           |
| **Resultado actual**        | Lo que realmente ocurre. Incluir evidencia.                                              |
| **Evidencia**               | Screenshots, logs (`INFO`/`ERROR`), request/response (curl/Postman), capturas de BD.     |
| **Logs**                    | Stack trace completo si lo hay.                                                          |
| **Workaround**              | Si existe, indicar cómo mitigar mientras se resuelve.                                    |
| **Asignado a**              | Developer responsable.                                                                   |
| **Estado**                  | `Nuevo` · `En análisis` · `En corrección` · `En verificación` · `Reabierto` · `Cerrado`. |

### 2.2 Severidades (referencia)

| Severidad | Significado                                                                       |
|-----------|-----------------------------------------------------------------------------------|
| Crítica   | Sistema caído, pérdida de datos, agujero de seguridad, flujo principal bloqueado. |
| Alta      | Funcionalidad principal falla pero hay workaround; impacto en muchos usuarios.    |
| Media     | Funcionalidad secundaria falla; impacto acotado.                                  |
| Baja      | Cosmético,文案, mejoras de UX sin impacto funcional.                                |

### 2.3 Ejemplo de reporte

> **BUG-DMH-042 · [api-user/register] Devuelve 500 cuando el email ya existe en Keycloak**  
> **Severidad:** Alta · **Prioridad:** Alta  
> **TC asociado:** `TC-USER-002`  
> **Entorno:** `staging` · `api-user v0.0.1` · Keycloak 26.0  
> **Pasos:**  
> 1. `POST /api/user/register` con email `qa+1@test.com` (ya registrado en Keycloak).  
> 2. Observar respuesta.  
> **Esperado:** `400 Bad Request` con mensaje `"El email ya está registrado"`.  
> **Actual:** `500 Internal Server Error` con stack `WebApplicationException` no controlada.  
> **Evidencia:** log `app.log` línea 312, captura Postman.  
> **Workaround:** Validar duplicado antes de llamar a Keycloak.

---

## 3. Criterio para incluir un caso de prueba en una suite de **Smoke**

**Objetivo de la suite de Smoke:** validar en pocos minutos que las funcionalidades **críticas** del sistema están operativas después de un despliegue o cambio. Si smoke falla, **no se promueve el build**.

### 3.1 Criterios de inclusión

Un caso entra a la suite de **Smoke** si cumple **TODOS** estos criterios:

1. **Cubre un flujo crítico de negocio** (registro, login, logout, recuperación de contraseña, etc.) sin el cual la aplicación no es utilizable.
2. **Recorre el camino feliz (happy path).** Smoke no valida casos negativos exhaustivos.
3. **Es estable y determinista.** No debe depender de datos externos volátiles ni de timings sensibles.
4. **Es rápido de ejecutar.** Idealmente < 5 s por caso. La suite completa de smoke debería correr en menos de 10–15 minutos.
5. **Verifica una integración esencial** (BD, Keycloak, gateway) en su nivel más básico.
6. **Tiene alta probabilidad de detectar regresiones graves** en componentes centrales.
7. **No requiere configuración especial** del entorno más allá del setup estándar.
8. **Es trazable a un requisito crítico** del producto.

> **Regla práctica:** si el smoke falla, el equipo no continúa con pruebas más profundas hasta resolverlo.

---

## 4. Criterio para incluir un caso de prueba en una suite de **Regresión**

**Objetivo de la suite de Regresión:** asegurar que los cambios nuevos (funcionalidades, refactors, fixes) **no rompieron** funcionalidades existentes. Se ejecuta antes de cada release, después de merges a `main`, y de manera periódica.

### 4.1 Criterios de inclusión

Un caso entra a la suite de **Regresión** si cumple **al menos uno** de estos criterios:

1. **Cubre funcionalidades existentes que ya fueron validadas en producción.** Smoke valida que "sigue vivo"; regresión valida que "sigue funcionando como antes".
2. **Cubre casos negativos y de borde** (campos vacíos, formatos inválidos, duplicados, timeouts, errores 4xx/5xx esperados).
3. **Cubre integraciones con servicios externos** (Keycloak, BD, otros microservicios) en escenarios diversos.
4. **Cubre reglas de negocio no triviales** (validaciones de dominio, formato de CVU/alias, unicidad de email/dni/teléfono).
5. **Fue agregado/modificado por el cambio actual.** Todo test nuevo afectado por un cambio debe correr en regresión.
6. **Tiene un historial de haber detectado bugs.** Los casos que históricamente encuentran defectos son candidatos prioritarios.
7. **Cubre validaciones de seguridad y permisos** (autenticación, autorización, manejo de tokens).
8. **Cubre requisitos no funcionales** cuando aplique (performance básica, timeouts, mensajes de error legibles).
9. **Es trazable a un requisito funcional** documentado (historia de usuario, criterio de aceptación).

> **Regla práctica:** si un caso valida un escenario que ya estaba en producción y se quiere garantizar que no se rompió, va a regresión. La suite de regresión puede ser larga (cientos de casos) y se suele ejecutar en nightly o pre-release.

---

## 5. Cómo se ejecutan las suites en este proyecto

- La suite que **ejecuta todos los tests del módulo `api-user`** es [`ApiUserApplicationTests`](../../Project/DMH/api-user/src/test/java/com/dmh/ApiUserApplicationTests.java) — al correr `mvn test` (o `mvn verify`) sobre el módulo `api-user`, **todos los tests JUnit/Mockito y RestAssured se ejecutan juntos**, sin separación a nivel de pipeline.
- La separación entre **JUnit/Mockito** (capa `Service`) y **RestAssured** (capa `Controller/API`) es **exclusivamente a nivel de documentación y organización** de este `TestDocs/`, tal como se describe en [`02-junit-mockito-tests.md`](02-junit-mockito-tests.md) y [`03-restassured-tests.md`](03-restassured-tests.md). **No requiere configuración adicional ni etiquetas `@Tag`** — son los 44 tests existentes, ya implementados en sus respectivas clases.
- Las clasificaciones **Smoke / Regresión** son **conceptuales**: sirven para que QA pueda decidir qué subset priorizar ante un cambio. A nivel de ejecución **corre todo**.

---

## 6. Planilla de Casos de Prueba — Sprint 1 (`api-user`) — **44 casos**

> Esta planilla mapea 1-a-1 los **44 tests automatizados que ya existen** en el proyecto (no inventa nuevos tests):
> - **25 tests** en [`ApiUserServiceTest.java`](../../Project/DMH/api-user/src/test/java/com/dmh/UserService/ApiUserServiceTest.java) — JUnit 5 + Mockito sobre `UserService`.
> - **19 tests** en [`ApiUserControllerTest.java`](../../Project/DMH/api-user/src/test/java/com/dmh/UserController/ApiUserControllerTest.java) — JUnit 5 + RestAssured + Spring Boot sobre `UserController`.

### 6.1 Leyenda de columnas

- **ID**: identificador del caso (formato `TC-USER-XXX`).
- **Suite**: `Smoke` (S) o `Regresión` (R).
- **Capa**: `Service` (JUnit+Mockito) o `API` (RestAssured sobre Controller).
- **Prioridad**: A = Alta, M = Media, B = Baja.
- **Status**: estado actual del caso (Pendiente / Passed / Failed / Blocked).

### 6.2 Capa Service — JUnit 5 + Mockito (25 casos)

| ID          | Módulo              | Suite      | Capa    | Prioridad | Título                                                                                               | Test existente en el proyecto                                                           | Precondiciones                                              | Datos de entrada                    | Pasos (resumen)                                                                                                                      | Resultado esperado                                                                        | Status    |
|-------------|---------------------|------------|---------|-----------|------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------|-------------------------------------------------------------|-------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------|-----------|
| TC-USER-001 | Register            | Smoke      | Service | A         | Registro exitoso en `UserService` con Keycloak + BD                                                  | `ShouldRegisterUserSuccessfully`                                                        | Servicio disponible                                         | `UserDTO` completo con email único  | 1) mock `keycloakClient.createUser` → `keycloakId` 2) `userMapper.DTOtoUser` 3) `userService.register(dto)` 4) `userRepository.save` | `User` persistido, `keycloakId` seteado, `cvu`/`alias` autogenerados                      | Pendiente |
| TC-USER-002 | Register            | Regresión  | Service | A         | Registro lanza `UserBadRequestException` cuando Keycloak devuelve `BadRequestException`              | `ShouldThrowUserBadRequestExceptionWhenKeycloakReturnsBadRequest`                       | Keycloak rechaza por email duplicado                        | `UserDTO` válido                    | 1) mock `keycloakClient.createUser` → `BadRequestException` 2) `userService.register(dto)`                                           | Lanza `UserBadRequestException`, NO persiste en BD                                        | Pendiente |
| TC-USER-003 | Register            | Regresión  | Service | A         | Registro lanza `UserInternalServerErrorException` cuando Keycloak devuelve `WebApplicationException` | `ShouldThrowUserInternalServerErrorExceptionWhenKeycloakReturnsWebApplicationException` | Keycloak devuelve 5xx                                       | `UserDTO` válido                    | 1) mock `keycloakClient.createUser` → `WebApplicationException` 2) `userService.register(dto)`                                       | Lanza `UserInternalServerErrorException`                                                  | Pendiente |
| TC-USER-004 | Register            | Regresión  | Service | A         | Registro lanza `UserInternalServerErrorException` ante excepción genérica                            | `ShouldThrowUserInternalServerErrorExceptionOnGenericException`                         | Error inesperado                                            | `UserDTO` válido                    | 1) mock `keycloakClient.createUser` → `RuntimeException` 2) `userService.register(dto)`                                              | Lanza `UserInternalServerErrorException`                                                  | Pendiente |
| TC-USER-005 | Login               | Smoke      | Service | A         | Login exitoso en `UserService` devuelve `AccessTokenResponse`                                        | `ShouldLoginSuccessfully`                                                               | Usuario existente en BD + Keycloak                          | `email` y `pwd` válidos             | 1) mock `userRepository.findByEmail` → `Optional.of(user)` 2) mock `keycloakAuth.login` → token 3) `userService.login(email, pwd)`   | `AccessTokenResponse` con `access_token` y `refresh_token`                                | Pendiente |
| TC-USER-006 | Login               | Regresión  | Service | A         | Login lanza `UserNotFoundException` cuando el email no existe                                        | `ShouldThrowUserNotFoundExceptionWhenUserDoesNotExist` (LoginTests)                     | `userRepository.findByEmail` → empty                        | `email` no registrado               | 1) `userService.login(email, pwd)`                                                                                                   | Lanza `UserNotFoundException`                                                             | Pendiente |
| TC-USER-007 | Login               | Regresión  | Service | A         | Login lanza `UserInvalidCredentialsException` cuando Keycloak devuelve 401                           | `ShouldThrowUserInvalidCredentialsExceptionOn401Error`                                  | Keycloak → `RuntimeException("401 Unauthorized")`           | `email` existente, `pwd` incorrecto | 1) `userService.login(email, pwd)`                                                                                                   | Lanza `UserInvalidCredentialsException`                                                   | Pendiente |
| TC-USER-008 | Login               | Regresión  | Service | A         | Login lanza `UserInternalServerErrorException` ante otro error de Keycloak                           | `ShouldThrowUserInternalServerErrorExceptionOnOtherErrors`                              | Keycloak → `RuntimeException("Connection timeout")`         | `email` existente                   | 1) `userService.login(email, pwd)`                                                                                                   | Lanza `UserInternalServerErrorException`                                                  | Pendiente |
| TC-USER-009 | Logout              | Smoke      | Service | A         | `UserService.logout(token)` invoca `KeycloakAuth.logout(token)` sin lanzar excepción                 | `ShouldLogoutSuccessfully`                                                              | Mock `keycloakAuth.logout` → no-op                          | `refreshToken`                      | 1) `userService.logout(token)`                                                                                                       | `keycloakAuth.logout(token)` invocado 1 vez                                               | Pendiente |
| TC-USER-010 | Logout              | Regresión  | Service | A         | `UserService.logout(token)` lanza `UserInternalServerErrorException` ante fallo de Keycloak          | `ShouldThrowUserInternalServerErrorExceptionWhenLogoutFails`                            | `keycloakAuth.logout` → `RuntimeException`                  | `refreshToken`                      | 1) `userService.logout(token)`                                                                                                       | Lanza `UserInternalServerErrorException`                                                  | Pendiente |
| TC-USER-011 | Send Verification   | Smoke      | Service | A         | `UserService.sendEmailVerification(email)` invoca Keycloak exitosamente                              | `ShouldSendEmailVerificationSuccessfully`                                               | Usuario existente con `keycloakId`                          | `email`                             | 1) mock `userRepository.findByEmail` → `Optional.of(user)` 2) `userService.sendEmailVerification(email)`                             | `keycloakClient.sendEmailVerification(keycloakId)` invocado 1 vez                         | Pendiente |
| TC-USER-012 | Send Verification   | Regresión  | Service | A         | `UserService.sendEmailVerification(email)` lanza `UserNotFoundException` si el email no existe       | `ShouldThrowUserNotFoundExceptionWhenUserDoesNotExistForVerification`                   | `userRepository.findByEmail` → empty                        | `email` no registrado               | 1) `userService.sendEmailVerification(email)`                                                                                        | Lanza `UserNotFoundException`, NO llama a Keycloak                                        | Pendiente |
| TC-USER-013 | Send Verification   | Regresión  | Service | A         | `UserService.sendEmailVerification(email)` lanza `UserBadRequestException` si `keycloakId` es null   | `ShouldThrowUserBadRequestExceptionWhenUserHasNoKeycloakId`                             | Usuario sin `keycloakId`                                    | `email` de usuario sin keycloakId   | 1) `userService.sendEmailVerification(email)`                                                                                        | Lanza `UserBadRequestException`, NO llama a Keycloak                                      | Pendiente |
| TC-USER-014 | Send Verification   | Regresión  | Service | A         | `UserService.sendEmailVerification(email)` propaga excepción de Keycloak (refactor pendiente)        | `ShouldThrowExceptionWhenKeycloakFails`                                                 | `keycloakClient.sendEmailVerification` → `RuntimeException` | `email` existente                   | 1) `userService.sendEmailVerification(email)`                                                                                        | Propaga `RuntimeException` (refactor a `UserInternalServerErrorException` está pendiente) | Pendiente |
| TC-USER-015 | Reset Password      | Smoke      | Service | A         | `UserService.resetPasswordByEmail(email)` invoca Keycloak exitosamente                               | `ShouldResetPasswordSuccessfully`                                                       | Usuario existente                                           | `email`                             | 1) `userService.resetPasswordByEmail(email)`                                                                                         | `keycloakClient.resetPassword(keycloakId)` invocado 1 vez                                 | Pendiente |
| TC-USER-016 | Reset Password      | Regresión  | Service | A         | `UserService.resetPasswordByEmail(email)` lanza `UserNotFoundException` si email no existe           | `ShouldThrowUserNotFoundExceptionWhenUserDoesNotExistForReset`                          | `userRepository.findByEmail` → empty                        | `email` no registrado               | 1) `userService.resetPasswordByEmail(email)`                                                                                         | Lanza `UserNotFoundException`                                                             | Pendiente |
| TC-USER-017 | Reset Password      | Regresión  | Service | A         | `UserService.resetPasswordByEmail(email)` lanza `UserBadRequestException` si `keycloakId` es vacío   | `ShouldThrowUserBadRequestExceptionWhenUserHasNoKeycloakIdForReset`                     | Usuario con `keycloakId=""`                                 | `email` del usuario                 | 1) `userService.resetPasswordByEmail(email)`                                                                                         | Lanza `UserBadRequestException`, NO llama a Keycloak                                      | Pendiente |
| TC-USER-018 | Reset Password      | Regresión  | Service | A         | `UserService.resetPasswordByEmail(email)` propaga excepción de Keycloak (refactor pendiente)         | `ShouldThrowExceptionWhenKeycloakResetFails`                                            | `keycloakClient.resetPassword` → `RuntimeException`         | `email` existente                   | 1) `userService.resetPasswordByEmail(email)`                                                                                         | Propaga `RuntimeException`                                                                | Pendiente |
| TC-USER-019 | Save User           | Regresión  | Service | M         | `UserService.saveUser(dto)` mapea, guarda y devuelve DTO                                             | `ShouldSaveUserSuccessfully`                                                            | Mocks OK                                                    | `UserDTO` válido                    | 1) `userService.saveUser(dto)`                                                                                                       | Devuelve `UserDTO` con `email` correcto; `mapper`, `repository.save` y `mapper` invocados | Pendiente |
| TC-USER-020 | Get Users           | Smoke      | Service | M         | `UserService.getUsers()` devuelve lista mapeada con usuarios                                         | `ShouldGetAllUsersSuccessfully`                                                         | 1 usuario en BD                                             | —                                   | 1) `userService.getUsers()`                                                                                                          | Lista de tamaño 1, llama a `findAll()`                                                    | Pendiente |
| TC-USER-021 | Get Users           | Regresión  | Service | M         | `UserService.getUsers()` devuelve lista vacía si no hay usuarios                                     | `ShouldReturnEmptyListWhenNoUsersExist`                                                 | BD vacía                                                    | —                                   | 1) `userService.getUsers()`                                                                                                          | Lista vacía (`isEmpty() == true`)                                                         | Pendiente |
| TC-USER-022 | Get Email           | Smoke      | Service | M         | `UserService.getEmail(email)` devuelve el email si existe                                            | `ShouldGetEmailSuccessfully`                                                            | Usuario existente                                           | `email`                             | 1) `userService.getEmail(email)`                                                                                                     | Devuelve el email solicitado                                                              | Pendiente |
| TC-USER-023 | Get Email           | Regresión  | Service | M         | `UserService.getEmail(email)` lanza `UserNotFoundException` si no existe                             | `ShouldThrowUserNotFoundExceptionWhenUserDoesNotExist` (GetEmailTests)                  | `userRepository.findByEmail` → empty                        | `email` no registrado               | 1) `userService.getEmail(email)`                                                                                                     | Lanza `UserNotFoundException`                                                             | Pendiente |
| TC-USER-024 | Get User Name Email | Regresión  | Service | M         | `UserService.getUserNameEmail()` devuelve lista con nombre + email                                   | `ShouldGetUserNameEmailSuccessfully`                                                    | 1 usuario en BD                                             | —                                   | 1) `userService.getUserNameEmail()`                                                                                                  | Lista de tamaño 1                                                                         | Pendiente |
| TC-USER-025 | Delete User         | Regression | Service | M         | `UserService.deleteUser(id)` invoca `userRepository.deleteById(id)`                                  | `ShouldDeleteUserSuccessfully`                                                          | —                                                           | `id` UUID                           | 1) `userService.deleteUser(id)`                                                                                                      | `userRepository.deleteById(id)` invocado 1 vez                                            | Pendiente |

### 6.3 Capa API — JUnit 5 + RestAssured + Spring Boot (19 casos)

| ID          | Módulo            | Suite     | Capa | Prioridad | Título                                                                                                     | Test existente en el proyecto                                 | Precondiciones                                                     | Datos de entrada                                                       | Pasos (resumen)                                                     | Resultado esperado                                             | Status    |
|-------------|-------------------|-----------|------|-----------|------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------|--------------------------------------------------------------------|------------------------------------------------------------------------|---------------------------------------------------------------------|----------------------------------------------------------------|-----------|
| TC-USER-026 | Register          | Smoke     | API  | A         | `POST /register` con `UserDTO` válido devuelve 200 con `keycloakId`                                        | `shouldRegisterUserSuccessfully`                              | Mock `userService.register` → `user`                               | `UserDTO` completo con email único                                     | 1) POST `/api/user/register`                                        | `200 OK`, body con `keycloakId` no nulo                        | Pendiente |
| TC-USER-027 | Register          | Regresión | API  | A         | `POST /register` cuando el servicio lanza `UserBadRequestException` devuelve 400                           | `shouldReturn400WhenKeycloakRejectsUser`                      | Mock `userService.register` → `UserBadRequestException`            | `UserDTO` con email `duplicado@example.com` (datos distintos al smoke) | 1) POST `/api/user/register`                                        | `400 Bad Request`                                              | Pendiente |
| TC-USER-028 | Login             | Smoke     | API  | A         | `POST /login` con credenciales válidas devuelve 200 con tokens                                             | `shouldLoginSuccessfullyWithValidCredentials`                 | Mock `userService.login` → token válido                            | `{"email":..., "pwd":...}`                                             | 1) POST `/api/user/login`                                           | `200 OK`, body con `access_token` no vacío                     | Pendiente |
| TC-USER-029 | Login             | Regresión | API  | A         | `POST /login` cuando el servicio lanza `UserNotFoundException` devuelve 404                                | `shouldReturn404WhenUserDoesNotExist` (LoginEndpointTests)    | Mock `userService.login` → `UserNotFoundException`                 | `{"email":"nonexistent1234@example.com", "pwd":...}`                   | 1) POST `/api/user/login`                                           | `404 Not Found`                                                | Pendiente |
| TC-USER-030 | Login             | Regresión | API  | A         | `POST /login` cuando el servicio lanza `UserInvalidCredentialsException` devuelve 400                      | `shouldReturn401OnInvalidCredentials`                         | Mock `userService.login` → `UserInvalidCredentialsException`       | `{"email":testEmail, "pwd":"wrongPassword1234"}`                       | 1) POST `/api/user/login`                                           | `400 Bad Request`                                              | Pendiente |
| TC-USER-031 | Logout            | Smoke     | API  | A         | `POST /logout` con `X-Refresh-Token` válido devuelve 200 + mensaje                                         | `shouldLogoutSuccessfullyWithValidRefreshToken`               | Mock `userService.logout` → no-op                                  | Header `X-Refresh-Token: valid-refresh-token`                          | 1) POST `/api/user/logout`                                          | `200 OK`, body `"User Logout Successfully"`                    | Pendiente |
| TC-USER-032 | Logout            | Regresión | API  | A         | `POST /logout` con refresh token inválido (servicio lanza `UserInternalServerErrorException`) devuelve 500 | `shouldReturn500WhenLogoutFails`                              | Mock `userService.logout` → `UserInternalServerErrorException`     | Header `X-Refresh-Token: invalid-token`                                | 1) POST `/api/user/logout`                                          | `500 Internal Server Error`                                    | Pendiente |
| TC-USER-033 | Send Verification | Smoke     | API  | A         | `POST /send-verification?email=…` con usuario existente devuelve 200 + mensaje                             | `shouldSendVerificationEmail`                                 | Mock `userService.sendEmailVerification` → no-op                   | Query `email=test@example.com`                                         | 1) POST `/api/user/send-verification?email=test@example.com`        | `200 OK`, body `"Email de verificación enviado correctamente"` | Pendiente |
| TC-USER-034 | Send Verification | Regresión | API  | A         | `POST /send-verification` cuando el servicio lanza `UserNotFoundException` devuelve 404                    | `shouldReturn404WhenUserNotFoundForVerification`              | Mock `userService.sendEmailVerification` → `UserNotFoundException` | Query `email=nonexistent@example.com`                                  | 1) POST `/api/user/send-verification?email=nonexistent@example.com` | `404 Not Found`                                                | Pendiente |
| TC-USER-035 | Send Verification | Regresión | API  | A         | `POST /send-verification` cuando el servicio lanza `UserBadRequestException` devuelve 400                  | `shouldReturn400WhenUserHasNoKeycloakIdForVerification`       | Mock → `UserBadRequestException`                                   | Query `email=test@example.com`                                         | 1) POST `/api/user/send-verification?email=test@example.com`        | `400 Bad Request`                                              | Pendiente |
| TC-USER-036 | Send Verification | Regresión | API  | A         | `POST /send-verification` cuando el servicio lanza `UserInternalServerErrorException` devuelve 500         | `shouldReturn500WhenKeycloakFailsForVerification`             | Mock → `UserInternalServerErrorException`                          | Query `email=test@example.com`                                         | 1) POST `/api/user/send-verification?email=test@example.com`        | `500 Internal Server Error`                                    | Pendiente |
| TC-USER-037 | Reset Password    | Smoke     | API  | A         | `POST /reset-password?email=…` con usuario existente devuelve 200                                          | `shouldResetPasswordSuccessfully`                             | Mock `userService.resetPasswordByEmail` → no-op                    | Query `email=test@example.com`                                         | 1) POST `/api/user/reset-password?email=test@example.com`           | `200 OK`                                                       | Pendiente |
| TC-USER-038 | Reset Password    | Regresión | API  | A         | `POST /reset-password` cuando el servicio lanza `UserNotFoundException` devuelve 404                       | `shouldReturn404WhenUserNotFoundForReset`                     | Mock → `UserNotFoundException`                                     | Query `email=nonexistent@example.com`                                  | 1) POST `/api/user/reset-password?email=nonexistent@example.com`    | `404 Not Found`                                                | Pendiente |
| TC-USER-039 | Reset Password    | Regresión | API  | A         | `POST /reset-password` cuando el servicio lanza `UserBadRequestException` devuelve 400                     | `shouldReturn400WhenUserHasNoKeycloakIdForReset`              | Mock → `UserBadRequestException`                                   | Query `email=test@example.com`                                         | 1) POST `/api/user/reset-password?email=test@example.com`           | `400 Bad Request`                                              | Pendiente |
| TC-USER-040 | Reset Password    | Regresión | API  | A         | `POST /reset-password` cuando el servicio lanza `UserInternalServerErrorException` devuelve 500            | `shouldReturn500WhenKeycloakFailsForReset`                    | Mock → `UserInternalServerErrorException`                          | Query `email=test@example.com`                                         | 1) POST `/api/user/reset-password?email=test@example.com`           | `500 Internal Server Error`                                    | Pendiente |
| TC-USER-041 | Get All Users     | Smoke     | API  | M         | `GET /api/user` devuelve 200 con lista de usuarios                                                         | `shouldReturnAllUsers`                                        | Mock `userService.getUsers` → 1 usuario                            | —                                                                      | 1) GET `/api/user`                                                  | `200 OK`                                                       | Pendiente |
| TC-USER-042 | Get Email         | Smoke     | API  | M         | `GET /api/user/getEmail?email=…` con usuario existente devuelve 200 + email                                | `shouldReturnEmailWhenUserExists`                             | Mock `userService.getEmail` → email                                | Query `email=test@example.com`                                         | 1) GET `/api/user/getEmail?email=test@example.com`                  | `200 OK`, body = email                                         | Pendiente |
| TC-USER-043 | Get Email         | Regresión | API  | M         | `GET /api/user/getEmail` cuando el servicio lanza `UserNotFoundException` devuelve 404                     | `shouldReturn404WhenUserDoesNotExist` (GetEmailEndpointTests) | Mock → `UserNotFoundException`                                     | Query `email=nonexistent@example.com`                                  | 1) GET `/api/user/getEmail?email=nonexistent@example.com`           | `404 Not Found`                                                | Pendiente |
| TC-USER-044 | Delete User       | Smoke     | API  | M         | `DELETE /api/user/{id}` con id válido devuelve 204                                                         | `shouldDeleteUserSuccessfully`                                | Mock `userService.deleteUser` → no-op                              | Path `id=UUID`                                                         | 1) DELETE `/api/user/{id}`                                          | `204 No Content`                                               | Pendiente |

### 6.4 Resumen ejecutivo

| Suite         | # Casos | IDs                                                                                                                                                                                                                                                             |
|---------------|---------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Smoke**     | **16**  | `TC-USER-001`, `TC-USER-005`, `TC-USER-009`, `TC-USER-011`, `TC-USER-015`, `TC-USER-020`, `TC-USER-022`, `TC-USER-025` (Service) + `TC-USER-026`, `TC-USER-028`, `TC-USER-031`, `TC-USER-033`, `TC-USER-037`, `TC-USER-041`, `TC-USER-042`, `TC-USER-044` (API) |
| **Regresión** | **28**  | Resto de la planilla                                                                                                                                                                                                                                            |
| **Total**     | **44**  | Cobertura completa del módulo `api-user`                                                                                                                                                                                                                        |

### 6.5 Distribución por capa

| Capa / Suite               | Smoke  | Regresión | Total  |
|----------------------------|--------|-----------|--------|
| Service (JUnit + Mockito)  | 8      | 17        | 25     |
| API (RestAssured + Spring) | 8      | 11        | 19     |
| **Total módulo**           | **16** | **28**    | **44** |

### 6.6 Distribución por feature

| Feature             | Smoke  | Regresión | # Casos | IDs                                      |
|---------------------|--------|-----------|---------|------------------------------------------|
| Register            | 2      | 3         | 5       | `TC-USER-001…004`, `TC-USER-026…027`     |
| Login               | 2      | 4         | 6       | `TC-USER-005…008`, `TC-USER-028…030`     |
| Logout              | 2      | 2         | 4       | `TC-USER-009…010`, `TC-USER-031…032`     |
| Send Verification   | 2      | 5         | 7       | `TC-USER-011…014`, `TC-USER-033…036`     |
| Reset Password      | 2      | 5         | 7       | `TC-USER-015…018`, `TC-USER-037…040`     |
| Save User           | 0      | 1         | 1       | `TC-USER-019`                            |
| Get Users           | 1      | 1         | 2       | `TC-USER-020…021`                        |
| Get Email           | 1      | 2         | 3       | `TC-USER-022…023`, `TC-USER-042…043`     |
| Get User Name Email | 0      | 1         | 1       | `TC-USER-024`                            |
| Delete User         | 2      | 0         | 2       | `TC-USER-025`, `TC-USER-044`             |
| **Total**           | **16** | **28**    | **44**  | Cobertura completa del módulo `api-user` |

> **Nota:** en este Sprint 1 los **endpoints críticos de negocio** (`/register`, `/login`, `/logout`, `/send-verification`, `/reset-password`) tienen **SIEMPRE al menos 1 caso en Smoke** (happy path del servicio + happy path del endpoint) **+ todos los negativos en Regresión**. Los endpoints auxiliares (`saveUser`, `getUsers`, `getEmail`, `getUserNameEmail`, `deleteUser`) tienen su happy path **en Smoke sólo si son críticos para la operación del módulo** (en este caso `getUsers`, `getEmail`, `deleteUser`); los demás casos auxiliares quedan en Regresión.

---

## 7. Mantenimiento de la planilla

- **Por cada bug reportado (`BUG-DMH-NNN`)** que no esté cubierto, agregar un caso en la planilla y vincularlo al bug.
- **Por cada cambio funcional** del Sprint 1+, actualizar/agregar casos en la suite de **Regresión**.
- **Antes de promover a producción**, ejecutar la suite de **Smoke** completa y una pasada de **Regresión** sobre los casos afectados.
- **Versionar** esta planilla en Git junto al código (carpeta `TestDocs/api-user/`).

---

## 8. Referencias

- Suite que corre todos los tests: [`ApiUserApplicationTests.java`](../../Project/DMH/api-user/src/test/java/com/dmh/ApiUserApplicationTests.java)
- Tests JUnit/Mockito documentados: [`ApiUserServiceTest.java`](../../Project/DMH/api-user/src/test/java/com/dmh/UserService/ApiUserServiceTest.java) → descripción en [`02-junit-mockito-tests.md`](02-junit-mockito-tests.md)
- Tests RestAssured documentados: [`ApiUserControllerTest.java`](../../Project/DMH/api-user/src/test/java/com/dmh/UserController/ApiUserControllerTest.java) → descripción en [`03-restassured-tests.md`](03-restassured-tests.md)
- Endpoints: [`UserController.java`](../../Project/DMH/api-user/src/main/java/com/dmh/UserController/UserController.java)
- Lógica de negocio: [`UserService.java`](../../Project/DMH/api-user/src/main/java/com/dmh/UserService/UserService.java)
- DTO validado: [`UserDTO.java`](../../Project/DMH/api-user/src/main/java/com/dmh/UserDTO/UserDTO.java)
- Excepciones / códigos de error: [`GlobalExceptionHandler.java`](../../Project/DMH/api-user/src/main/java/com/dmh/Exceptions/GlobalExceptionHandler.java)
- Requisitos Sprint 1: `Requirements/SPRINT 1.pdf`