package com.dev.cloud_file_storage.controllers;

import com.dev.cloud_file_storage.dto.ResourceDto;
import com.dev.cloud_file_storage.services.ResourceService;
import io.minio.errors.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

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

//    @PostMapping
    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> uploadResource(@RequestParam (name = "path", required = false) String path,
                                            @RequestParam("object") List<MultipartFile> files) throws IOException,
            ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException,
            InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
//        return ResponseEntity.ok().build();
        List<ResourceDto> list = resourceService.upload(path, files);

//        if (list.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
//        }
        return ResponseEntity.status(HttpStatus.CREATED).body(list);
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

    @PostMapping("/move")
    public ResponseEntity<?> moveResource(@RequestParam (name = "from", required = false) String from,
                                          @RequestParam (name = "to", required = false) String to)
            throws ServerException, InsufficientDataException, ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException,
            InternalException {

        return ResponseEntity.ok().body(resourceService.move(from, to));
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchResource(@RequestParam (name = "query", required = false) String query)
            throws ServerException, InsufficientDataException, ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException,
            InternalException {

        return ResponseEntity.ok().body(resourceService.search(query));
    }
}
