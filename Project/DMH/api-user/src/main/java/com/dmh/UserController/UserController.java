package com.dmh.UserController;

import com.dmh.Entity.User;
import com.dmh.UserDTO.UserDTO;
import com.dmh.UserService.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody UserDTO userDTO) {

        User registeredUser = userService.register(userDTO);

        userDTO.setKeycloakId(registeredUser.getKeycloackId());
        return ResponseEntity.ok(userDTO);
    }

    @PostMapping("/login")
    public AccessTokenResponse login(@RequestBody UserDTO userDTO) {

        return userService.login(
                userDTO.getEmail(),
                userDTO.getPwd()
        );
    }

//    @PostMapping("/logout")
//    public ResponseEntity<String> logout(@RequestBody Map<String, String> body) {
//        userService.logout(body.get("refresh_token"));
//        return  ResponseEntity.ok("User Logout Successfully");
//    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(
             HttpServletRequest refreshToken) {
        String token = refreshToken.getHeader("X-Refresh-Token");
        userService.logout(token);
        return ResponseEntity.ok("User Logout Successfully");
    }

    @PostMapping("/{keycloakId}/send-verification")
    public ResponseEntity<String> sendEmailVerification(@PathVariable String keycloakId) {
        userService.sendEmailVerification(keycloakId);
        return ResponseEntity.ok("Email de verificación enviado correctamente");
    }

    @PostMapping("/{keycloakId}/reset-password")
    public ResponseEntity<Void> resetPassword(@PathVariable String keycloakId) {
        userService.resetUserPassword(keycloakId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/getEmail")
    public ResponseEntity<String> validateEmail(@RequestParam String email) {
        return ResponseEntity.ok(userService.getEmail(email));
    }

}