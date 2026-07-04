package com.dev.cloud_file_storage.utils;

import com.dev.cloud_file_storage.dto.resource_dto.DirectoryDto;
import com.dev.cloud_file_storage.dto.resource_dto.FileDto;
import com.dev.cloud_file_storage.enums.ResourceType;
import com.dev.cloud_file_storage.security.PersonDetails;
import io.minio.messages.Item;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.context.SecurityContextHolder;

@UtilityClass
public class ResourceUtils {

    public static String getPathToResource(String path) {
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
            return path.lastIndexOf("/") == -1 ? "/" : path.substring(0, path.lastIndexOf("/"));
        } else {
            return path.lastIndexOf("/") == -1 ? "/" : path.substring(0, path.lastIndexOf("/"));
        }
    }

    public static String getResourceName(String path) {
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
            return path.lastIndexOf("/") == -1 ? path : path.substring(path.lastIndexOf("/") + 1);
        } else {
            return path.lastIndexOf("/") == -1 ? path : path.substring(path.lastIndexOf("/") + 1);
        }
    }

    public static DirectoryDto getResourceFromItem(Item item) {
        if (item.isDir()) {
            return new DirectoryDto(
                    //todo захардкожено, в имени ставится '/' тк фронт режет последний символ
                    getPathToResource(item.objectName()),
                    getResourceName(item.objectName()) + "/",
                    ResourceType.DIRECTORY
            );
        } else {
            return new FileDto(
                    getPathToResource(item.objectName()),
                    getResourceName(item.objectName()),
                    item.size(),
                    ResourceType.FILE
            );
        }
    }

    public static DirectoryDto getResource(String path) {
        if (path.endsWith("/")) {
            return new DirectoryDto(
                    getPathToResource(path),
                    getResourceName(path),
                    ResourceType.DIRECTORY
            );
        } else {
            return new FileDto(
                    getPathToResource(path),
                    getResourceName(path),
                    10, //todo захардкожено
                    ResourceType.FILE
            );
        }
    }

    public static String getNameUserFolder(String path) {
        PersonDetails personDetails = (PersonDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        long userId = personDetails.getPerson().getId();

        return String.format("user-%d-files/", userId);
    }
}
