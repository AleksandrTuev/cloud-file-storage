package com.dev.cloud_file_storage.services;

import com.dev.cloud_file_storage.dto.resource_dto.DirectoryDto;
import com.dev.cloud_file_storage.dto.resource_dto.FileDto;
import com.dev.cloud_file_storage.enums.ResourceType;
import org.springframework.stereotype.Service;

@Service
public class ResourceService {
    public DirectoryDto getResourceInfo(String path) {
        //folder1/folder2/folder3/
        //folder1/folder2/file.txt
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
            return new DirectoryDto(
                    path.substring(0, path.lastIndexOf('/') + 1),
                    path.substring(path.lastIndexOf('/') + 1),
                    ResourceType.DIRECTORY
            );

        } else {
//            int size =
            return new FileDto(
                    path.substring(0, path.lastIndexOf('/') + 1),
                    path.substring(path.lastIndexOf('/') + 1),
                    10, //todo тестовое значение / реализовать его получение
                    ResourceType.FILE
            );
        }
//        path.substring(path.lastIndexOf('/') + 1);
    }
}
