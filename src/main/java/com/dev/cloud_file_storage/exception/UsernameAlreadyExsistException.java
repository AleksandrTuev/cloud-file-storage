package com.dev.cloud_file_storage.exception;

public class UsernameAlreadyExsistException extends RuntimeException {
    public UsernameAlreadyExsistException(String message) {
        super(message);
    }
}
