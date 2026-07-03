package com.dev.cloud_file_storage.exception;

public class InitBucketException extends RuntimeException {
    public InitBucketException(String message) {
        super(message);
    }

    public InitBucketException(String message, Throwable cause) {}
}
