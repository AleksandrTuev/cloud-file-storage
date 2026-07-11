package com.dev.cloud_file_storage.services;

import com.dev.cloud_file_storage.dto.ResourceDto;
import com.dev.cloud_file_storage.exception.InvalidPathException;
import com.dev.cloud_file_storage.exception.InvalidQueryException;
import com.dev.cloud_file_storage.exception.ResourceAlreadyExistsException;
import com.dev.cloud_file_storage.exception.ResourceNotFoundException;
import com.dev.cloud_file_storage.utils.ResourceUtils;
import io.minio.Result;
import io.minio.StatObjectResponse;
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
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
public class ResourceService {
    private final DirectoryService directoryService;
    private final MinioService minioService;

    public ResourceDto getInfo(String path) {

        ResourceDto resourceDto = null;
        path = ResourceUtils.getPathToFolderUser(path);

        for (Result<Item> result : minioService.getList(path)) {
            Item item = minioService.getItem(result);

            if (path.equals(item.objectName())) {
                if (item.objectName().endsWith("/")) {
                    resourceDto = getDirectoryInfo(path);
                } else {
                    resourceDto = getFileInfo(path);
                }
            }
        }

        if (resourceDto == null) {
            throw new ResourceNotFoundException("Resource not found");
        }
        return resourceDto;
    }

    public void remove(String path) {

        checkValidPath(path);
        checkExists(path);

        for (Result<Item> result : minioService.getList(path)) {
            Item item = minioService.getItem(result);
            minioService.remove(item.objectName());
        }
    }

    public void download(String path, HttpServletResponse response) {

        checkValidPath(path);
        checkExists(path);

        if (isDirectory(path)) {
            List<String> resources = getFullResourcesList(path);

            try (OutputStream outputStream = response.getOutputStream();
                 ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {

                for (String resource : resources) {
                    ZipEntry zipEntry = new ZipEntry(ResourceUtils.deleteNameUserFolder(resource));
                    zipOutputStream.putNextEntry(zipEntry);
                    zipOutputStream.closeEntry();
                }

                zipOutputStream.finish();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            minioService.download(path, ResourceUtils.getResourceName(path));
        }
    }

    public ResourceDto move(String from, String to) {

        String oldParentPath = ResourceUtils.getParentPath(from);
        String newParentPath = ResourceUtils.getParentPath(to);
        String oldNameResource = ResourceUtils.getResourceName(from);
        String newNameResource = ResourceUtils.getResourceName(to);
        ResourceDto resourceDto = new ResourceDto();

        checkValidPath(from);
        checkValidPath(to);
        checkExists(from);

        if (!oldNameResource.equals(newNameResource)) {
            to = ResourceUtils.deleteNameUserFolder(to);
        }

        checkDuplicate(newParentPath + newNameResource);

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
                Item item = minioService.getItem(result);

                if (from.equals(item.objectName())) {
                    minioService.copy(from, ResourceUtils.getNameUserFolder() + to);

                    remove(from);
                    resourceDto = ResourceUtils.getFileDto(newParentPath, newNameResource, item.size());
                }
            }
            return resourceDto;
        }
    }

    public List<ResourceDto> search(String query) {

        if (query == null || query.isEmpty()) {
            throw new InvalidQueryException("Query is null or empty");
        }
        List<String> list = getFullResourcesList(ResourceUtils.getNameUserFolder());
        List<ResourceDto> resourceDtos = new ArrayList<>();
        for (String resource : list) {
            if (ResourceUtils.getResourceName(resource).equals(query)) {
                resourceDtos.add(getInfo(ResourceUtils.deleteNameUserFolder(resource)));
            }
        }
        return resourceDtos;
    }

    public ResourceDto upload(String path, MultipartFile file) {
        try {
            path = ResourceUtils.getPathToFolderUser(path);

            Path tempPath = Files.createTempFile("minio-", ResourceUtils.getResourceName(file.getOriginalFilename()));
            File tempFile = tempPath.toFile();

            file.transferTo(tempFile);
            checkDuplicate(path + file.getOriginalFilename());

            String contentType = file.getContentType();
            if (contentType == null) {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }
            minioService.upload(path + file.getOriginalFilename(), tempFile.getAbsolutePath(), contentType);
            tempFile.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResourceUtils.getFileDto(path, file.getOriginalFilename(), file.getSize());
    }

    private void checkValidPath(String path) {
        if (path == null || path.isEmpty()) {
            throw new InvalidPathException("Invalid path");
        }
    }

    private void checkExists(String path) {

        if (!directoryService.isExists(path)) {
            throw new ResourceNotFoundException("Resource not found");
        }
    }

    private void checkDuplicate(String path) {

        if (directoryService.isExists(path)) {
            throw new ResourceAlreadyExistsException("Resource already exists");
        }
    }

    private boolean isDirectory(String path) {
        return path.trim().endsWith("/");
    }

    private ResourceDto getDirectoryInfo(String path) {
        return ResourceUtils.getDirectoryDto(ResourceUtils.getParentPath(path), ResourceUtils.getResourceName(path));
    }

    private ResourceDto getFileInfo(String path) {

        StatObjectResponse stat = minioService.getStat(path);
        long resourceSize = stat.size();

        return ResourceUtils.getFileDto(ResourceUtils.getParentPath(path), ResourceUtils.getResourceName(path),
                resourceSize);
    }

    private List<String> getResources(String path) {
        List<String> resources = new ArrayList<>();
        for (Result<Item> result : minioService.getList(path)) {
            Item item = minioService.getItem(result);

            if (path.equals(item.objectName())) {
                continue;
            }

            resources.add(item.objectName());
        }
        return resources;
    }

    private List<String> getFullResourcesList(String path) {
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
}
