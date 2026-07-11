package com.dev.cloud_file_storage.exception;

public class MinioException extends RuntimeException {
    public MinioException(String message, Throwable cause) {
        super(message);
    }
}
