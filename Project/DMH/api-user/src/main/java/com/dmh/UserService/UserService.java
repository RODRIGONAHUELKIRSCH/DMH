package com.dmh.UserService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.dmh.Entity.User;
import com.dmh.UserDTO.UserDTO;
import com.dmh.UserMapper.UserMapper;
import com.dmh.UserRepository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       UserMapper userMapper,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserDTO saveUser(UserDTO dtoUser) {
        User user = userMapper.DTOtoUser(dtoUser);

        String encodedPassword = passwordEncoder.encode(user.getPwd());
        user.setPwd(encodedPassword);

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

    @Transactional
    public UUID validatePassword(String email, String password) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado en la base de datos"));

        if (!passwordEncoder.matches(password, user.getPwd())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        return user.getId();
    }
}