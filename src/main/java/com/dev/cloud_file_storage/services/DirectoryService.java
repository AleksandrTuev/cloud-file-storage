package com.dev.cloud_file_storage.services;

import com.dev.cloud_file_storage.dto.ResourceDto;
import com.dev.cloud_file_storage.utils.ResourceUtils;
import io.minio.Result;
import io.minio.errors.*;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DirectoryService {
    private final MinioService minioService;

    public List<ResourceDto> getInfo(String path) throws ServerException, InsufficientDataException,
            ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException,
            InvalidResponseException, XmlParserException, InternalException {

        path = ResourceUtils.getPathToFolderUser(path);
        String parentPath = path;
        List<ResourceDto> list = new ArrayList<>();
        //todo validate когда в адресной строке вводится несуществующая папка
        for (Result<Item> result : minioService.getList(path)) {
            Item item = result.get();
            if (parentPath.equals(item.objectName())) {
                continue;
            }

            if (item.isDir()) {
                list.add(ResourceUtils.getDirectoryDto(ResourceUtils.getParentPath(item.objectName()),
                        ResourceUtils.getResourceName(item.objectName() + "/")));
            } else {
                list.add(ResourceUtils.getFileDto(ResourceUtils.getParentPath(item.objectName()),
                        ResourceUtils.getResourceName(item.objectName()), item.size()));
            }
        }
        return list;
    }

    public ResourceDto createFolder(String path) throws ServerException, InsufficientDataException,
            ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException,
            InvalidResponseException, XmlParserException, InternalException {

        path = ResourceUtils.getPathToFolderUser(path);
        minioService.putEmptyFolder(path);
        //todo validate когда добавляются папки с одним именем
        //todo validate папка и файл с одним именем могут находится
        //todo stream нужно закрывать для освобождения ресурсов
        return ResourceUtils.getDirectoryDto(ResourceUtils.getParentPath(path), ResourceUtils.getResourceName(path));
    }
}
