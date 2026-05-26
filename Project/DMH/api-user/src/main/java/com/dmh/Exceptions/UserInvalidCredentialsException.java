package com.dmh.Exceptions;

public class UserInvalidCredentialsException extends RuntimeException {
    public UserInvalidCredentialsException(String message) {
       super(message);
    }
    public UserInvalidCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }
}
