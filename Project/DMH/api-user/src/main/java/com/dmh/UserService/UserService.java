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
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.WebApplicationException;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final KeycloakClient keycloakClient;
    private final KeycloakAuth keycloakAuth;

    public UserService(UserRepository userRepository,
                       UserMapper userMapper,
                       KeycloakClient keycloakClient,
                       KeycloakAuth keycloakAuth
                       ) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.keycloakClient = keycloakClient;
        this.keycloakAuth = keycloakAuth;
    }

   @Transactional
     public User register(UserDTO userDTO) {
        String keycloakId = null;
        try {

             keycloakId = keycloakClient.createUser(userDTO.getNombre(), userDTO.getApellido(), userDTO.getEmail(), userDTO.getPwd());
             userDTO.setKeycloakId(keycloakId);

             User user = userMapper.DTOtoUser(userDTO);
             user.setKeycloackId(userDTO.getKeycloakId());

             return userRepository.save(user);

         } catch (BadRequestException ex) {
             throw new UserBadRequestException("Error de registro: " + ex.getMessage());
         } catch (WebApplicationException ex) {
             throw new UserInternalServerErrorException("Error de Keycloak: " + ex.getMessage());
         } catch (Exception ex) {
             throw new UserInternalServerErrorException("Error al registrar usuario: " + ex.getMessage());
         }
     }

    @Transactional
    public AccessTokenResponse login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        try {
            return keycloakAuth.login(email, password);
        } catch (Exception e) {
            String msg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
            if (msg.contains("401") || msg.contains("unauthorized")) {
                throw new UserInvalidCredentialsException("Email o contraseña incorrectos");
            }
            throw new UserInternalServerErrorException("Error interno del servidor: " + e.getMessage());
        }
    }

    @Transactional
    public void logout(String refreshToken) {
        try {
            keycloakAuth.logout(refreshToken);
        } catch (Exception ex) {
            throw new UserInternalServerErrorException("Error al cerrar sesión: " + ex.getMessage());
        }
    }

    @Transactional
    public void sendEmailVerification(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("No existe un usuario con el email: " + email));

        if (user.getKeycloackId() == null || user.getKeycloackId().isBlank()) {
            throw new UserBadRequestException("El usuario no tiene un keycloakId asociado");
        }

        keycloakClient.sendEmailVerification(user.getKeycloackId());
    }

    @Transactional
    public void resetPasswordByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("No existe un usuario con el email: " + email));

        if (user.getKeycloackId() == null || user.getKeycloackId().isBlank()) {
            throw new UserBadRequestException("El usuario no tiene un keycloakId asociado");
        }

        keycloakClient.resetPassword(user.getKeycloackId());
    }

    @Transactional
    public UserDTO saveUser(UserDTO dtoUser) {
        User user = userMapper.DTOtoUser(dtoUser);


        userRepository.save(user);

        return userMapper.UsertoDTO(user);
    }

    @Transactional
    public List<UserDTO> getUsers() {
        return ((List<User>)userRepository.findAll())
                .stream()
                .map(userMapper::UsertoDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public String getEmail(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con email: " + email));

        return user.getEmail();
    }

    public List<UserDTO> getUserNameEmail() {
        return ((List<User>)userRepository.findAll())
                .stream()
                .map(userMapper::UsertoDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteUser(UUID id){
        userRepository.deleteById(id);
    }

}