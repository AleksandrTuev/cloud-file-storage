package com.dev.cloud_file_storage.config;

import com.dev.cloud_file_storage.exception.InitBucketException;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.*;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Configuration
public class MinioConfig {

    @Bean
    public MinioClient minioClient() {
        MinioClient minioClient = MinioClient.builder()
                .endpoint("http://localhost:9000")
                .credentials("minioadmin", "minioadmin")
                .build();

        String bucketName = "user-files";

        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());

            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            throw new InitBucketException("failed to initialize main bucket", e);
        }

        return minioClient;
    }

//    @Bean
//    public MinioClient minioClient() throws MinioException {
//        return MinioClient.builder()
//                .endpoint("https://localhost:9000")
////                .endpoint("http://127.0.0.1:9000")
//                .credentials("minioadmin", "minioadmin")
//                .build()
//                .makeBucket(MakeBucketArgs.builder().bucket("my-bucket").build());
//    }
}
