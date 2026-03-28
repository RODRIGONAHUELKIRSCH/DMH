package com.dmh.UserService;

import com.dmh.Entity.User;
import com.dmh.Keycloak.KeycloakAuth;
import com.dmh.Keycloak.KeycloakClient;
import com.dmh.UserDTO.UserDTO;
import com.dmh.UserMapper.UserMapper;
import com.dmh.UserRepository.UserRepository;
import jakarta.transaction.Transactional;
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

            keycloakId = keycloakClient.createUser(userDTO.getNombre(),userDTO.getApellido() ,userDTO.getEmail(), userDTO.getPwd());
            userDTO.setKeycloakId(keycloakId);

            User user = userMapper.DTOtoUser(userDTO);
            user.setKeycloackId(userDTO.getKeycloakId());

            return userRepository.save(user);

        } catch (Exception e) {
            // Rollback en Keycloak si falla DB
            if (keycloakId != null) {
                keycloakClient.deleteUser(keycloakId);
            }
            throw new RuntimeException("No se pudo registrar el usuario: " + e.getMessage(), e);
        }
    }

    @Transactional
    public AccessTokenResponse login(String email, String password) {
        return keycloakAuth.login(email, password);
    }

    @Transactional
    public void logout(String refreshToken) {
        keycloakAuth.logout(refreshToken);
    }

    @Transactional
    public void sendEmailVerification(String keycloakId) {

        keycloakClient.sendEmailVerification(keycloakId);
    }

    @Transactional
    public void resetUserPassword(String keycloakId) {
        keycloakClient.resetPassword(keycloakId);
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
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado en la base de datos"));

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