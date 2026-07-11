package com.dev.cloud_file_storage.services;

import com.dev.cloud_file_storage.dto.ResourceDto;
import com.dev.cloud_file_storage.exception.ResourceAlreadyExistsException;
import com.dev.cloud_file_storage.exception.ResourceNotFoundException;
import com.dev.cloud_file_storage.utils.ResourceUtils;
import io.minio.Result;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DirectoryService {
    private final MinioService minioService;

    public List<ResourceDto> getInfo(String path) {

        path = ResourceUtils.getPathToFolderUser(path);
        String parentPath = path;
        List<ResourceDto> list = new ArrayList<>();
        boolean found = false;

        for (Result<Item> result : minioService.getList(path)) {
            Item item = minioService.getItem(result);
            if (parentPath.equals(item.objectName())) {
                found = true;
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

        if (list.isEmpty() && !found) {
            throw new ResourceNotFoundException("Resource not exists");
        }
        return list;
    }

    public ResourceDto createFolder(String path) {

        path = ResourceUtils.getPathToFolderUser(path);
        if (isExists(path)) {
            throw new ResourceAlreadyExistsException("Folder already exists");
        }

        minioService.putEmptyFolder(path);
        return ResourceUtils.getDirectoryDto(ResourceUtils.getParentPath(path), ResourceUtils.getResourceName(path));
    }

    public boolean isExists(String path) {

        for (Result<Item> result : minioService.getList(ResourceUtils.getParentPath(path))) {
            Item item = minioService.getItem(result);
            String oldPath = item.objectName().toLowerCase();
            String newPath = path.toLowerCase();

            if (oldPath.equals(newPath)) {
                return true;
            }
        }
        return false;
    }
}
