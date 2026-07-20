package com.dev.cloud_file_storage.controllers.impl;

import com.dev.cloud_file_storage.controllers.DirectoryApi;
import com.dev.cloud_file_storage.services.DirectoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DirectoryController implements DirectoryApi {

    private final DirectoryService directoryService;

    @Override
    public ResponseEntity<?> getFolderInfo(@RequestParam(name = "path", required = false) String path) {

        return ResponseEntity.status(HttpStatus.OK).body(directoryService.getInfo(path));
    }

    @Override
    public ResponseEntity<?> createFolder(@RequestParam (name = "path", required = false) String path) {

        return ResponseEntity.status(HttpStatus.CREATED).body(directoryService.createFolder(path));
    }
}
