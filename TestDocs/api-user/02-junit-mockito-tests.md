# Documentación de Tests · Capa Service (JUnit 5 + Mockito)

> **Microservicio:** `api-user`  
> **Capa bajo prueba:** [`UserService`](../../Project/DMH/api-user/src/main/java/com/dmh/UserService/UserService.java:1) — lógica de negocio.  
> **Stack de testing:** JUnit 5 (Jupiter) + Mockito 5.  
> **Archivo de tests:** [`ApiUserServiceTest.java`](../../Project/DMH/api-user/src/test/java/com/dmh/UserService/ApiUserServiceTest.java) — **25 tests ya implementados** (8 Smoke + 17 Regresión).  
> **Ejecución:** todos los tests del módulo corren juntos vía [`ApiUserApplicationTests.java`](../../Project/DMH/api-user/src/test/java/com/dmh/ApiUserApplicationTests.java) con `mvn test`/`mvn verify`. La separación Service/API es **sólo organizativa**.  
> **Trazabilidad:** cada test referencia el `TC-USER-XXX` de [`01-plan-testing.md`](01-plan-testing.md) y el nombre real del test en el archivo.

> **⚠️ Este documento NO copia el código de los tests existentes** — describe **qué hace cada test, qué mocks prepara, qué verifica y a qué suite pertenece**, siguiendo buenas prácticas de documentación.

---

## 1. Convenciones y buenas prácticas aplicadas en estos tests

1. **Una clase de test por clase productiva** → `UserService` se prueba en `ApiUserServiceTest`.
2. **Patrón AAA (Arrange – Act – Assert)** en cada test (vía comentarios `// given`, `// when`, `// then`).
3. **`@DisplayName` legible** en cada test (no se usan tags `@Tag` para smoke/regresión — la clasificación es **conceptual** y se mantiene en este `TestDocs/`).
4. **Agrupación por feature con `@Nested`** (Register, Login, Logout, EmailVerification, PasswordReset, SaveUser, GetUsers, GetEmail, GetUserNameEmail, DeleteUser).
5. **`@ExtendWith(MockitoExtension.class)` + `STRICT_STUBS`** → detecta stubs no usados y/o argumentos mal matcheados.
6. **Solo se mockean los colaboradores** (`UserRepository`, `UserMapper`, `KeycloakClient`, `KeycloakAuth`). El SUT (`UserService`) es real.
7. **Datos únicos por ejecución** (`UUID`, `System.currentTimeMillis()`) en `setUp()` para evitar interferencia entre tests.
8. **Verificación de interacciones** con `verify(...)` — asegura efectos colaterales en BD/Keycloak.
9. **Verificación de excepciones** con `assertThrows`.
10. **Tests independientes**: cada test parte del estado creado en `@BeforeEach`.

---

## 2. Estructura de la clase de test

```
src/test/java/com/dmh/UserService/
└── ApiUserServiceTest.java
```

La clase está anotada con `@ExtendWith(MockitoExtension.class)` y `@DisplayName("UserService — Unit Tests")`. Sus campos son:

- **Mocks**: `userRepository`, `userMapper`, `keycloakClient`, `keycloakAuth` (`@Mock`).
- **SUT**: `userService` (`@InjectMocks`).
- **Datos**: `userDTO`, `user`, `testKeycloakId`, `testEmail`, `testPassword` (inicializados en `@BeforeEach setUp()` con un email único por ejecución).

Los tests se organizan en **10 `@Nested` classes** que agrupan por feature.

---

## 3. Suite de **Smoke** (8 tests — capa Service)

> Validan el camino feliz de cada feature crítica. Cubren: registro, login, logout, send-verification, reset-password, getUsers, getEmail, deleteUser.

### 3.1 `RegisterTests` — Smoke

#### TC-USER-001 · `ShouldRegisterUserSuccessfully`

| Campo                             | Detalle                                                                                                                                                                                                                                          |
|-----------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Test en el proyecto**           | `ApiUserServiceTest.RegisterTests.ShouldRegisterUserSuccessfully`                                                                                                                                                                                |
| **Suite**                         | **Smoke**                                                                                                                                                                                                                                        |
| **Qué hace**                      | Verifica el happy path del registro: `userService.register(dto)` crea el usuario en Keycloak, lo mapea a entidad y lo persiste en BD.                                                                                                            |
| **Mocks que prepara**             | `keycloakClient.createUser(...)` → devuelve `testKeycloakId`. `userMapper.DTOtoUser(any())` → devuelve `user`. `userRepository.save(any())` → devuelve `user`.                                                                                   |
| **Qué verifica (asserts/verify)** | `result != null`, `result.keycloakId == testKeycloakId`, `result.email == testEmail`. Verifica que `keycloakClient.createUser` se llamó con los 4 args correctos, que `userMapper.DTOtoUser` y `userRepository.save` se llamaron 1 vez cada uno. |
| **Por qué es Smoke**              | Es el happy path del endpoint más crítico del módulo (sin él no hay usuarios).                                                                                                                                                                   |

### 3.2 `LoginTests` — Smoke

#### TC-USER-005 · `ShouldLoginSuccessfully`

| Campo                   | Detalle                                                                                                                                                                                                   |
|-------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Test en el proyecto** | `ApiUserServiceTest.LoginTests.ShouldLoginSuccessfully`                                                                                                                                                   |
| **Suite**               | **Smoke**                                                                                                                                                                                                 |
| **Qué hace**            | Verifica que `userService.login(email, pwd)` consulta el repo y delega en Keycloak para devolver un `AccessTokenResponse` válido.                                                                         |
| **Mocks que prepara**   | `userRepository.findByEmail(testEmail)` → `Optional.of(user)`. `keycloakAuth.login(testEmail, testPassword)` → `AccessTokenResponse` con `access_token="access-token"` y `refresh_token="refresh-token"`. |
| **Qué verifica**        | `result != null`, `result.token == "access-token"`. Verifica que `userRepository.findByEmail` y `keycloakAuth.login` se llamaron 1 vez cada uno.                                                          |
| **Por qué es Smoke**    | Login es el flujo principal de autenticación; sin él nada funciona.                                                                                                                                       |

### 3.3 `LogoutTests` — Smoke

#### TC-USER-009 · `ShouldLogoutSuccessfully`

| Campo                   | Detalle                                                                                                                   |
|-------------------------|---------------------------------------------------------------------------------------------------------------------------|
| **Test en el proyecto** | `ApiUserServiceTest.LogoutTests.ShouldLogoutSuccessfully`                                                                 |
| **Suite**               | **Smoke**                                                                                                                 |
| **Qué hace**            | Verifica que `userService.logout(token)` cierra sesión delegando en `KeycloakAuth.logout(token)` sin lanzar excepciones.  |
| **Mocks que prepara**   | `keycloakAuth.logout("refresh-token")` → no-op.                                                                           |
| **Qué verifica**        | Que el método retorna sin lanzar excepciones. Verifica que `keycloakAuth.logout` se llamó exactamente 1 vez con el token. |
| **Por qué es Smoke**    | Logout es flujo crítico de sesión (la app no es utilizable si el usuario no puede salir).                                 |

### 3.4 `EmailVerificationTests` — Smoke

#### TC-USER-011 · `ShouldSendEmailVerificationSuccessfully`

| Campo                   | Detalle                                                                                                                                                   |
|-------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Test en el proyecto** | `ApiUserServiceTest.EmailVerificationTests.ShouldSendEmailVerificationSuccessfully`                                                                       |
| **Suite**               | **Smoke**                                                                                                                                                 |
| **Qué hace**            | Verifica el happy path del envío de email de verificación: el service busca el usuario por email y llama a Keycloak para enviar el mail.                  |
| **Mocks que prepara**   | `userRepository.findByEmail(testEmail)` → `Optional.of(user)` (con `keycloakId` seteado). `keycloakClient.sendEmailVerification(testKeycloakId)` → no-op. |
| **Qué verifica**        | Que el método retorna sin lanzar. Verifica que `userRepository.findByEmail` y `keycloakClient.sendEmailVerification` se llamaron 1 vez cada uno.          |
| **Por qué es Smoke**    | El envío de verificación de email es un paso crítico para activar la cuenta.                                                                              |

### 3.5 `PasswordResetTests` — Smoke

#### TC-USER-015 · `ShouldResetPasswordSuccessfully`

| Campo                   | Detalle                                                                                                                                           |
|-------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------|
| **Test en el proyecto** | `ApiUserServiceTest.PasswordResetTests.ShouldResetPasswordSuccessfully`                                                                           |
| **Suite**               | **Smoke**                                                                                                                                         |
| **Qué hace**            | Verifica el happy path del reset de contraseña: el service busca el usuario por email y dispara el flujo de reset en Keycloak.                    |
| **Mocks que prepara**   | `userRepository.findByEmail(testEmail)` → `Optional.of(user)` (con `keycloakId` seteado). `keycloakClient.resetPassword(testKeycloakId)` → no-op. |
| **Qué verifica**        | Que el método retorna sin lanzar. Verifica que `userRepository.findByEmail` y `keycloakClient.resetPassword` se llamaron 1 vez cada uno.          |
| **Por qué es Smoke**    | Reset de contraseña es flujo crítico de recuperación de cuenta.                                                                                   |

### 3.6 `GetUsersTests` — Smoke

#### TC-USER-020 · `ShouldGetAllUsersSuccessfully`

| Campo                   | Detalle                                                                                         |
|-------------------------|-------------------------------------------------------------------------------------------------|
| **Test en el proyecto** | `ApiUserServiceTest.GetUsersTests.ShouldGetAllUsersSuccessfully`                                |
| **Suite**               | **Smoke**                                                                                       |
| **Qué hace**            | Verifica que `userService.getUsers()` devuelve la lista de usuarios existentes mapeada a DTO.   |
| **Mocks que prepara**   | `userRepository.findAll()` → `List.of(user)`. `userMapper.UsertoDTO(any())` → `userDTO`.        |
| **Qué verifica**        | `result != null`, `result.size() == 1`. Verifica que `userRepository.findAll()` se llamó 1 vez. |
| **Por qué es Smoke**    | Listar usuarios es una operación central de administración/consulta del módulo.                 |

### 3.7 `GetEmailTests` — Smoke

#### TC-USER-022 · `ShouldGetEmailSuccessfully`

| Campo                   | Detalle                                                                                            |
|-------------------------|----------------------------------------------------------------------------------------------------|
| **Test en el proyecto** | `ApiUserServiceTest.GetEmailTests.ShouldGetEmailSuccessfully`                                      |
| **Suite**               | **Smoke**                                                                                          |
| **Qué hace**            | Verifica que `userService.getEmail(email)` devuelve el email del usuario cuando existe.            |
| **Mocks que prepara**   | `userRepository.findByEmail(testEmail)` → `Optional.of(user)`.                                     |
| **Qué verifica**        | Que el resultado es igual a `testEmail`. Verifica que `userRepository.findByEmail` se llamó 1 vez. |
| **Por qué es Smoke**    | Endpoint de lookup básico; es un happy path corto y crítico.                                       |

### 3.8 `DeleteUserTests` — Regresión (sin happy path en Service) — ver § 4.10

> Nota: el happy path de eliminar usuario (`deleteUser(id)` → `repository.deleteById(id)`) está clasificado como **Regresión** porque, en la práctica, está cubierto a nivel **API (Smoke)** por `TC-USER-044`. No se duplica el caso en Service.

---

## 4. Suite de **Regresión** (17 tests — capa Service)

> Cubren casos negativos, bordes y comportamientos auxiliares que no son happy path crítico.

### 4.1 `RegisterTests` — Regresión (3 tests)

#### TC-USER-002 · `ShouldThrowUserBadRequestExceptionWhenKeycloakReturnsBadRequest`

| Campo                   | Detalle                                                                                                                                                           |
|-------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Test en el proyecto** | `ApiUserServiceTest.RegisterTests.ShouldThrowUserBadRequestExceptionWhenKeycloakReturnsBadRequest`                                                                |
| **Suite**               | **Regresión**                                                                                                                                                     |
| **Qué hace**            | Verifica que cuando Keycloak devuelve `BadRequestException` (típico: email ya registrado), el service la traduce a `UserBadRequestException` y NO persiste en BD. |
| **Mocks que prepara**   | `keycloakClient.createUser(...)` → lanza `BadRequestException("Email already exists")`.                                                                           |
| **Qué verifica**        | `assertThrows(UserBadRequestException.class, () -> userService.register(dto))`. (No hay verify explícito de `save` porque se usa `never()` en variantes.)         |

#### TC-USER-003 · `ShouldThrowUserInternalServerErrorExceptionWhenKeycloakReturnsWebApplicationException`

| Campo                   | Detalle                                                                                                                      |
|-------------------------|------------------------------------------------------------------------------------------------------------------------------|
| **Test en el proyecto** | `ApiUserServiceTest.RegisterTests.ShouldThrowUserInternalServerErrorExceptionWhenKeycloakReturnsWebApplicationException`     |
| **Suite**               | **Regresión**                                                                                                                |
| **Qué hace**            | Verifica que una `WebApplicationException` desde Keycloak (típicamente 5xx) se traduce a `UserInternalServerErrorException`. |
| **Mocks que prepara**   | `keycloakClient.createUser(...)` → lanza `WebApplicationException("Keycloak error")`.                                        |
| **Qué verifica**        | `assertThrows(UserInternalServerErrorException.class, ...)`.                                                                 |

#### TC-USER-004 · `ShouldThrowUserInternalServerErrorExceptionOnGenericException`

| Campo                   | Detalle                                                                                                                                                                     |
|-------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Test en el proyecto** | `ApiUserServiceTest.RegisterTests.ShouldThrowUserInternalServerErrorExceptionOnGenericException`                                                                            |
| **Suite**               | **Regresión**                                                                                                                                                               |
| **Qué hace**            | Verifica que cualquier excepción no controlada (`RuntimeException` genérica) se traduce a `UserInternalServerErrorException` (cubre el `catch (Exception ex)` del service). |
| **Mocks que prepara**   | `keycloakClient.createUser(...)` → lanza `RuntimeException("Unexpected error")`.                                                                                            |
| **Qué verifica**        | `assertThrows(UserInternalServerErrorException.class, ...)`.                                                                                                                |

### 4.2 `LoginTests` — Regresión (3 tests)

#### TC-USER-006 · `ShouldThrowUserNotFoundExceptionWhenUserDoesNotExist`

| Campo                   | Detalle                                                                                                                   |
|-------------------------|---------------------------------------------------------------------------------------------------------------------------|
| **Test en el proyecto** | `ApiUserServiceTest.LoginTests.ShouldThrowUserNotFoundExceptionWhenUserDoesNotExist`                                      |
| **Suite**               | **Regresión**                                                                                                             |
| **Qué hace**            | Verifica que si el email no existe en BD, el service lanza `UserNotFoundException` y NO llama a Keycloak.                 |
| **Mocks que prepara**   | `userRepository.findByEmail(testEmail)` → `Optional.empty()`.                                                             |
| **Qué verifica**        | `assertThrows(UserNotFoundException.class, ...)`. Implicitamente verifica que no se llega a invocar `keycloakAuth.login`. |

#### TC-USER-007 · `ShouldThrowUserInvalidCredentialsExceptionOn401Error`

| Campo                   | Detalle                                                                                                                                             |
|-------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------|
| **Test en el proyecto** | `ApiUserServiceTest.LoginTests.ShouldThrowUserInvalidCredentialsExceptionOn401Error`                                                                |
| **Suite**               | **Regresión**                                                                                                                                       |
| **Qué hace**            | Verifica que cuando Keycloak devuelve un error 401 (credenciales inválidas), el service lo traduce a `UserInvalidCredentialsException`.             |
| **Mocks que prepara**   | `userRepository.findByEmail(testEmail)` → `Optional.of(user)`. `keycloakAuth.login(testEmail, pwd)` → lanza `RuntimeException("401 Unauthorized")`. |
| **Qué verifica**        | `assertThrows(UserInvalidCredentialsException.class, ...)`.                                                                                         |

#### TC-USER-008 · `ShouldThrowUserInternalServerErrorExceptionOnOtherErrors`

| Campo                   | Detalle                                                                                                                                               |
|-------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Test en el proyecto** | `ApiUserServiceTest.LoginTests.ShouldThrowUserInternalServerErrorExceptionOnOtherErrors`                                                              |
| **Suite**               | **Regresión**                                                                                                                                         |
| **Qué hace**            | Verifica que cualquier otro error de Keycloak (timeout, 5xx, etc.) se traduce a `UserInternalServerErrorException`.                                   |
| **Mocks que prepara**   | `userRepository.findByEmail(testEmail)` → `Optional.of(user)`. `keycloakAuth.login(testEmail, pwd)` → lanza `RuntimeException("Connection timeout")`. |
| **Qué verifica**        | `assertThrows(UserInternalServerErrorException.class, ...)`.                                                                                          |

### 4.3 `LogoutTests` — Regresión (1 test)

#### TC-USER-010 · `ShouldThrowUserInternalServerErrorExceptionWhenLogoutFails`

| Campo                   | Detalle                                                                                                                        |
|-------------------------|--------------------------------------------------------------------------------------------------------------------------------|
| **Test en el proyecto** | `ApiUserServiceTest.LogoutTests.ShouldThrowUserInternalServerErrorExceptionWhenLogoutFails`                                    |
| **Suite**               | **Regresión**                                                                                                                  |
| **Qué hace**            | Verifica que si `KeycloakAuth.logout(token)` falla (timeout, 5xx), el service lo traduce a `UserInternalServerErrorException`. |
| **Mocks que prepara**   | `keycloakAuth.logout("invalid-token")` → lanza `RuntimeException("Logout failed")`.                                            |
| **Qué verifica**        | `assertThrows(UserInternalServerErrorException.class, ...)`.                                                                   |

### 4.4 `EmailVerificationTests` — Regresión (3 tests)

#### TC-USER-012 · `ShouldThrowUserNotFoundExceptionWhenUserDoesNotExistForVerification`

| Campo                   | Detalle                                                                                                                    |
|-------------------------|----------------------------------------------------------------------------------------------------------------------------|
| **Test en el proyecto** | `ApiUserServiceTest.EmailVerificationTests.ShouldThrowUserNotFoundExceptionWhenUserDoesNotExistForVerification`            |
| **Suite**               | **Regresión**                                                                                                              |
| **Qué hace**            | Verifica que cuando el email no existe en BD, `sendEmailVerification` lanza `UserNotFoundException` y NO llama a Keycloak. |
| **Mocks que prepara**   | `userRepository.findByEmail(testEmail)` → `Optional.empty()`.                                                              |
| **Qué verifica**        | `assertThrows(UserNotFoundException.class, ...)`. `verify(keycloakClient, never()).sendEmailVerification(anyString())`.    |

#### TC-USER-013 · `ShouldThrowUserBadRequestExceptionWhenUserHasNoKeycloakId`

| Campo                   | Detalle                                                                                                                                                   |
|-------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Test en el proyecto** | `ApiUserServiceTest.EmailVerificationTests.ShouldThrowUserBadRequestExceptionWhenUserHasNoKeycloakId`                                                     |
| **Suite**               | **Regresión**                                                                                                                                             |
| **Qué hace**            | Verifica que un usuario en BD sin `keycloakId` no puede disparar envío de verificación: el service lanza `UserBadRequestException` y NO llama a Keycloak. |
| **Mocks que prepara**   | `user.setKeycloackId(null)`; `userRepository.findByEmail(testEmail)` → `Optional.of(user)`.                                                               |
| **Qué verifica**        | `assertThrows(UserBadRequestException.class, ...)`. `verify(keycloakClient, never()).sendEmailVerification(anyString())`.                                 |

#### TC-USER-014 · `ShouldThrowExceptionWhenKeycloakFails`

| Campo                   | Detalle                                                                                                                                                                                                                                                                                          |
|-------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Test en el proyecto** | `ApiUserServiceTest.EmailVerificationTests.ShouldThrowExceptionWhenKeycloakFails`                                                                                                                                                                                                                |
| **Suite**               | **Regresión**                                                                                                                                                                                                                                                                                    |
| **Qué hace**            | Verifica el comportamiento actual del service ante una excepción de Keycloak: **la propaga** (no la envuelve en `UserInternalServerErrorException`). Esto está documentado como **refactor pendiente**: `sendEmailVerification` debería envolver errores de Keycloak igual que `login`/`logout`. |
| **Mocks que prepara**   | `userRepository.findByEmail(testEmail)` → `Optional.of(user)`. `keycloakClient.sendEmailVerification(testKeycloakId)` → lanza `RuntimeException("Email service unavailable")`.                                                                                                                   |
| **Qué verifica**        | `assertThrows(RuntimeException.class, ...)`.                                                                                                                                                                                                                                                     |

### 4.5 `PasswordResetTests` — Regresión (3 tests)

#### TC-USER-016 · `ShouldThrowUserNotFoundExceptionWhenUserDoesNotExistForReset`

| Campo                   | Detalle                                                                                                             |
|-------------------------|---------------------------------------------------------------------------------------------------------------------|
| **Test en el proyecto** | `ApiUserServiceTest.PasswordResetTests.ShouldThrowUserNotFoundExceptionWhenUserDoesNotExistForReset`                |
| **Suite**               | **Regresión**                                                                                                       |
| **Qué hace**            | Verifica que cuando el email no existe, `resetPasswordByEmail` lanza `UserNotFoundException` y NO llama a Keycloak. |
| **Mocks que prepara**   | `userRepository.findByEmail(testEmail)` → `Optional.empty()`.                                                       |
| **Qué verifica**        | `assertThrows(UserNotFoundException.class, ...)`. `verify(keycloakClient, never()).resetPassword(anyString())`.     |

#### TC-USER-017 · `ShouldThrowUserBadRequestExceptionWhenUserHasNoKeycloakIdForReset`

| Campo                   | Detalle                                                                                                                                         |
|-------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------|
| **Test en el proyecto** | `ApiUserServiceTest.PasswordResetTests.ShouldThrowUserBadRequestExceptionWhenUserHasNoKeycloakIdForReset`                                       |
| **Suite**               | **Regresión**                                                                                                                                   |
| **Qué hace**            | Verifica que un usuario con `keycloakId` blank/vacío no puede disparar reset: el service lanza `UserBadRequestException` y NO llama a Keycloak. |
| **Mocks que prepara**   | `user.setKeycloackId("")`; `userRepository.findByEmail(testEmail)` → `Optional.of(user)`.                                                       |
| **Qué verifica**        | `assertThrows(UserBadRequestException.class, ...)`. `verify(keycloakClient, never()).resetPassword(anyString())`.                               |

#### TC-USER-018 · `ShouldThrowExceptionWhenKeycloakResetFails`

| Campo                   | Detalle                                                                                                                                                            |
|-------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Test en el proyecto** | `ApiUserServiceTest.PasswordResetTests.ShouldThrowExceptionWhenKeycloakResetFails`                                                                                 |
| **Suite**               | **Regresión**                                                                                                                                                      |
| **Qué hace**            | Verifica el comportamiento actual: la excepción de Keycloak se **propaga** (no se envuelve). Mismo **refactor pendiente** que `TC-USER-014`.                       |
| **Mocks que prepara**   | `userRepository.findByEmail(testEmail)` → `Optional.of(user)`. `keycloakClient.resetPassword(testKeycloakId)` → lanza `RuntimeException("Password reset failed")`. |
| **Qué verifica**        | `assertThrows(RuntimeException.class, ...)`.                                                                                                                       |

### 4.6 `SaveUserTests` — Regresión (1 test)

#### TC-USER-019 · `ShouldSaveUserSuccessfully`

| Campo                            | Detalle                                                                                                                                                            |
|----------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Test en el proyecto**          | `ApiUserServiceTest.SaveUserTests.ShouldSaveUserSuccessfully`                                                                                                      |
| **Suite**                        | **Regresión**                                                                                                                                                      |
| **Qué hace**                     | Verifica que `userService.saveUser(dto)` ejecuta el flujo completo de persistencia: mapea DTO→Entity, guarda en BD y vuelve a mapear Entity→DTO.                   |
| **Mocks que prepara**            | `userMapper.DTOtoUser(any())` → `user`. `userRepository.save(any())` → `user`. `userMapper.UsertoDTO(any())` → `userDTO`.                                          |
| **Qué verifica**                 | `result != null`, `result.email == userDTO.email`. Verifica que `userMapper.DTOtoUser`, `userRepository.save` y `userMapper.UsertoDTO` se llamaron 1 vez cada uno. |
| **Por qué Regresión (no Smoke)** | Endpoint auxiliar (`saveUser`) — no es flujo principal de usuario, no es consumido directamente desde el controller vía HTTP en este Sprint.                       |

### 4.7 `GetUsersTests` — Regresión (1 test)

#### TC-USER-021 · `ShouldReturnEmptyListWhenNoUsersExist`

| Campo                   | Detalle                                                                                                     |
|-------------------------|-------------------------------------------------------------------------------------------------------------|
| **Test en el proyecto** | `ApiUserServiceTest.GetUsersTests.ShouldReturnEmptyListWhenNoUsersExist`                                    |
| **Suite**               | **Regresión**                                                                                               |
| **Qué hace**            | Verifica el borde: con BD vacía, `userService.getUsers()` devuelve una lista vacía (no null, no excepción). |
| **Mocks que prepara**   | `userRepository.findAll()` → `List.of()`.                                                                   |
| **Qué verifica**        | `result != null`, `result.isEmpty()`.                                                                       |

### 4.8 `GetEmailTests` — Regresión (1 test)

#### TC-USER-023 · `ShouldThrowUserNotFoundExceptionWhenUserDoesNotExist`

| Campo                   | Detalle                                                                                              |
|-------------------------|------------------------------------------------------------------------------------------------------|
| **Test en el proyecto** | `ApiUserServiceTest.GetEmailTests.ShouldThrowUserNotFoundExceptionWhenUserDoesNotExist`              |
| **Suite**               | **Regresión**                                                                                        |
| **Qué hace**            | Verifica que cuando el email no existe, `userService.getEmail(email)` lanza `UserNotFoundException`. |
| **Mocks que prepara**   | `userRepository.findByEmail(testEmail)` → `Optional.empty()`.                                        |
| **Qué verifica**        | `assertThrows(UserNotFoundException.class, ...)`.                                                    |

### 4.9 `GetUserNameEmailTests` — Regresión (1 test)

#### TC-USER-024 · `ShouldGetUserNameEmailSuccessfully`

| Campo                            | Detalle                                                                                                  |
|----------------------------------|----------------------------------------------------------------------------------------------------------|
| **Test en el proyecto**          | `ApiUserServiceTest.GetUserNameEmailTests.ShouldGetUserNameEmailSuccessfully`                            |
| **Suite**                        | **Regresión**                                                                                            |
| **Qué hace**                     | Verifica que `userService.getUserNameEmail()` devuelve la lista de usuarios con nombre y email mapeados. |
| **Mocks que prepara**            | `userRepository.findAll()` → `List.of(user)`. `userMapper.UsertoDTO(any())` → `userDTO`.                 |
| **Qué verifica**                 | `result != null`, `result.size() == 1`. Verifica que `userRepository.findAll()` se llamó 1 vez.          |
| **Por qué Regresión (no Smoke)** | Endpoint auxiliar (`getUserNameEmail`) — no es flujo crítico de negocio.                                 |

### 4.10 `DeleteUserTests` — Regresión (1 test)

#### TC-USER-025 · `ShouldDeleteUserSuccessfully`

| Campo                            | Detalle                                                                                                         |
|----------------------------------|-----------------------------------------------------------------------------------------------------------------|
| **Test en el proyecto**          | `ApiUserServiceTest.DeleteUserTests.ShouldDeleteUserSuccessfully`                                               |
| **Suite**                        | **Regresión**                                                                                                   |
| **Qué hace**                     | Verifica que `userService.deleteUser(id)` delega la eliminación en `userRepository.deleteById(id)`.             |
| **Mocks que prepara**            | `userRepository.deleteById(id)` → no-op.                                                                        |
| **Qué verifica**                 | Verifica que `userRepository.deleteById(id)` se llamó 1 vez.                                                    |
| **Por qué Regresión (no Smoke)** | El happy path de eliminación ya está cubierto en la **capa API (Smoke)** por `TC-USER-044` — no se duplica acá. |

---

## 5. Resumen — Capa Service

| Feature             | Smoke | Regresión | # Tests |
|---------------------|-------|-----------|---------|
| Register            | 1     | 3         | 4       |
| Login               | 1     | 3         | 4       |
| Logout              | 1     | 1         | 2       |
| Send Verification   | 1     | 3         | 4       |
| Reset Password      | 1     | 3         | 4       |
| Save User           | 0     | 1         | 1       |
| Get Users           | 1     | 1         | 2       |
| Get Email           | 1     | 1         | 2       |
| Get User Name Email | 0     | 1         | 1       |
| Delete User         | 0     | 1         | 1       |
| **Total Service**   | **8** | **17**    | **25**  |

Cada test tiene su **`TC-USER-XXX`** correspondiente en la planilla [`01-plan-testing.md`](01-plan-testing.md) y vive en el archivo [`ApiUserServiceTest.java`](../../Project/DMH/api-user/src/test/java/com/dmh/UserService/ApiUserServiceTest.java) — **no se duplica código en este documento**.

---

## 6. Cómo se ejecutan estos tests

- **No requiere configuración especial.** La suite que corre **todos los tests del módulo** es [`ApiUserApplicationTests.java`](../../Project/DMH/api-user/src/test/java/com/dmh/ApiUserApplicationTests.java).
- Desde `Project/DMH/api-user`:

---

## 7. Referencias

- Producción: [`UserService.java`](../../Project/DMH/api-user/src/main/java/com/dmh/UserService/UserService.java)
- Tests existentes: [`ApiUserServiceTest.java`](../../Project/DMH/api-user/src/test/java/com/dmh/UserService/ApiUserServiceTest.java)
- Excepciones: `Project/DMH/api-user/src/main/java/com/dmh/Exceptions/`
- DTO: [`UserDTO.java`](../../Project/DMH/api-user/src/main/java/com/dmh/UserDTO/UserDTO.java)
- Planilla con los 44 casos: [`01-plan-testing.md`](01-plan-testing.md)
- Tests de la capa API (RestAssured): [`03-restassured-tests.md`](03-restassured-tests.md)