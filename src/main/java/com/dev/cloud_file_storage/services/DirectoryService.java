package com.dev.cloud_file_storage.services;

import com.dev.cloud_file_storage.dto.ResourceDto;
import com.dev.cloud_file_storage.enums.ResourceType;
import com.dev.cloud_file_storage.utils.ProjectConstants;
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

    public List<ResourceDto> getInfo(String path) throws ServerException, InsufficientDataException,
            ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException,
            InvalidResponseException, XmlParserException, InternalException {

        path = ResourceUtils.getPathToFolderUser(path);
        String parentPath = path;

        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(ProjectConstants.NAME_MAIN_BUCKET)
                        .prefix(path)
                        .delimiter("/")
                        .build());
        List<ResourceDto> list = new ArrayList<>();
        //todo validate когда в адресной строке вводится несуществующая папка
        for (Result<Item> result : results) {
            Item item = result.get();
            if (parentPath.equals(item.objectName())) {
                continue;
            }
            list.add(ResourceUtils.getResourceFromItem(item));
        }
        return list;
    }

    public ResourceDto createFolder(String path) throws ServerException, InsufficientDataException,
            ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException,
            InvalidResponseException, XmlParserException, InternalException {

        path = ResourceUtils.getPathToFolderUser(path);
        minioClient.putObject(PutObjectArgs
                .builder()
                .bucket(ProjectConstants.NAME_MAIN_BUCKET)
                .object(path)
                .stream(new ByteArrayInputStream(new byte[0]), 0, -1)
                .build());
        //todo validate когда добавляются папки с одним именем
        //todo validate папка и файл с одним именем могут находится
        //todo stream нужно закрывать для освобождения ресурсов
        return ResourceDto.builder()
                .path(ResourceUtils.getParentPath(path))
                .name(ResourceUtils.getResourceName(path))
                .type(ResourceType.DIRECTORY)
                .build();
    }
}
