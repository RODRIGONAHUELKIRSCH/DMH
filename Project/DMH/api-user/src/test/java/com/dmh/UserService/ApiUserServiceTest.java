package com.dmh.UserService;

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
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.WebApplicationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.representations.AccessTokenResponse;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Unit Tests")
public class ApiUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private KeycloakClient keycloakClient;

    @Mock
    private KeycloakAuth keycloakAuth;

    @InjectMocks
    private UserService userService;

    private UserDTO userDTO;
    private User user;
    private String testKeycloakId;
    private String testEmail;
    private String testPassword;

    @BeforeEach
    void setUp() {
        testKeycloakId = UUID.randomUUID().toString();
        testEmail = "test@example.com";
        testPassword = "password123";

        userDTO = new UserDTO();
        userDTO.setNombre("John");
        userDTO.setApellido("Doe");
        userDTO.setTelefono("1234567890");
        userDTO.setDni("12345678");
        userDTO.setEmail(testEmail);
        userDTO.setPwd(testPassword);
        userDTO.setKeycloakId(testKeycloakId);
        userDTO.setCvu("1234567890123456789012");
        userDTO.setAlias("john.doe.test");

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
    @DisplayName("Register Tests")
    class RegisterTests {

        @Test
        @DisplayName("Should register user successfully")
        void ShouldRegisterUserSuccessfully() {
            // given
            when(keycloakClient.createUser(anyString(), anyString(), anyString(), anyString()))
                    .thenReturn(testKeycloakId);
            when(userMapper.DTOtoUser(any(UserDTO.class))).thenReturn(user);
            when(userRepository.save(any(User.class))).thenReturn(user);

            // when
            User result = userService.register(userDTO);

            // then
            assertNotNull(result);
            assertEquals(testKeycloakId, result.getKeycloackId());
            verify(keycloakClient).createUser(
                    userDTO.getNombre(), 
                    userDTO.getApellido(), 
                    userDTO.getEmail(), 
                    userDTO.getPwd()
            );
            verify(userMapper).DTOtoUser(userDTO);
            verify(userRepository).save(user);
        }

        @Test
        @DisplayName("Should throw UserBadRequestException when Keycloak returns BadRequestException")
        void ShouldThrowUserBadRequestExceptionWhenKeycloakReturnsBadRequest() {
            // given
            when(keycloakClient.createUser(anyString(), anyString(), anyString(), anyString()))
                    .thenThrow(new BadRequestException("Email already exists"));

            // when & then
            assertThrows(UserBadRequestException.class, () -> userService.register(userDTO));
        }

        @Test
        @DisplayName("Should throw UserInternalServerErrorException when Keycloak returns WebApplicationException")
        void ShouldThrowUserInternalServerErrorExceptionWhenKeycloakReturnsWebApplicationException() {
            // given
            when(keycloakClient.createUser(anyString(), anyString(), anyString(), anyString()))
                    .thenThrow(new WebApplicationException("Keycloak error"));

            // when & then
            assertThrows(UserInternalServerErrorException.class, () -> userService.register(userDTO));
        }

        @Test
        @DisplayName("Should throw UserInternalServerErrorException on generic exception")
        void ShouldThrowUserInternalServerErrorExceptionOnGenericException() {
            // given
            when(keycloakClient.createUser(anyString(), anyString(), anyString(), anyString()))
                    .thenThrow(new RuntimeException("Unexpected error"));

            // when & then
            assertThrows(UserInternalServerErrorException.class, () -> userService.register(userDTO));
        }
    }

    @Nested
    @DisplayName("Login Tests")
    class LoginTests {

        @Test
        @DisplayName("Should login successfully")
        void ShouldLoginSuccessfully() {
            // given
            AccessTokenResponse tokenResponse = new AccessTokenResponse();
            tokenResponse.setToken("access-token");
            tokenResponse.setRefreshToken("refresh-token");

            when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(user));
            when(keycloakAuth.login(testEmail, testPassword)).thenReturn(tokenResponse);

            // when
            AccessTokenResponse result = userService.login(testEmail, testPassword);

            // then
            assertNotNull(result);
            assertEquals("access-token", result.getToken());
            verify(userRepository).findByEmail(testEmail);
            verify(keycloakAuth).login(testEmail, testPassword);
        }

        @Test
        @DisplayName("Should throw UserNotFoundException when user does not exist")
        void ShouldThrowUserNotFoundExceptionWhenUserDoesNotExist() {
            // given
            when(userRepository.findByEmail(testEmail)).thenReturn(Optional.empty());

            // when & then
            assertThrows(UserNotFoundException.class, () -> userService.login(testEmail, testPassword));
        }

        @Test
        @DisplayName("Should throw UserInvalidCredentialsException on 401 error")
        void ShouldThrowUserInvalidCredentialsExceptionOn401Error() {
            // given
            when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(user));
            when(keycloakAuth.login(testEmail, testPassword))
                    .thenThrow(new RuntimeException("401 Unauthorized"));

            // when & then
            assertThrows(UserInvalidCredentialsException.class, () -> userService.login(testEmail, testPassword));
        }

        @Test
        @DisplayName("Should throw UserInternalServerErrorException on other errors")
        void ShouldThrowUserInternalServerErrorExceptionOnOtherErrors() {
            // given
            when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(user));
            when(keycloakAuth.login(testEmail, testPassword))
                    .thenThrow(new RuntimeException("Connection timeout"));

            // when & then
            assertThrows(UserInternalServerErrorException.class, () -> userService.login(testEmail, testPassword));
        }
    }

    @Nested
    @DisplayName("Logout Tests")
    class LogoutTests {

        @Test
        @DisplayName("Should logout successfully")
        void ShouldLogoutSuccessfully() {
            // given
            String refreshToken = "refresh-token";
            doNothing().when(keycloakAuth).logout(refreshToken);

            // when
            userService.logout(refreshToken);

            // then
            verify(keycloakAuth).logout(refreshToken);
        }

        @Test
        @DisplayName("Should throw UserInternalServerErrorException when logout fails")
        void ShouldThrowUserInternalServerErrorExceptionWhenLogoutFails() {
            // given
            String refreshToken = "invalid-token";
            doThrow(new RuntimeException("Logout failed")).when(keycloakAuth).logout(refreshToken);

            // when & then
            assertThrows(UserInternalServerErrorException.class, () -> userService.logout(refreshToken));
        }
    }

    @Nested
    @DisplayName("Email Verification Tests")
    class EmailVerificationTests {

        @Test
        @DisplayName("Should send email verification successfully")
        void ShouldSendEmailVerificationSuccessfully() {
            // given
            doNothing().when(keycloakClient).sendEmailVerification(testKeycloakId);

            // when
            userService.sendEmailVerification(testKeycloakId);

            // then
            verify(keycloakClient).sendEmailVerification(testKeycloakId);
        }

        @Test
        @DisplayName("Should throw exception when sending email verification fails")
        void ShouldThrowExceptionWhenSendingEmailVerificationFails() {
            // given
            doThrow(new RuntimeException("Email service unavailable"))
                    .when(keycloakClient).sendEmailVerification(testKeycloakId);

            // when & then
            assertThrows(RuntimeException.class, () -> userService.sendEmailVerification(testKeycloakId));
        }
    }

    @Nested
    @DisplayName("Password Reset Tests")
    class PasswordResetTests {

        @Test
        @DisplayName("Should reset password successfully")
        void ShouldResetPasswordSuccessfully() {
            // given
            doNothing().when(keycloakClient).resetPassword(testKeycloakId);

            // when
            userService.resetUserPassword(testKeycloakId);

            // then
            verify(keycloakClient).resetPassword(testKeycloakId);
        }

        @Test
        @DisplayName("Should throw exception when password reset fails")
        void ShouldThrowExceptionWhenPasswordResetFails() {
            // given
            doThrow(new RuntimeException("Password reset failed"))
                    .when(keycloakClient).resetPassword(testKeycloakId);

            // when & then
            assertThrows(RuntimeException.class, () -> userService.resetUserPassword(testKeycloakId));
        }
    }

    @Nested
    @DisplayName("Save User Tests")
    class SaveUserTests {

        @Test
        @DisplayName("Should save user successfully")
        void ShouldSaveUserSuccessfully() {
            // given
            when(userMapper.DTOtoUser(any(UserDTO.class))).thenReturn(user);
            when(userRepository.save(any(User.class))).thenReturn(user);
            when(userMapper.UsertoDTO(any(User.class))).thenReturn(userDTO);

            // when
            UserDTO result = userService.saveUser(userDTO);

            // then
            assertNotNull(result);
            assertEquals(userDTO.getEmail(), result.getEmail());
            verify(userMapper).DTOtoUser(userDTO);
            verify(userRepository).save(user);
            verify(userMapper).UsertoDTO(user);
        }
    }

    @Nested
    @DisplayName("Get Users Tests")
    class GetUsersTests {

        @Test
        @DisplayName("Should get all users successfully")
        void ShouldGetAllUsersSuccessfully() {
            // given
            List<User> users = List.of(user);
            when(userRepository.findAll()).thenReturn(users);
            when(userMapper.UsertoDTO(any(User.class))).thenReturn(userDTO);

            // when
            List<UserDTO> result = userService.getUsers();

            // then
            assertNotNull(result);
            assertEquals(1, result.size());
            verify(userRepository).findAll();
        }

        @Test
        @DisplayName("Should return empty list when no users exist")
        void ShouldReturnEmptyListWhenNoUsersExist() {
            // given
            when(userRepository.findAll()).thenReturn(List.of());

            // when
            List<UserDTO> result = userService.getUsers();

            // then
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Get Email Tests")
    class GetEmailTests {

        @Test
        @DisplayName("Should get email successfully")
        void ShouldGetEmailSuccessfully() {
            // given
            when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(user));

            // when
            String result = userService.getEmail(testEmail);

            // then
            assertEquals(testEmail, result);
            verify(userRepository).findByEmail(testEmail);
        }

        @Test
        @DisplayName("Should throw UserNotFoundException when user does not exist")
        void ShouldThrowUserNotFoundExceptionWhenUserDoesNotExist() {
            // given
            when(userRepository.findByEmail(testEmail)).thenReturn(Optional.empty());

            // when & then
            assertThrows(UserNotFoundException.class, () -> userService.getEmail(testEmail));
        }
    }

    @Nested
    @DisplayName("Get User Name Email Tests")
    class GetUserNameEmailTests {

        @Test
        @DisplayName("Should get user name and email successfully")
        void ShouldGetUserNameEmailSuccessfully() {
            // given
            List<User> users = List.of(user);
            when(userRepository.findAll()).thenReturn(users);
            when(userMapper.UsertoDTO(any(User.class))).thenReturn(userDTO);

            // when
            List<UserDTO> result = userService.getUserNameEmail();

            // then
            assertNotNull(result);
            assertEquals(1, result.size());
            verify(userRepository).findAll();
        }
    }

    @Nested
    @DisplayName("Delete User Tests")
    class DeleteUserTests {

        @Test
        @DisplayName("Should delete user successfully")
        void ShouldDeleteUserSuccessfully() {
            // given
            UUID userId = user.getId();
            doNothing().when(userRepository).deleteById(userId);

            // when
            userService.deleteUser(userId);

            // then
            verify(userRepository).deleteById(userId);
        }
    }
}
