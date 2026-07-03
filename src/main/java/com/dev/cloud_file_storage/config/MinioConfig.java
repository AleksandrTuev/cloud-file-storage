package com.dev.cloud_file_storage.config;

import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Configuration
public class MinioConfig {

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint("http://localhost:9000")
                .credentials("minioadmin", "minioadmin")
                .build();
    }

    @Bean
    public void mainBucket() throws ServerException, InsufficientDataException,
            ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException,
            InvalidResponseException, XmlParserException, InternalException {
        minioClient().makeBucket(MakeBucketArgs.builder().bucket("user-files").build());
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
