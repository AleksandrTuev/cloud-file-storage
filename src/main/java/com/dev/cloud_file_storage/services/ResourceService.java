package com.dev.cloud_file_storage.services;

import com.dev.cloud_file_storage.dto.ResourceDto;
import com.dev.cloud_file_storage.enums.ResourceType;
import com.dev.cloud_file_storage.utils.ProjectConstants;
import com.dev.cloud_file_storage.utils.ResourceUtils;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.Nullable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class ResourceService {
    private final MinioClient minioClient;
    private final DirectoryService directoryService;

    public ResourceService(MinioClient minioClient, DirectoryService directoryService) {
        this.minioClient = minioClient;
        this.directoryService = directoryService;
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

    public List<String> getFullResourcesList(String path) throws ServerException, InsufficientDataException,
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
            List<String> resources = getFullResourcesList(path);

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

    public ResourceDto move(String from, String to) throws ServerException, InsufficientDataException,
            ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException,
            InvalidResponseException, XmlParserException, InternalException {

        String oldParentPath = ResourceUtils.getParentPath(from);
        String newParentPath = ResourceUtils.getParentPath(to);
        String oldNameResource = ResourceUtils.getResourceName(from);
        String newNameResource = ResourceUtils.getResourceName(to);
        ResourceDto resourceDto;

        if (!oldNameResource.equals(newNameResource)) {
            to = ResourceUtils.deleteNameUserFolder(to);
        }

        if (isDirectory(from)) {
            List<String> oldResources = getFullResourcesList(from);
            resourceDto = directoryService.createFolder(to);

            for (String oldResource : oldResources) {
                String oldName = ResourceUtils.getResourceName(oldResource);
                directoryService.createFolder(to + oldName + "/");
            }
            remove(from);
            return resourceDto;
        } else {

            return new ResourceDto(); //todo заглушка
        }
    }

    public List<ResourceDto> search(String query) throws ServerException, InsufficientDataException,
            ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException,
            InvalidResponseException, XmlParserException, InternalException {

        List<String> list = getFullResourcesList(ResourceUtils.getNameUserFolder());
        List<ResourceDto> resourceDtos = new ArrayList<>();
        for (String resource : list) {
            if (ResourceUtils.getResourceName(resource).equals(query)) {
                resourceDtos.add(getInfo(ResourceUtils.deleteNameUserFolder(resource)));
            }
        }
        return resourceDtos;
    }

    public List<ResourceDto> upload(String path, List<MultipartFile> files) throws IOException, ServerException,
            InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException,
            InvalidResponseException, XmlParserException, InternalException {

        if (files.isEmpty()) {
            //todo выкинуть исключение не валидные данные, код 400
            return List.of();
        }

        path = ResourceUtils.getPathToFolderUser(path);
        File tempFile = null;
        List<ResourceDto> resourceDtos = new ArrayList<>();

        for (MultipartFile file : files) {
            Path tempPath = Files.createTempFile("minio-", file.getOriginalFilename());
            tempFile = tempPath.toFile();

            file.transferTo(tempFile);

            String contentType = file.getContentType();
            if (contentType == null) {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }

            minioClient.uploadObject(
                    UploadObjectArgs.builder()
                            .bucket(ProjectConstants.NAME_MAIN_BUCKET)
                            .object(path + file.getOriginalFilename())
                            .filename(tempFile.getAbsolutePath())
                            .contentType(contentType)
                            .build());

            resourceDtos.add(ResourceDto.builder()
                    .path(path)
                    .name(file.getOriginalFilename())
                    .size(file.getSize())
                    .type(ResourceType.FILE)
                    .build());

            tempFile.delete();
        }

        return resourceDtos;
    }
}
