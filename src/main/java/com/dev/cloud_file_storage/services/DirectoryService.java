package com.dev.cloud_file_storage.services;

import com.dev.cloud_file_storage.dto.ResourceDto;
import com.dev.cloud_file_storage.exception.ResourceAlreadyExistsException;
import com.dev.cloud_file_storage.exception.ResourceNotFoundException;
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
        for (Result<Item> result : minioService.getList(path)) {
            Item item = result.get();
            if (parentPath.equals(item.objectName())) {
                continue;
            }

            if (item.isDir()) {
                list.add(ResourceUtils.getDirectoryDto(ResourceUtils.getParentPath(item.objectName()),
                        ResourceUtils.getResourceName(item.objectName()) + "/"));
            } else {
                list.add(ResourceUtils.getFileDto(ResourceUtils.getParentPath(item.objectName()),
                        ResourceUtils.getResourceName(item.objectName()), item.size()));
            }
        }

        if (list.isEmpty()) {
            throw new ResourceNotFoundException("Resource not exists");
        }

        return list;
    }

    public ResourceDto createFolder(String path) throws ServerException, InsufficientDataException,
            ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException,
            InvalidResponseException, XmlParserException, InternalException {

        path = ResourceUtils.getPathToFolderUser(path);
        if (isExists(path)) {
            throw new ResourceAlreadyExistsException("Folder already exists");
        }

        minioService.putEmptyFolder(path);
        return ResourceUtils.getDirectoryDto(ResourceUtils.getParentPath(path), ResourceUtils.getResourceName(path));
    }

    public boolean isExists(String path) throws ServerException, InsufficientDataException, ErrorResponseException,
            IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException,
            InternalException {

        for (Result<Item> result : minioService.getList(path)) {
            if (result.get().objectName().equals(path)) {
                return true;
            }
        }
        return false;
    }
}
