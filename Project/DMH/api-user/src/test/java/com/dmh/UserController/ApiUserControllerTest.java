package com.dmh.UserController;

import com.dmh.Entity.User;
import com.dmh.Exceptions.UserBadRequestException;
import com.dmh.Exceptions.UserInternalServerErrorException;
import com.dmh.Exceptions.UserInvalidCredentialsException;
import com.dmh.Exceptions.UserNotFoundException;
import com.dmh.Keycloak.KeycloakAuth;
import com.dmh.Keycloak.KeycloakClient;
import com.dmh.UserDTO.UserDTO;
import com.dmh.UserMapper.UserMapper;
import com.dmh.UserRepository.UserRepository;
import com.dmh.UserService.UserService;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.keycloak.representations.AccessTokenResponse;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import java.util.List;
import java.util.UUID;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("UserController API Integration Tests")
public class ApiUserControllerTest {

    @LocalServerPort
    private int port;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private UserMapper userMapper;

    @MockitoBean
    private KeycloakClient keycloakClient;

    @MockitoBean
    private KeycloakAuth keycloakAuth;

    private String baseUri;
    private String testEmail;
    private String testPassword;
    private User user;

    @BeforeEach
    void setUp() {

        Mockito.reset(userService,userRepository, userMapper, keycloakClient, keycloakAuth);
        Mockito.clearInvocations(userService);

        baseUri = "http://localhost:" + port + "/api/user";
        String testKeycloakId = UUID.randomUUID().toString();
        testEmail = "john.doe." + System.currentTimeMillis() + "@example.com";
        testPassword = "Password123!";

        user = new User();
        user.setId(UUID.randomUUID());
        user.setNombre("John");
        user.setApellido("Doe");
        user.setTelefono("1234567890");
        user.setDni("12345678");
        user.setEmail(testEmail);
        user.setKeycloackId(testKeycloakId);
    }

    @Nested
    @DisplayName("Register Endpoint Tests")
    class RegisterEndpointTests {

        @Test
        @DisplayName("POST /register - should register user successfully")
        void shouldRegisterUserSuccessfully() {
            when(userService.register(any(UserDTO.class))).thenReturn(user);

            UserDTO userDTO = new UserDTO();
            userDTO.setNombre("John");
            userDTO.setApellido("Doe");
            userDTO.setTelefono("1234567890");
            userDTO.setDni("12345678");
            userDTO.setEmail(testEmail);
            userDTO.setPwd(testPassword);
            userDTO.setCvu("1234567890123456789012");
            userDTO.setAlias("john.doe.test");

            Response response = given()
                    .contentType(ContentType.JSON)
                    .body(userDTO)
                    .when()
                    .post(baseUri + "/register");

            assertEquals(200, response.statusCode());
            assertNotNull(response.jsonPath().getString("keycloakId"));
        }

        @Test
        @DisplayName("POST /register - should return 400 when UserBadRequestException is thrown")
        void shouldReturn400WhenKeycloakRejectsUser() {
            doThrow(new UserBadRequestException("Email already exists"))
                    .when(userService).register(any(UserDTO.class));

            UserDTO userDTO = new UserDTO();
            userDTO.setNombre("John");
            userDTO.setApellido("Doe");
            userDTO.setTelefono("1234567890");
            userDTO.setDni("12345679");
            userDTO.setEmail(testEmail);
            userDTO.setPwd(testPassword);
            userDTO.setCvu("1234567890123456789013");
            userDTO.setAlias("john.doe.test2");

            Response response = given()
                    .contentType(ContentType.JSON)
                    .body(userDTO)
                    .when()
                    .post(baseUri + "/register");

            assertEquals(400, response.statusCode());
        }
    }

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Login Endpoint Tests")
    class LoginEndpointTests {

        @Test
        @Order(1)
        @DisplayName("POST /login - should login successfully with valid credentials")
        void shouldLoginSuccessfullyWithValidCredentials() {
            AccessTokenResponse tokenResponse = new AccessTokenResponse();
            tokenResponse.setToken("access-token-123");
            tokenResponse.setRefreshToken("refresh-token-123");

            when(userService.login(testEmail, testPassword)).thenReturn(tokenResponse);

            UserDTO loginDTO = new UserDTO();
            loginDTO.setEmail(testEmail);
            loginDTO.setPwd(testPassword);

            Response response = given()
                    .contentType(ContentType.JSON)
                    .body(loginDTO)
                    .when()
                    .post(baseUri + "/login");

            assertEquals(200, response.statusCode());
        }

        @Test
        @Order(2)
        @DisplayName("POST /login - should return 404 when user does not exist")
        void shouldReturn404WhenUserDoesNotExist() {

            Mockito.reset(userService);
            when(userService.login(anyString(), anyString()))
                    .thenThrow(new UserNotFoundException("Usuario no encontrado"));

            java.util.Map<String, String> loginBody = new java.util.HashMap<>();
            loginBody.put("email", "nonexistent1234@example.com");
            loginBody.put("pwd", testPassword);

            Response response = given()
                    .contentType(ContentType.JSON)
                    .body(loginBody)
                    .when()
                    .post(baseUri + "/login");

            assertEquals(404, response.statusCode());
        }

        @Test
        @Order(3)
        @DisplayName("POST /login - should return 400 on invalid credentials")
        void shouldReturn401OnInvalidCredentials() {

            Mockito.reset(userService);

            when(userService.login(anyString(), anyString()))
                    .thenThrow(new UserInvalidCredentialsException("Email o contraseña incorrectos"));

            java.util.Map<String, String> loginBody = new java.util.HashMap<>();
            loginBody.put("email", testEmail);
            loginBody.put("pwd", "wrongPassword1234");

            Response response = given()
                    .contentType(ContentType.JSON)
                    .body(loginBody)
                    .when()
                    .post(baseUri + "/login");

            assertEquals(400, response.statusCode());
        }
    }

    @Nested
    @DisplayName("Logout Endpoint Tests")
    class LogoutEndpointTests {

        @Test
        @DisplayName("POST /logout - should logout successfully with valid refresh token")
        void shouldLogoutSuccessfullyWithValidRefreshToken() {
            String refreshToken = "valid-refresh-token";
            doNothing().when(userService).logout(refreshToken);

            Response response = given()
                    .header("X-Refresh-Token", refreshToken)
                    .when()
                    .post(baseUri + "/logout");

            assertEquals(200, response.statusCode());
        }

        @Test
        @DisplayName("POST /logout - should return 500 when logout fails")
        void shouldReturn500WhenLogoutFails() {
            doThrow(new UserInternalServerErrorException("Error al cerrar sesión"))
                    .when(userService).logout(anyString());

            Response response = given()
                    .header("X-Refresh-Token", "invalid-token")
                    .when()
                    .post(baseUri + "/logout");

            assertEquals(500, response.statusCode());
        }
    }

    @Nested
    @DisplayName("Email Verification Endpoint Tests")
    class EmailVerificationEndpointTests {

        @Test
        @DisplayName("POST /send-verification?email=... - should send verification email")
        void shouldSendVerificationEmail() {
            doNothing().when(userService).sendEmailVerification(testEmail);

            Response response = given()
                    .param("email", testEmail)
                    .when()
                    .post(baseUri + "/send-verification");

            assertEquals(200, response.statusCode());
            verify(userService).sendEmailVerification(testEmail);
        }

        @Test
        @DisplayName("POST /send-verification?email=... - should return 404 when user not found")
        void shouldReturn404WhenUserNotFoundForVerification() {
            doThrow(new UserNotFoundException("Usuario no encontrado con email: " + testEmail))
                    .when(userService).sendEmailVerification(anyString());

            Response response = given()
                    .param("email", "nonexistent@example.com")
                    .when()
                    .post(baseUri + "/send-verification");

            assertEquals(404, response.statusCode());
        }

        @Test
        @DisplayName("POST /send-verification?email=... - should return 400 when user has no keycloakId")
        void shouldReturn400WhenUserHasNoKeycloakIdForVerification() {
            doThrow(new UserBadRequestException("El usuario no tiene un keycloakId asociado"))
                    .when(userService).sendEmailVerification(anyString());

            Response response = given()
                    .param("email", testEmail)
                    .when()
                    .post(baseUri + "/send-verification");

            assertEquals(400, response.statusCode());
        }

        @Test
        @DisplayName("POST /send-verification?email=... - should return 500 when keycloak fails")
        void shouldReturn500WhenKeycloakFailsForVerification() {
            doThrow(new UserInternalServerErrorException("Keycloak error"))
                    .when(userService).sendEmailVerification(anyString());

            Response response = given()
                    .param("email", testEmail)
                    .when()
                    .post(baseUri + "/send-verification");

            assertEquals(500, response.statusCode());
        }
    }

    @Nested
    @DisplayName("Reset Password Endpoint Tests")
    class ResetPasswordEndpointTests {

        @Test
        @DisplayName("POST /reset-password?email=... - should reset password successfully")
        void shouldResetPasswordSuccessfully() {
            doNothing().when(userService).resetPasswordByEmail(testEmail);

            Response response = given()
                    .param("email", testEmail)
                    .when()
                    .post(baseUri + "/reset-password");

            assertEquals(200, response.statusCode());
            verify(userService).resetPasswordByEmail(testEmail);
        }

        @Test
        @DisplayName("POST /reset-password?email=... - should return 404 when user not found")
        void shouldReturn404WhenUserNotFoundForReset() {
            doThrow(new UserNotFoundException("Usuario no encontrado con email: " + testEmail))
                    .when(userService).resetPasswordByEmail(anyString());

            Response response = given()
                    .param("email", "nonexistent@example.com")
                    .when()
                    .post(baseUri + "/reset-password");

            assertEquals(404, response.statusCode());
        }

        @Test
        @DisplayName("POST /reset-password?email=... - should return 400 when user has no keycloakId")
        void shouldReturn400WhenUserHasNoKeycloakIdForReset() {
            doThrow(new UserBadRequestException("El usuario no tiene un keycloakId asociado"))
                    .when(userService).resetPasswordByEmail(anyString());

            Response response = given()
                    .param("email", testEmail)
                    .when()
                    .post(baseUri + "/reset-password");

            assertEquals(400, response.statusCode());
        }

        @Test
        @DisplayName("POST /reset-password?email=... - should return 500 when keycloak fails")
        void shouldReturn500WhenKeycloakFailsForReset() {
            doThrow(new UserInternalServerErrorException("Keycloak error"))
                    .when(userService).resetPasswordByEmail(anyString());

            Response response = given()
                    .param("email", testEmail)
                    .when()
                    .post(baseUri + "/reset-password");

            assertEquals(500, response.statusCode());
        }
    }

    @Nested
    @DisplayName("Get All Users Endpoint Tests")
    class GetAllUsersEndpointTests {

        @Test
        @DisplayName("GET / - should return all users")
        void shouldReturnAllUsers() {
            UserDTO userDTO = new UserDTO();
            userDTO.setEmail(testEmail);
            userDTO.setNombre("John");
            userDTO.setApellido("Doe");

            when(userService.getUsers()).thenReturn(List.of(userDTO));

            Response response = given()
                    .when()
                    .get(baseUri);

            assertEquals(200, response.statusCode());
        }
    }

    @Nested
    @DisplayName("Get Email Endpoint Tests")
    class GetEmailEndpointTests {

        @Test
        @DisplayName("GET /getEmail - should return email when user exists")
        void shouldReturnEmailWhenUserExists() {
            when(userService.getEmail(testEmail)).thenReturn(testEmail);

            Response response = given()
                    .param("email", testEmail)
                    .when()
                    .get(baseUri + "/getEmail");

            assertEquals(200, response.statusCode());
            assertEquals(testEmail, response.getBody().asString());
        }

        @Test
        @DisplayName("GET /getEmail - should return 404 when user does not exist")
        void shouldReturn404WhenUserDoesNotExist() {
            doThrow(new UserNotFoundException("Usuario no encontrado"))
                    .when(userService).getEmail(anyString());

            Response response = given()
                    .param("email", "nonexistent@example.com")
                    .when()
                    .get(baseUri + "/getEmail");

            assertEquals(404, response.statusCode());
        }
    }

    @Nested
    @DisplayName("Delete User Endpoint Tests")
    class DeleteUserEndpointTests {

        @Test
        @DisplayName("DELETE /{id} - should delete user successfully")
        void shouldDeleteUserSuccessfully() {
            UUID userId = user.getId();
            doNothing().when(userService).deleteUser(userId);

            Response response = given()
                    .when()
                    .delete(baseUri + "/" + userId);

            assertEquals(204, response.statusCode());
        }
    }
}
