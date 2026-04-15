package com.dmh.Exceptions;

import org.apache.http.auth.InvalidCredentialsException;
import org.springframework.boot.health.actuate.endpoint.HttpCodeStatusMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler  extends RuntimeException{

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<?> handleNotFound(InvalidCredentialsException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "error","Contraseña incorrecta",
                "message", ex.getMessage()
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleNotFound(Exception ex){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error","Internal Server Error",
                "message", ex.getMessage()
        ));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<?> handleNotFound(UsernameNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "error","Contraseña incorrecta",
                "message", ex.getMessage()
        ));
    }


}
