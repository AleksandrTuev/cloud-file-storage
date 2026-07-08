package com.dev.cloud_file_storage.controllers;

import com.dev.cloud_file_storage.services.ResourceService;
import io.minio.errors.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/api/resource")
@RequiredArgsConstructor
public class ResourceController {
    private final ResourceService resourceService;

    @GetMapping
    public ResponseEntity<?> getResource(@RequestParam (name = "path", required = false) String path)
            throws ServerException, InsufficientDataException, ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException,
            InternalException {

        return ResponseEntity.ok().body(resourceService.getInfo(path));
    }

    @GetMapping("/download")
    public ResponseEntity<?> downloadResource(@RequestParam (name = "path", required = false) String path,
                                              HttpServletResponse response)
            throws ServerException, InsufficientDataException, ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException,
            InternalException {

        resourceService.download(path, response);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).build();
    }

    @DeleteMapping
    public ResponseEntity<?> removeResource(@RequestParam (name = "path", required = false) String path)
            throws ServerException, InsufficientDataException, ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException,
            InternalException {

            resourceService.remove(path);
        return ResponseEntity.noContent().build();
    }
}
