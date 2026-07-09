package com.dev.cloud_file_storage.services;

import com.dev.cloud_file_storage.dto.ResourceDto;
import com.dev.cloud_file_storage.utils.ResourceUtils;
import io.minio.Result;
import io.minio.StatObjectResponse;
import io.minio.errors.*;
import io.minio.messages.Item;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class ResourceService {
    private final DirectoryService directoryService;
    private final MinioService minioService;

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
        for (Result<Item> result : minioService.getList(path)) {
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
        for (Result<Item> result : minioService.getList(path)) {
            Item item = result.get();
            minioService.remove(item.objectName());
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
            minioService.download(path, ResourceUtils.getResourceName(path));
        }
    }

    private boolean isDirectory(String path) {
        return path.trim().endsWith("/");
    }

    private ResourceDto getDirectoryInfo(String path) {
        return ResourceUtils.getDirectoryDto(ResourceUtils.getParentPath(path), ResourceUtils.getResourceName(path));
    }

    private ResourceDto getFileInfo(String path) throws ServerException, InsufficientDataException,
            ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException,
            InvalidResponseException, XmlParserException, InternalException {

        StatObjectResponse stat = minioService.getStat(path);
        long resourceSize = stat.size();

        return ResourceUtils.getFileDto(ResourceUtils.getParentPath(path), ResourceUtils.getResourceName(path),
                resourceSize);
    }

    public ResourceDto move(String from, String to) throws ServerException, InsufficientDataException,
            ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException,
            InvalidResponseException, XmlParserException, InternalException {

        String oldParentPath = ResourceUtils.getParentPath(from);
        String newParentPath = ResourceUtils.getParentPath(to);
        String oldNameResource = ResourceUtils.getResourceName(from);
        String newNameResource = ResourceUtils.getResourceName(to);
        ResourceDto resourceDto = new ResourceDto();

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
            for (Result<Item> result : minioService.getList(from)) {
                Item item = result.get();

                if (from.equals(item.objectName())) {
                    minioService.copy(from, ResourceUtils.getNameUserFolder() + to);

                    remove(from);
                    resourceDto = ResourceUtils.getFileDto(newParentPath, newNameResource, item.size());
                }
            }

        return resourceDto;
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

    public ResourceDto upload(String path, MultipartFile file) throws IOException, ServerException,
            InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException,
            InvalidResponseException, XmlParserException, InternalException {

        if (file.isEmpty()) {
            //todo выкинуть исключение не валидные данные, код 400
//            return List.of();
        }

        path = ResourceUtils.getPathToFolderUser(path);
        Path tempPath = Files.createTempFile("minio-", ResourceUtils.getResourceName(file.getOriginalFilename()));
        File tempFile = tempPath.toFile();

        file.transferTo(tempFile);

        String contentType = file.getContentType();
        if (contentType == null) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }
        minioService.upload(path + file.getOriginalFilename(), tempFile.getAbsolutePath(), contentType);
        tempFile.delete();

        return ResourceUtils.getFileDto(path, file.getOriginalFilename(), file.getSize());
    }
}
