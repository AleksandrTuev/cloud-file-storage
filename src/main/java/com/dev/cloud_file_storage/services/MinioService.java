package com.dev.cloud_file_storage.services;

import com.dev.cloud_file_storage.utils.ProjectConstants;
import com.dev.cloud_file_storage.utils.ResourceUtils;
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

    public Iterable<Result<Item>> getList(String path) {
        return minioClient.listObjects(ListObjectsArgs.builder()
                .bucket(ProjectConstants.NAME_MAIN_BUCKET)
                .prefix(path)
                .delimiter("/")
                .build());
    }

    public void upload(String path, String absolutePath, String contentType) throws IOException,
            ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException,
            InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        minioClient.uploadObject(
                UploadObjectArgs.builder()
                        .bucket(ProjectConstants.NAME_MAIN_BUCKET)
                        .object(path)
                        .filename(absolutePath)
                        .contentType(contentType)
                        .build());
    }

    public void copy(String from, String to) throws ServerException, InsufficientDataException,
            ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException,
            InvalidResponseException, XmlParserException, InternalException {

        minioClient.copyObject(CopyObjectArgs.builder()
                .bucket(ProjectConstants.NAME_MAIN_BUCKET)
                .object(to)
                .source(
                        CopySource.builder()
                                .bucket(ProjectConstants.NAME_MAIN_BUCKET)
                                .object(from)
                                .build()
                ).build());
    }

    public StatObjectResponse getStat(String path) throws ServerException, InsufficientDataException,
            ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException,
            InvalidResponseException, XmlParserException, InternalException {

        return minioClient.statObject(StatObjectArgs.builder()
                .bucket(ProjectConstants.NAME_MAIN_BUCKET)
                .object(path)
                .build());
    }

    public void download(String path, String absolutPath) throws ServerException, InsufficientDataException,
            ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException,
            InvalidResponseException, XmlParserException, InternalException {

        minioClient.downloadObject(
                DownloadObjectArgs.builder()
                        .bucket(ProjectConstants.NAME_MAIN_BUCKET)
                        .object(path)
                        .filename(absolutPath)
                        .build());
    }

    public void remove(String path) throws ServerException, InsufficientDataException, ErrorResponseException,
            IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException,
            InternalException {

        minioClient.removeObject(RemoveObjectArgs.builder()
                .bucket(ProjectConstants.NAME_MAIN_BUCKET)
                .object(path)
                .build());
    }

    public void putEmptyFolder(String path) throws ServerException, InsufficientDataException, ErrorResponseException,
            IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException,
            InternalException {

        minioClient.putObject(PutObjectArgs
                .builder()
                .bucket(ProjectConstants.NAME_MAIN_BUCKET)
                .object(path)
                .stream(new ByteArrayInputStream(new byte[0]), 0, -1)
                .build());
    }
}
