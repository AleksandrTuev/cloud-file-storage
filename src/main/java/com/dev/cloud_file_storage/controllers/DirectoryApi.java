package com.dev.cloud_file_storage.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/api/directory")
@Tag(name = "Directory controller")
public interface DirectoryApi {
    @GetMapping
    @Operation(summary = "getting information", description = "valid path: '/', 'folder1/', 'folder1/folder2/'")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "folder information successfully retrieved"),
            @ApiResponse(responseCode = "400", description = "invalid path"),
            @ApiResponse(responseCode = "404", description = "resource not found"),
            @ApiResponse(responseCode = "500", description = "unknown error")
    })
    ResponseEntity<?> getFolderInfo(@RequestParam(name = "path", required = false) String path);

    @PostMapping
    @Operation(summary = "creating folder", description = "valid path: 'folder1', 'folder1/', 'folder1/folder1-1'")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "folder successfully created"),
            @ApiResponse(responseCode = "400", description = "invalid path"),
            @ApiResponse(responseCode = "409", description = "folder already exists"),
            @ApiResponse(responseCode = "500", description = "unknown error")
    })
    ResponseEntity<?> createFolder(@RequestParam (name = "path", required = false) String path);

}
