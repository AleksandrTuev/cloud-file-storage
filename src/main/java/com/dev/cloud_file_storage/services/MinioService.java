package com.dev.cloud_file_storage.services;

import com.dev.cloud_file_storage.exception.MinioException;
import com.dev.cloud_file_storage.utils.ProjectConstants;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
@RequiredArgsConstructor
public class MinioService {
    private final MinioClient minioClient;
    private final String message = "Minio exception";

    public Iterable<Result<Item>> getList(String path) {
        return minioClient.listObjects(ListObjectsArgs.builder()
                .bucket(ProjectConstants.NAME_MAIN_BUCKET)
                .prefix(path)
                .delimiter("/")
                .build());
    }

    public void upload(String path, String absolutePath, String contentType) {
        try {
            minioClient.uploadObject(
                    UploadObjectArgs.builder()
                            .bucket(ProjectConstants.NAME_MAIN_BUCKET)
                            .object(path)
                            .filename(absolutePath)
                            .contentType(contentType)
                            .build());
        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException
                 | NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException
                 | InternalException e) {
            throw new MinioException(message, e);
        }
    }

    public void copy(String from, String to) {
        try {
            minioClient.copyObject(CopyObjectArgs.builder()
                    .bucket(ProjectConstants.NAME_MAIN_BUCKET)
                    .object(to)
                    .source(
                            CopySource.builder()
                                    .bucket(ProjectConstants.NAME_MAIN_BUCKET)
                                    .object(from)
                                    .build()
                    ).build());
        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException
                 | NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException
                 | InternalException e) {
            throw new MinioException(message, e);
        }
    }

    public StatObjectResponse getStat(String path) {
        try {
            return minioClient.statObject(StatObjectArgs.builder()
                    .bucket(ProjectConstants.NAME_MAIN_BUCKET)
                    .object(path)
                    .build());
        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException
                 | NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException
                 | InternalException e) {
            throw new MinioException(message, e);
        }
    }

    public void download(String path, String absolutPath) {
        try {
            minioClient.downloadObject(
                    DownloadObjectArgs.builder()
                            .bucket(ProjectConstants.NAME_MAIN_BUCKET)
                            .object(path)
                            .filename(absolutPath)
                            .build());
        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException
                 | NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException
                 | InternalException e) {
            throw new MinioException(message, e);
        }
    }

    public void remove(String path) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(ProjectConstants.NAME_MAIN_BUCKET)
                    .object(path)
                    .build());
        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException
                 | NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException
                 | InternalException e) {
            throw new MinioException(message, e);
        }
    }

    public void putEmptyFolder(String path) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(new byte[0])) {
            minioClient.putObject(PutObjectArgs
                    .builder()
                    .bucket(ProjectConstants.NAME_MAIN_BUCKET)
                    .object(path)
                    .stream(bais, 0, -1)
                    .build());
        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException
                 | NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException
                 | InternalException e) {
            throw new MinioException(message, e);
        }
    }

    public Item getItem(Result<Item> result) {
        try {
            return result.get();
        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException
                 | NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException
                 | InternalException e) {
            throw new MinioException(message, e);
        }
    }
}
