package com.dev.cloud_file_storage.services;

import com.dev.cloud_file_storage.dto.resource_dto.DirectoryDto;
import com.dev.cloud_file_storage.enums.ResourceType;
import com.dev.cloud_file_storage.utils.ResourceUtils;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.Result;
import io.minio.errors.*;
import io.minio.messages.Item;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Service
public class DirectoryService {
    private final MinioClient minioClient;

    public DirectoryService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public List<DirectoryDto> getFolderInfo(String path) throws ServerException, InsufficientDataException,
            ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException,
            InvalidResponseException, XmlParserException, InternalException {

        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket("user-files")
                        .prefix(path)
                        .delimiter("/")
                        .build());
        List<DirectoryDto> list = new ArrayList<>();
        //todo validate когда в адресной строке вводится несуществующая папка
        for (Result<Item> result : results) {
            Item item = result.get();
            if ((!item.isDir()) && (item.objectName().endsWith("/"))) {
                return List.of();
            }
            list.add(ResourceUtils.getResourceFromItem(item));

        }
        return list;
    }

    public DirectoryDto createFolder(String path) throws ServerException, InsufficientDataException,
            ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException,
            InvalidResponseException, XmlParserException, InternalException {


        path = getPathToFolderUser(path);
        minioClient.putObject(PutObjectArgs
                .builder()
                .bucket("user-files")
                .object(path)
                .stream(new ByteArrayInputStream(new byte[0]), 0, -1)
                .build());
        //todo validate когда добавляются папки с одним именем
        //todo validate папка и файл с одним именем могут находится
        //todo stream нужно закрывать для освобождения ресурсов
        return new DirectoryDto(
                ResourceUtils.getPathToResource(path),
                ResourceUtils.getResourceName(path),
                ResourceType.DIRECTORY
        );
    }

    public String getPathToFolderUser(String path) {
        return ResourceUtils.getNameUserFolder(path) + path;
    }
}
