# Documentación de Tests · Capa API (RestAssured + JUnit 5)

> **Microservicio:** `api-user`  
> **Capa bajo prueba:** [`UserController`](../../Project/DMH/api-user/src/main/java/com/dmh/UserController/UserController.java:1) — capa HTTP, contratos, serialización, status codes.  
> **Stack de testing:** JUnit 5 + RestAssured 6 + Spring Boot Test (`@SpringBootTest`) + `@MockitoBean` (mock del `UserService` para evitar dependencias reales de Keycloak/BD).  
> **Archivo de tests:** [`ApiUserControllerTest.java`](../../Project/DMH/api-user/src/test/java/com/dmh/UserController/ApiUserControllerTest.java) — **19 tests ya implementados** (8 Smoke + 11 Regresión).  
> **Ejecución:** todos los tests del módulo corren juntos vía [`ApiUserApplicationTests.java`](../../Project/DMH/api-user/src/test/java/com/dmh/ApiUserApplicationTests.java) con `mvn test`/`mvn verify`. La separación Service/API es **sólo organizativa**.  
> **Trazabilidad:** cada test referencia el `TC-USER-XXX` de [`01-plan-testing.md`](01-plan-testing.md) y el nombre real del test en el archivo.


---

## 1. Convenciones y buenas prácticas aplicadas en estos tests

1. **`@SpringBootTest(webEnvironment = RANDOM_PORT)`** → la app arranca en un puerto aleatorio por test class.
2. **`@MockitoBean` en `UserService`, `KeycloakAuth` y `KeycloakClient`** → la lógica de negocio y las llamadas externas están mockeadas; **sólo se valida la capa HTTP** (routing, request/response, status, headers, JSON).
3. **`@LocalServerPort` + `RestAssured.port`** en `@BeforeEach` → URL dinámica hacia la app levantada.
4. **`RestAssured.reset()`** en `@BeforeEach` para evitar estado compartido.
5. **`ContentType.JSON` / `accept(JSON)`** explícitos en cada request.
6. **Datos únicos por ejecución** (`timestamp` en email) → evita colisiones y permite paralelización futura.
7. **Asserts específicos**: `statusCode`, `body("campo", equalTo(...))`, `body(equalTo(...))` (string exacto).
8. **`Mockito.reset(userService)` antes de tests de Login** que requieren stubbing limpio (ver patrón en `LoginEndpointTests`).
9. **Agrupación por endpoint con `@Nested`** (RegisterEndpointTests, LoginEndpointTests, LogoutEndpointTests, EmailVerificationEndpointTests, ResetPasswordEndpointTests, GetAllUsersEndpointTests, GetEmailEndpointTests, DeleteUserEndpointTests).
10. **Tests independientes**: cada test configura sus propios mocks.

---

## 2. Estructura de la clase de test

```
src/test/java/com/dmh/UserController/
└── ApiUserControllerTest.java
```

- Anotada con `@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)` y `@DisplayName("UserController API Integration Tests")`.
- **Mocks (`@MockitoBean`)**: `userService`, `userRepository`, `userMapper`, `keycloakClient`, `keycloakAuth`.
- **Config**: `@LocalServerPort private int port`.
- **`@BeforeEach setUp()`**:
  - `Mockito.reset(...)` para limpiar stubs.
  - Construye `baseUri = "http://localhost:" + port + "/api/user"`.
  - Genera `testKeycloakId = UUID.randomUUID()`, `testEmail` único por timestamp, `testPassword = "Password123!"`.
  - Construye un `user` (entidad) con datos únicos.

Los tests se organizan en **8 `@Nested` classes** que agrupan por endpoint.

---

## 3. Suite de **Smoke** (8 tests — capa API)

> Validan el happy path HTTP de cada endpoint crítico: routing, contrato JSON, status code 200/204.

### 3.1 `LogoutEndpointTests` — Smoke

#### TC-USER-031 · `shouldLogoutSuccessfullyWithValidRefreshToken`

| Campo                   | Detalle                                                                                   |
|-------------------------|-------------------------------------------------------------------------------------------|
| **Test en el proyecto** | `ApiUserControllerTest.LogoutEndpointTests.shouldLogoutSuccessfullyWithValidRefreshToken` |
| **Suite**               | **Smoke**                                                                                 |
| **Endpoint**            | `POST /api/user/logout`                                                                   |
| **Qué hace**            | Verifica el happy path del logout vía header `X-Refresh-Token`.                           |
| **Mocks que prepara**   | `userService.logout("valid-refresh-token")` → no-op.                                      |
| **Request**             | Header `X-Refresh-Token: valid-refresh-token`, sin body.                                  |
| **Qué verifica**        | Status `200`.                                                                             |
| **Por qué es Smoke**    | Logout es flujo crítico de sesión.                                                        |

### 3.2 `EmailVerificationEndpointTests` — Smoke

#### TC-USER-033 · `shouldSendVerificationEmail`

| Campo                   | Detalle                                                                                   |
|-------------------------|-------------------------------------------------------------------------------------------|
| **Test en el proyecto** | `ApiUserControllerTest.EmailVerificationEndpointTests.shouldSendVerificationEmail`        |
| **Suite**               | **Smoke**                                                                                 |
| **Endpoint**            | `POST /api/user/send-verification?email=…`                                                |
| **Qué hace**            | Verifica el happy path del envío de email de verificación.                                |
| **Mocks que prepara**   | `userService.sendEmailVerification(testEmail)` → no-op.                                   |
| **Request**             | Query param `email=test@example.com`, sin body.                                           |
| **Qué verifica**        | Status `200`. Verifica que `userService.sendEmailVerification(testEmail)` se llamó 1 vez. |
| **Por qué es Smoke**    | Activación de cuenta vía email es paso crítico.                                           |

### 3.3 `ResetPasswordEndpointTests` — Smoke

#### TC-USER-037 · `shouldResetPasswordSuccessfully`

| Campo                   | Detalle                                                                                  |
|-------------------------|------------------------------------------------------------------------------------------|
| **Test en el proyecto** | `ApiUserControllerTest.ResetPasswordEndpointTests.shouldResetPasswordSuccessfully`       |
| **Suite**               | **Smoke**                                                                                |
| **Endpoint**            | `POST /api/user/reset-password?email=…`                                                  |
| **Qué hace**            | Verifica el happy path del reset de contraseña.                                          |
| **Mocks que prepara**   | `userService.resetPasswordByEmail(testEmail)` → no-op.                                   |
| **Request**             | Query param `email=test@example.com`, sin body.                                          |
| **Qué verifica**        | Status `200`. Verifica que `userService.resetPasswordByEmail(testEmail)` se llamó 1 vez. |
| **Por qué es Smoke**    | Recuperación de cuenta es flujo crítico.                                                 |

### 3.4 `GetAllUsersEndpointTests` — Smoke

#### TC-USER-041 · `shouldReturnAllUsers`

| Campo                   | Detalle                                                               |
|-------------------------|-----------------------------------------------------------------------|
| **Test en el proyecto** | `ApiUserControllerTest.GetAllUsersEndpointTests.shouldReturnAllUsers` |
| **Suite**               | **Smoke**                                                             |
| **Endpoint**            | `GET /api/user`                                                       |
| **Qué hace**            | Verifica el happy path del listado de usuarios.                       |
| **Mocks que prepara**   | `userService.getUsers()` → `List.of(userDTO)`.                        |
| **Request**             | `GET /api/user`, sin body, sin headers especiales.                    |
| **Qué verifica**        | Status `200`.                                                         |
| **Por qué es Smoke**    | Endpoint principal de consulta/admin del módulo.                      |

### 3.5 `GetEmailEndpointTests` — Smoke

#### TC-USER-042 · `shouldReturnEmailWhenUserExists`

| Campo                   | Detalle                                                                       |
|-------------------------|-------------------------------------------------------------------------------|
| **Test en el proyecto** | `ApiUserControllerTest.GetEmailEndpointTests.shouldReturnEmailWhenUserExists` |
| **Suite**               | **Smoke**                                                                     |
| **Endpoint**            | `GET /api/user/getEmail?email=…`                                              |
| **Qué hace**            | Verifica el happy path del lookup de email.                                   |
| **Mocks que prepara**   | `userService.getEmail(testEmail)` → `testEmail`.                              |
| **Request**             | Query param `email=test@example.com`.                                         |
| **Qué verifica**        | Status `200`. Body igual a `testEmail`.                                       |
| **Por qué es Smoke**    | Lookup básico; happy path corto y crítico.                                    |

### 3.6 `DeleteUserEndpointTests` — Smoke

#### TC-USER-044 · `shouldDeleteUserSuccessfully`

| Campo                   | Detalle                                                                      |
|-------------------------|------------------------------------------------------------------------------|
| **Test en el proyecto** | `ApiUserControllerTest.DeleteUserEndpointTests.shouldDeleteUserSuccessfully` |
| **Suite**               | **Smoke**                                                                    |
| **Endpoint**            | `DELETE /api/user/{id}`                                                      |
| **Qué hace**            | Verifica el happy path de eliminación de un usuario por id.                  |
| **Mocks que prepara**   | `userService.deleteUser(user.getId())` → no-op.                              |
| **Request**             | `DELETE /api/user/{user.getId()}`.                                           |
| **Qué verifica**        | Status `204` (No Content).                                                   |
| **Por qué es Smoke**    | Operación crítica del módulo (cumple ciclo de vida del usuario).             |

### 3.7 `RegisterEndpointTests` — Smoke

#### TC-USER-026 · `shouldRegisterUserSuccessfully`

| Campo                   | Detalle                                                                                                                                   |
|-------------------------|-------------------------------------------------------------------------------------------------------------------------------------------|
| **Test en el proyecto** | `ApiUserControllerTest.RegisterEndpointTests.shouldRegisterUserSuccessfully`                                                              |
| **Suite**               | **Smoke**                                                                                                                                 |
| **Endpoint**            | `POST /api/user/register`                                                                                                                 |
| **Qué hace**            | Verifica el happy path HTTP del registro: el controller recibe el `UserDTO`, llama al service y devuelve el DTO con `keycloakId` poblado. |
| **Mocks que prepara**   | `userService.register(any(UserDTO.class))` → `user`.                                                                                      |
| **Request**             | `POST /api/user/register` con body JSON: `UserDTO` completo (nombre, apellido, telefono, dni, email, pwd, cvu, alias).                    |
| **Qué verifica**        | Status `200`. `jsonPath.getString("keycloakId") != null`.                                                                                 |
| **Por qué es Smoke**    | Endpoint más crítico del módulo.                                                                                                          |

### 3.8 `LoginEndpointTests` — Smoke

#### TC-USER-028 · `shouldLoginSuccessfullyWithValidCredentials`

| Campo                   | Detalle                                                                                                                                               |
|-------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Test en el proyecto** | `ApiUserControllerTest.LoginEndpointTests.shouldLoginSuccessfullyWithValidCredentials`                                                                |
| **Suite**               | **Smoke**                                                                                                                                             |
| **Endpoint**            | `POST /api/user/login`                                                                                                                                |
| **Qué hace**            | Verifica el happy path HTTP del login: el controller recibe `email`+`pwd`, llama al service y devuelve el `AccessTokenResponse` (serializado a JSON). |
| **Mocks que prepara**   | `userService.login(testEmail, testPassword)` → `AccessTokenResponse` con `token="access-token-123"` y `refreshToken="refresh-token-123"`.             |
| **Request**             | `POST /api/user/login` con body JSON: `UserDTO` con sólo `email` y `pwd`.                                                                             |
| **Qué verifica**        | Status `200`.                                                                                                                                         |
| **Por qué es Smoke**    | Flujo principal de autenticación.                                                                                                                     |

---

## 4. Suite de **Regresión** (11 tests — capa API)

> Cubren todos los caminos negativos de los endpoints críticos: status 400, 404, 500 según el tipo de excepción lanzada por el service.

### 4.1 `RegisterEndpointTests` — Regresión (1 test)

#### TC-USER-027 · `shouldReturn400WhenKeycloakRejectsUser`

| Campo                   | Detalle                                                                                                                                                                        |
|-------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Test en el proyecto** | `ApiUserControllerTest.RegisterEndpointTests.shouldReturn400WhenKeycloakRejectsUser`                                                                                           |
| **Suite**               | **Regresión**                                                                                                                                                                  |
| **Endpoint**            | `POST /api/user/register`                                                                                                                                                      |
| **Qué hace**            | Verifica que cuando el service lanza `UserBadRequestException` (Keycloak rechaza el usuario — p.ej. email duplicado), el `GlobalExceptionHandler` traduce a `400 Bad Request`. |
| **Mocks que prepara**   | `userService.register(any(UserDTO.class))` → lanza `UserBadRequestException("Email already exists")`.                                                                          |
| **Request**             | `POST /api/user/register` con body JSON: `UserDTO` con datos distintos al smoke (DNI y CVU cambiados).                                                                         |
| **Qué verifica**        | Status `400`.                                                                                                                                                                  |

### 4.2 `LoginEndpointTests` — Regresión (2 tests)

#### TC-USER-029 · `shouldReturn404WhenUserDoesNotExist`

| Campo                   | Detalle                                                                                                                                                                |
|-------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Test en el proyecto** | `ApiUserControllerTest.LoginEndpointTests.shouldReturn404WhenUserDoesNotExist`                                                                                         |
| **Suite**               | **Regresión**                                                                                                                                                          |
| **Endpoint**            | `POST /api/user/login`                                                                                                                                                 |
| **Qué hace**            | Verifica que `UserNotFoundException` desde el service se traduce a `404 Not Found`.                                                                                    |
| **Mocks que prepara**   | `Mockito.reset(userService)` (limpia stubs previos del smoke). `userService.login(anyString(), anyString())` → lanza `UserNotFoundException("Usuario no encontrado")`. |
| **Request**             | `POST /api/user/login` con body JSON: `Map {"email":"nonexistent1234@example.com", "pwd":testPassword}`.                                                               |
| **Qué verifica**        | Status `404`.                                                                                                                                                          |

#### TC-USER-030 · `shouldReturn401OnInvalidCredentials`

| Campo                   | Detalle                                                                                                                                                                                                                                               |
|-------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Test en el proyecto** | `ApiUserControllerTest.LoginEndpointTests.shouldReturn401OnInvalidCredentials`                                                                                                                                                                        |
| **Suite**               | **Regresión**                                                                                                                                                                                                                                         |
| **Endpoint**            | `POST /api/user/login`                                                                                                                                                                                                                                |
| **Qué hace**            | Verifica que `UserInvalidCredentialsException` desde el service se traduce a `400 Bad Request` (el nombre del test dice `shouldReturn401` pero el assert verifica `400`, coherente con el `GlobalExceptionHandler` que mapea esta excepción a `400`). |
| **Mocks que prepara**   | `Mockito.reset(userService)`. `userService.login(anyString(), anyString())` → lanza `UserInvalidCredentialsException("Email o contraseña incorrectos")`.                                                                                              |
| **Request**             | `POST /api/user/login` con body JSON: `Map {"email":testEmail, "pwd":"wrongPassword1234"}`.                                                                                                                                                           |
| **Qué verifica**        | Status `400`.                                                                                                                                                                                                                                         |

### 4.3 `LogoutEndpointTests` — Regresión (1 test)

#### TC-USER-032 · `shouldReturn500WhenLogoutFails`

| Campo                   | Detalle                                                                                                    |
|-------------------------|------------------------------------------------------------------------------------------------------------|
| **Test en el proyecto** | `ApiUserControllerTest.LogoutEndpointTests.shouldReturn500WhenLogoutFails`                                 |
| **Suite**               | **Regresión**                                                                                              |
| **Endpoint**            | `POST /api/user/logout`                                                                                    |
| **Qué hace**            | Verifica que `UserInternalServerErrorException` desde el service se traduce a `500 Internal Server Error`. |
| **Mocks que prepara**   | `userService.logout(anyString())` → lanza `UserInternalServerErrorException("Error al cerrar sesión")`.    |
| **Request**             | Header `X-Refresh-Token: invalid-token`.                                                                   |
| **Qué verifica**        | Status `500`.                                                                                              |

### 4.4 `EmailVerificationEndpointTests` — Regresión (3 tests)

#### TC-USER-034 · `shouldReturn404WhenUserNotFoundForVerification`

| Campo                   | Detalle                                                                                                                 |
|-------------------------|-------------------------------------------------------------------------------------------------------------------------|
| **Test en el proyecto** | `ApiUserControllerTest.EmailVerificationEndpointTests.shouldReturn404WhenUserNotFoundForVerification`                   |
| **Suite**               | **Regresión**                                                                                                           |
| **Endpoint**            | `POST /api/user/send-verification?email=…`                                                                              |
| **Qué hace**            | Verifica que `UserNotFoundException` desde el service se traduce a `404`.                                               |
| **Mocks que prepara**   | `userService.sendEmailVerification(anyString())` → lanza `UserNotFoundException("Usuario no encontrado con email: …")`. |
| **Request**             | Query param `email=nonexistent@example.com`.                                                                            |
| **Qué verifica**        | Status `404`.                                                                                                           |

#### TC-USER-035 · `shouldReturn400WhenUserHasNoKeycloakIdForVerification`

| Campo                   | Detalle                                                                                                                           |
|-------------------------|-----------------------------------------------------------------------------------------------------------------------------------|
| **Test en el proyecto** | `ApiUserControllerTest.EmailVerificationEndpointTests.shouldReturn400WhenUserHasNoKeycloakIdForVerification`                      |
| **Suite**               | **Regresión**                                                                                                                     |
| **Endpoint**            | `POST /api/user/send-verification?email=…`                                                                                        |
| **Qué hace**            | Verifica que `UserBadRequestException` (usuario sin `keycloakId`) se traduce a `400`.                                             |
| **Mocks que prepara**   | `userService.sendEmailVerification(anyString())` → lanza `UserBadRequestException("El usuario no tiene un keycloakId asociado")`. |
| **Request**             | Query param `email=test@example.com`.                                                                                             |
| **Qué verifica**        | Status `400`.                                                                                                                     |

#### TC-USER-036 · `shouldReturn500WhenKeycloakFailsForVerification`

| Campo                   | Detalle                                                                                                        |
|-------------------------|----------------------------------------------------------------------------------------------------------------|
| **Test en el proyecto** | `ApiUserControllerTest.EmailVerificationEndpointTests.shouldReturn500WhenKeycloakFailsForVerification`         |
| **Suite**               | **Regresión**                                                                                                  |
| **Endpoint**            | `POST /api/user/send-verification?email=…`                                                                     |
| **Qué hace**            | Verifica que `UserInternalServerErrorException` se traduce a `500`.                                            |
| **Mocks que prepara**   | `userService.sendEmailVerification(anyString())` → lanza `UserInternalServerErrorException("Keycloak error")`. |
| **Request**             | Query param `email=test@example.com`.                                                                          |
| **Qué verifica**        | Status `500`.                                                                                                  |

### 4.5 `ResetPasswordEndpointTests` — Regresión (3 tests)

#### TC-USER-038 · `shouldReturn404WhenUserNotFoundForReset`

| Campo                   | Detalle                                                                                                                |
|-------------------------|------------------------------------------------------------------------------------------------------------------------|
| **Test en el proyecto** | `ApiUserControllerTest.ResetPasswordEndpointTests.shouldReturn404WhenUserNotFoundForReset`                             |
| **Suite**               | **Regresión**                                                                                                          |
| **Endpoint**            | `POST /api/user/reset-password?email=…`                                                                                |
| **Qué hace**            | Verifica que `UserNotFoundException` desde el service se traduce a `404`.                                              |
| **Mocks que prepara**   | `userService.resetPasswordByEmail(anyString())` → lanza `UserNotFoundException("Usuario no encontrado con email: …")`. |
| **Request**             | Query param `email=nonexistent@example.com`.                                                                           |
| **Qué verifica**        | Status `404`.                                                                                                          |

#### TC-USER-039 · `shouldReturn400WhenUserHasNoKeycloakIdForReset`

| Campo                   | Detalle                                                                                                                          |
|-------------------------|----------------------------------------------------------------------------------------------------------------------------------|
| **Test en el proyecto** | `ApiUserControllerTest.ResetPasswordEndpointTests.shouldReturn400WhenUserHasNoKeycloakIdForReset`                                |
| **Suite**               | **Regresión**                                                                                                                    |
| **Endpoint**            | `POST /api/user/reset-password?email=…`                                                                                          |
| **Qué hace**            | Verifica que `UserBadRequestException` (usuario sin `keycloakId`) se traduce a `400`.                                            |
| **Mocks que prepara**   | `userService.resetPasswordByEmail(anyString())` → lanza `UserBadRequestException("El usuario no tiene un keycloakId asociado")`. |
| **Request**             | Query param `email=test@example.com`.                                                                                            |
| **Qué verifica**        | Status `400`.                                                                                                                    |

#### TC-USER-040 · `shouldReturn500WhenKeycloakFailsForReset`

| Campo                   | Detalle                                                                                                       |
|-------------------------|---------------------------------------------------------------------------------------------------------------|
| **Test en el proyecto** | `ApiUserControllerTest.ResetPasswordEndpointTests.shouldReturn500WhenKeycloakFailsForReset`                   |
| **Suite**               | **Regresión**                                                                                                 |
| **Endpoint**            | `POST /api/user/reset-password?email=…`                                                                       |
| **Qué hace**            | Verifica que `UserInternalServerErrorException` se traduce a `500`.                                           |
| **Mocks que prepara**   | `userService.resetPasswordByEmail(anyString())` → lanza `UserInternalServerErrorException("Keycloak error")`. |
| **Request**             | Query param `email=test@example.com`.                                                                         |
| **Qué verifica**        | Status `500`.                                                                                                 |

### 4.6 `GetEmailEndpointTests` — Regresión (1 test)

#### TC-USER-043 · `shouldReturn404WhenUserDoesNotExist`

| Campo                   | Detalle                                                                                       |
|-------------------------|-----------------------------------------------------------------------------------------------|
| **Test en el proyecto** | `ApiUserControllerTest.GetEmailEndpointTests.shouldReturn404WhenUserDoesNotExist`             |
| **Suite**               | **Regresión**                                                                                 |
| **Endpoint**            | `GET /api/user/getEmail?email=…`                                                              |
| **Qué hace**            | Verifica que `UserNotFoundException` desde el service se traduce a `404`.                     |
| **Mocks que prepara**   | `userService.getEmail(anyString())` → lanza `UserNotFoundException("Usuario no encontrado")`. |
| **Request**             | Query param `email=nonexistent@example.com`.                                                  |
| **Qué verifica**        | Status `404`.                                                                                 |

---

## 5. Resumen — Capa API

| Feature           | Smoke | Regresión | # Tests |
|-------------------|-------|-----------|---------|
| Register          | 1     | 1         | 2       |
| Login             | 1     | 2         | 3       |
| Logout            | 1     | 1         | 2       |
| Send Verification | 1     | 3         | 4       |
| Reset Password    | 1     | 3         | 4       |
| Get All Users     | 1     | 0         | 1       |
| Get Email         | 1     | 1         | 2       |
| Delete User       | 1     | 0         | 1       |
| **Total API**     | **8** | **11**    | **19**  |

Cada test tiene su **`TC-USER-XXX`** correspondiente en la planilla [`01-plan-testing.md`](01-plan-testing.md) y vive en el archivo [`ApiUserControllerTest.java`](../../Project/DMH/api-user/src/test/java/com/dmh/UserController/ApiUserControllerTest.java) — **no se duplica código en este documento**.

---

## 6. Resumen consolidado — Cobertura total del módulo

| Capa / Suite              | Smoke  | Regresión | Total  |
|---------------------------|--------|-----------|--------|
| Service (JUnit + Mockito) | 8      | 17        | 25     |
| API (RestAssured)         | 8      | 11        | 19     |
| **Total módulo**          | **16** | **28**    | **44** |

---

## 7. Cómo se ejecutan estos tests

- **No requiere configuración especial.** La suite que corre **todos los tests del módulo** es [`ApiUserApplicationTests.java`](../../Project/DMH/api-user/src/test/java/com/dmh/ApiUserApplicationTests.java).
- Desde `Project/DMH/api-user`:

---

## 8. Referencias

- Producción: [`UserController.java`](../../Project/DMH/api-user/src/main/java/com/dmh/UserController/UserController.java)
- Lógica: [`UserService.java`](../../Project/DMH/api-user/src/main/java/com/dmh/UserService/UserService.java)
- DTO: [`UserDTO.java`](../../Project/DMH/api-user/src/main/java/com/dmh/UserDTO/UserDTO.java)
- Excepciones / respuestas de error: [`GlobalExceptionHandler.java`](../../Project/DMH/api-user/src/main/java/com/dmh/Exceptions/GlobalExceptionHandler.java)
- Tests existentes: [`ApiUserControllerTest.java`](../../Project/DMH/api-user/src/test/java/com/dmh/UserController/ApiUserControllerTest.java)
- Suite que corre todos los tests: [`ApiUserApplicationTests.java`](../../Project/DMH/api-user/src/test/java/com/dmh/ApiUserApplicationTests.java)
- Planilla con los 44 casos: [`01-plan-testing.md`](01-plan-testing.md)
- Documentación de la capa Service: [`02-junit-mockito-tests.md`](02-junit-mockito-tests.md)