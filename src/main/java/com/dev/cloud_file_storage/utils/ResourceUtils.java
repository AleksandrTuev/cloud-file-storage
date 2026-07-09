package com.dev.cloud_file_storage.utils;

import com.dev.cloud_file_storage.dto.ResourceDto;
import com.dev.cloud_file_storage.enums.ResourceType;
import com.dev.cloud_file_storage.security.PersonDetails;
import io.minio.messages.Item;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.context.SecurityContextHolder;

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

    public static ResourceDto getResourceFromItem(Item item) {
        if (item.isDir()) {
            return ResourceDto.builder()
                    .path(getParentPath(item.objectName()))
                    .name(getResourceName(item.objectName()) + "/")
                    .type(ResourceType.DIRECTORY)
                    .build();
        } else {
            return ResourceDto.builder()
                    .path(getParentPath(item.objectName()))
                    .name(getResourceName(item.objectName()))
                    .size(item.size())
                    .type(ResourceType.FILE)
                    .build();
        }
    }

    public static String getNameUserFolder() {
        PersonDetails personDetails = (PersonDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        long userId = personDetails.getPerson().getId();

        return String.format("user-%d-files/", userId);
    }

    public static String deleteNameUserFolder(String path) {
        int index = 0;
        for (int i = 0; i < path.toCharArray().length; i++) {
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
