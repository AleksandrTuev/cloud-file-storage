package com.dev.cloud_file_storage.controllers;

import com.dev.cloud_file_storage.dto.resource_dto.DirectoryDto;
import com.dev.cloud_file_storage.services.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/resource")
@RequiredArgsConstructor
public class ResourceController {
    private final ResourceService resourceService;

    @GetMapping
    public ResponseEntity<?> getResource(@RequestParam (name = "path", required = false) String path) {
        //получение информации о ресурсе (папки или файле / у пути папки в конце должно быть - /)
//        return resourceService.getResourceInfo(path);
        return ResponseEntity.ok().body(resourceService.getResourceInfo(path));
    }
}
