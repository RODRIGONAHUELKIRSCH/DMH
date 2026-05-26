package com.dmh.Exceptions;

public class UserInternalServerErrorException extends RuntimeException {
    public UserInternalServerErrorException(String message) {
        super(message);
    }
    public UserInternalServerErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}
