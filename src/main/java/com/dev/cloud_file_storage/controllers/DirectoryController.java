package com.dev.cloud_file_storage.controllers;

import com.dev.cloud_file_storage.services.DirectoryService;
import com.dev.cloud_file_storage.utils.ResourceUtils;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/api/directory")
@RequiredArgsConstructor
public class DirectoryController {

    private final DirectoryService directoryService;

    @GetMapping
    public ResponseEntity<?> getFolderInfo(@RequestParam(name = "path", required = false) String path)
            throws ServerException, InsufficientDataException, ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException,
            InternalException {

        if (path.isEmpty()) {
            path = ResourceUtils.getNameUserFolder(path);
        }
        return ResponseEntity.status(HttpStatus.OK).body(directoryService.getFolderInfo(path));
    }

    @PostMapping
    public ResponseEntity<?> createFolder(@RequestParam (name = "path", required = false) String path)
            throws ServerException, InsufficientDataException, ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException,
            InternalException {

        return ResponseEntity.status(HttpStatus.CREATED).body(directoryService.createFolder(path));
    }
}
