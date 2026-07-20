package com.dev.cloud_file_storage.controllers.impl;

import com.dev.cloud_file_storage.controllers.ResourceApi;
import com.dev.cloud_file_storage.services.ResourceService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ResourceController implements ResourceApi {
    private final ResourceService resourceService;

    @Override
    public ResponseEntity<?> getResource(@RequestParam (name = "path", required = false) String path) {

        return ResponseEntity.ok().body(resourceService.getInfo(path));
    }

    @Override
    public ResponseEntity<?> uploadResource(@RequestParam (name = "path", required = false) String path,
                                            @RequestParam("object") List<MultipartFile> files) {

        return ResponseEntity.status(HttpStatus.CREATED).body(resourceService.upload(path, files));
    }

    @Override
    public ResponseEntity<?> downloadResource(@RequestParam (name = "path", required = false) String path,
                                              HttpServletResponse response) {

        resourceService.download(path, response);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).build();
    }

    @Override
    public ResponseEntity<?> removeResource(@RequestParam (name = "path", required = false) String path) {

            resourceService.remove(path);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<?> moveResource(@RequestParam (name = "from", required = false) String from,
                                          @RequestParam (name = "to", required = false) String to) {

        return ResponseEntity.ok().body(resourceService.move(from, to));
    }

    @Override
    public ResponseEntity<?> searchResource(@RequestParam (name = "query", required = false) String query) {

        return ResponseEntity.ok().body(resourceService.search(query));
    }
}
