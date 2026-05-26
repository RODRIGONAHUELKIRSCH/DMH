package com.dmh.Exceptions;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.InternalServerErrorException;
import org.apache.http.auth.InvalidCredentialsException;
import org.springframework.boot.health.actuate.endpoint.HttpCodeStatusMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler  extends RuntimeException{

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleUserNotFoundException(
            UserNotFoundException ex
    ) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        "error", "Usuario no encontrado",
                        "message", ex.getMessage(),
                        "status", 404,
                        "timestamp", LocalDateTime.now()
                ));
    }

    @ExceptionHandler(UserInvalidCredentialsException.class)
    public ResponseEntity<?> handleInvalidPasswordException(
            UserInvalidCredentialsException ex
    ) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "error", "Contraseña incorrecta",
                        "message", ex.getMessage(),
                        "status", 400,
                        "timestamp", LocalDateTime.now()
                ));
    }

    @ExceptionHandler(UserInternalServerErrorException.class)
    public ResponseEntity<?> handleInternalServerErrorException(
            UserInternalServerErrorException ex
    ) {

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "error", "Internal Server Error",
                        "message", ex.getMessage(),
                        "status", 500,
                        "timestamp", LocalDateTime.now()
                ));
    }
    @ExceptionHandler(UserBadRequestException.class)
    public ResponseEntity<?> handleUserBadRequestException(
            UserBadRequestException ex
    ){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "error","Bad Request",
                        "message",ex.getMessage(),
                        "status",400,
                        "timestamp", LocalDateTime.now()
                ));
    }

    }
