package com.dev.cloud_file_storage.controllers;

import com.dev.cloud_file_storage.services.DirectoryService;
import io.minio.errors.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Directory controller")
public class DirectoryController {

    private final DirectoryService directoryService;

    @GetMapping
    @Operation(summary = "getting information")
    @ApiResponse(responseCode = "200", description = "folder information successfully retrieved")
    @ApiResponse(responseCode = "400", description = "invalid path")
    @ApiResponse(responseCode = "404", description = "resource not found")
    @ApiResponse(responseCode = "500", description = "unknown error")
    public ResponseEntity<?> getFolderInfo(@RequestParam(name = "path", required = false) String path)
            throws ServerException, InsufficientDataException, ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException,
            InternalException {

        return ResponseEntity.status(HttpStatus.OK).body(directoryService.getInfo(path));
    }

    @PostMapping
    @Operation(summary = "creating folder")
    @ApiResponse(responseCode = "200", description = "folder successfully created")
    @ApiResponse(responseCode = "400", description = "invalid path")
    @ApiResponse(responseCode = "409", description = "folder already exists")
    @ApiResponse(responseCode = "500", description = "unknown error")
    public ResponseEntity<?> createFolder(@RequestParam (name = "path", required = false) String path)
            throws ServerException, InsufficientDataException, ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException,
            InternalException {

        return ResponseEntity.status(HttpStatus.CREATED).body(directoryService.createFolder(path));
    }
}
