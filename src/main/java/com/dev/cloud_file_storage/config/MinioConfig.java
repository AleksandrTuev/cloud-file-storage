package com.dev.cloud_file_storage.config;

import io.minio.MinioClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint("http://localhost:9000")
                .credentials("minioadmin", "minioadmin")
                .build();
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
