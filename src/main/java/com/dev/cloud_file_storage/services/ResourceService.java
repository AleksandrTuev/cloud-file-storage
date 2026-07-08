package com.dev.cloud_file_storage.services;

import com.dev.cloud_file_storage.dto.ResourceDto;
import com.dev.cloud_file_storage.enums.ResourceType;
import com.dev.cloud_file_storage.utils.ProjectConstants;
import com.dev.cloud_file_storage.utils.ResourceUtils;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class ResourceService {
    private final MinioClient minioClient;

    public ResourceService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public ResourceDto getInfo(String path) throws ServerException, InsufficientDataException,
            ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException,
            InvalidResponseException, XmlParserException, InternalException {
        path = ResourceUtils.getPathToFolderUser(path);

        if (path.trim().endsWith("/")) {
            return getDirectoryInfo(path);
        } else {
            return getFileInfo(path);
        }
    }

    private List<String> getResources(String path) throws ServerException, InsufficientDataException,
            ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException,
            InvalidResponseException, XmlParserException, InternalException {
        List<String> resources = new ArrayList<>();
        Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder()
                .bucket(ProjectConstants.NAME_MAIN_BUCKET)
                .prefix(path)
                .delimiter("/")
                .build());
        for (Result<Item> result : results) {
            Item item = result.get();

            if (path.equals(item.objectName())) {
                continue;
            }

            resources.add(item.objectName());
        }
        return resources;
    }

    private List<String> getResourcesList(String path) throws ServerException, InsufficientDataException,
            ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException,
            InvalidResponseException, XmlParserException, InternalException {
        List<String> resources = new ArrayList<>();
        List<String> tempList = getResources(path);
        List<String> tempList2 = new ArrayList<>();

        while (true) {
            if (tempList.isEmpty()) {
                return resources;
            }

            if (!resources.containsAll(tempList)) {
                resources.addAll(tempList);
            }

            for (String s : tempList) {
                tempList2.addAll(getResources(s));
            }
            tempList.clear();
            tempList.addAll(tempList2);
            tempList2.clear();
        }
    }

    public void remove(String path) throws ServerException, InsufficientDataException, ErrorResponseException,
            IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException,
            InternalException {

        Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder()
                .bucket(ProjectConstants.NAME_MAIN_BUCKET)
                .prefix(path)
                .delimiter("/")
                .build());

        for (Result<Item> result : results) {
            Item item = result.get();

            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(ProjectConstants.NAME_MAIN_BUCKET)
                    .object(item.objectName())
                    .build());
        }

    }

    public void download(String path, HttpServletResponse response) throws ServerException, InsufficientDataException,
            ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException,
            InvalidResponseException, XmlParserException, InternalException {

        if (isDirectory(path)) {
            List<String> resources = getResourcesList(path);

            try (OutputStream outputStream = response.getOutputStream();
                 ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {

                resources.forEach(resource -> {
                    ZipEntry zipEntry = new ZipEntry(ResourceUtils.deleteNameUserFolder(resource));
                    try {
                        zipOutputStream.putNextEntry(zipEntry);
                        zipOutputStream.closeEntry();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });

                zipOutputStream.finish();
            }
        } else {
            //todo filename какое давать?
            minioClient.downloadObject(
                    DownloadObjectArgs.builder()
                            .bucket(ProjectConstants.NAME_MAIN_BUCKET)
                            .object(path)
                            .filename(ResourceUtils.getResourceName(path))
                            .build());
        }
    }

    private boolean isDirectory(String path) {
        return path.trim().endsWith("/");
    }

    private ResourceDto getDirectoryInfo(String path) {
        return ResourceDto.builder()
                .path(ResourceUtils.getParentPath(path))
                .name(ResourceUtils.getResourceName(path))
                .type(ResourceType.DIRECTORY)
                .build();
    }

    private ResourceDto getFileInfo(String path) throws ServerException, InsufficientDataException,
            ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException,
            InvalidResponseException, XmlParserException, InternalException {

        StatObjectResponse stat = minioClient.statObject(StatObjectArgs.builder()
                .bucket(ProjectConstants.NAME_MAIN_BUCKET).build());
        long resourceSize = stat.size();

        return ResourceDto.builder()
                .path(ResourceUtils.getParentPath(path))
                .name(ResourceUtils.getResourceName(path))
                .size(resourceSize)
                .type(ResourceType.FILE)
                .build();
    }
}
