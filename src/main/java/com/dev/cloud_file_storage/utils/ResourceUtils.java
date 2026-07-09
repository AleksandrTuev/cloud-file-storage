package com.dev.cloud_file_storage.utils;

import com.dev.cloud_file_storage.dto.ResourceDto;
import com.dev.cloud_file_storage.enums.ResourceType;
import com.dev.cloud_file_storage.security.PersonDetails;
import io.minio.Result;
import io.minio.errors.*;
import io.minio.messages.Item;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@UtilityClass
public class ResourceUtils {

    public static String getParentPath(String path) {
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path.lastIndexOf("/") == -1 ? "/" : path.substring(0, path.lastIndexOf("/") + 1);
    }

    public static String getResourceName(String path) {
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path.lastIndexOf("/") == -1 ? path : path.substring(path.lastIndexOf("/") + 1);
    }

    public static ResourceDto getDirectoryDto(String path, String name) {
        return ResourceDto.builder()
                .path(path)
                .name(name)
                .type(ResourceType.DIRECTORY)
                .build();
    }

    public static ResourceDto getFileDto(String path, String name, long size) {
        return ResourceDto.builder()
                .path(path)
                .name(name)
                .size(size)
                .type(ResourceType.FILE)
                .build();
    }

    public static String getNameUserFolder() {
        PersonDetails personDetails = (PersonDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        long userId = personDetails.getPerson().getId();

        return String.format("user-%d-files/", userId);
    }

    public static String deleteNameUserFolder(String path) {
        int index = 0;
        for (int i = 0; i < path.length(); i++) {
            if (path.charAt(i) == '/') {
                index = i + 1;
                break;
            }
        }
        return path.substring(index);
    }

    public String getPathToFolderUser(String path) {
        return getNameUserFolder() + path;
    }
}
