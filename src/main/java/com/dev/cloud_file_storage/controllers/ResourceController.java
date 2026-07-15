package com.dev.cloud_file_storage.controllers;

import com.dev.cloud_file_storage.services.ResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/resource")
@RequiredArgsConstructor
@Tag(name = "Resource controller", description = "interaction with resources")
public class ResourceController {
    private final ResourceService resourceService;

    @GetMapping
    @Operation(summary = "getting resource")
    @ApiResponse(responseCode = "200", description = "resource successfully obtained")
    @ApiResponse(responseCode = "400", description = "invalid path")
    @ApiResponse(responseCode = "404", description = "resource not found")
    @ApiResponse(responseCode = "500", description = "unknown error")
    public ResponseEntity<?> getResource(@RequestParam (name = "path", required = false) String path) {

        return ResponseEntity.ok().body(resourceService.getInfo(path));
    }

    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    @Operation(summary = "loading a resource to the server")
    @ApiResponse(responseCode = "201", description = "resource loaded successfully")
    @ApiResponse(responseCode = "409", description = "resource already exists")
    @ApiResponse(responseCode = "500", description = "unknown error")
    public ResponseEntity<?> uploadResource(@RequestParam (name = "path", required = false) String path,
                                            @RequestParam("object") List<MultipartFile> files) {

        return ResponseEntity.status(HttpStatus.CREATED).body(resourceService.upload(path, files));
    }

    @GetMapping("/download")
    @Operation(summary = "loading a resource from the server")
    @ApiResponse(responseCode = "200", description = "resource downloaded successfully")
    @ApiResponse(responseCode = "400", description = "invalid path")
    @ApiResponse(responseCode = "404", description = "resource not found")
    @ApiResponse(responseCode = "500", description = "unknown error")
    public ResponseEntity<?> downloadResource(@RequestParam (name = "path", required = false) String path,
                                              HttpServletResponse response) {

        resourceService.download(path, response);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).build();
    }

    @DeleteMapping
    @Operation(summary = "removing resource")
    @ApiResponse(responseCode = "204", description = "resource deleted successfully")
    @ApiResponse(responseCode = "400", description = "invalid path")
    @ApiResponse(responseCode = "404", description = "resource not found")
    @ApiResponse(responseCode = "500", description = "unknown error")
    public ResponseEntity<?> removeResource(@RequestParam (name = "path", required = false) String path) {

            resourceService.remove(path);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/move")
    @Operation(summary = "resource movement")
    @ApiResponse(responseCode = "400", description = "invalid path")
    @ApiResponse(responseCode = "404", description = "resource not found")
    @ApiResponse(responseCode = "409", description = "resource already exists")
    @ApiResponse(responseCode = "500", description = "unknown error")
    public ResponseEntity<?> moveResource(@RequestParam (name = "from", required = false) String from,
                                          @RequestParam (name = "to", required = false) String to) {

        return ResponseEntity.ok().body(resourceService.move(from, to));
    }

    @GetMapping("/search")
    @Operation(summary = "resource search")
    @ApiResponse(responseCode = "400", description = "invalid query")
    @ApiResponse(responseCode = "500", description = "unknown error")
    public ResponseEntity<?> searchResource(@RequestParam (name = "query", required = false) String query) {

        return ResponseEntity.ok().body(resourceService.search(query));
    }
}
