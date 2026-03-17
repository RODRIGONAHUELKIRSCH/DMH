package com.dmh.UserController;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.dmh.UserDTO.UserDTO;
import com.dmh.UserService.UserService;
import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.saveUser(userDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/validatePassword")
    public ResponseEntity<UUID> validatePassword(
            @RequestParam String email,
            @RequestParam String pwd) {

        return ResponseEntity.ok(userService.validatePassword(email, pwd));
    }

    @GetMapping("/getEmail")
    public ResponseEntity<String> validateEmail(@RequestParam String email) {
        return ResponseEntity.ok(userService.getEmail(email));
    }
}