package com.dev.cloud_file_storage.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequestMapping("/api/resource")
@Tag(name = "Resource controller", description = "interaction with resources")
public interface ResourceApi {
    @GetMapping
    @Operation(summary = "getting resource")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "resource successfully obtained"),
            @ApiResponse(responseCode = "400", description = "invalid path"),
            @ApiResponse(responseCode = "404", description = "resource not found"),
            @ApiResponse(responseCode = "500", description = "unknown error")
    })
    ResponseEntity<?> getResource(@RequestParam(name = "path", required = false) String path);

    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    @Operation(summary = "loading a resource to the server")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "resource loaded successfully"),
            @ApiResponse(responseCode = "409", description = "resource already exists"),
            @ApiResponse(responseCode = "500", description = "unknown error")
    })
    ResponseEntity<?> uploadResource(@RequestParam (name = "path", required = false) String path,
                                     @RequestParam("object") List<MultipartFile> files);

    @GetMapping("/download")
    @Operation(summary = "loading a resource from the server")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "resource downloaded successfully"),
            @ApiResponse(responseCode = "400", description = "invalid path"),
            @ApiResponse(responseCode = "404", description = "resource not found"),
            @ApiResponse(responseCode = "500", description = "unknown error")
    })
    ResponseEntity<?> downloadResource(@RequestParam (name = "path", required = false) String path,
                                       HttpServletResponse response);

    @DeleteMapping
    @Operation(summary = "removing resource")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "resource deleted successfully"),
            @ApiResponse(responseCode = "400", description = "invalid path"),
            @ApiResponse(responseCode = "404", description = "resource not found"),
            @ApiResponse(responseCode = "500", description = "unknown error")
    })
    ResponseEntity<?> removeResource(@RequestParam (name = "path", required = false) String path);

    @PostMapping("/move")
    @Operation(summary = "resource movement")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "invalid path"),
            @ApiResponse(responseCode = "404", description = "resource not found"),
            @ApiResponse(responseCode = "409", description = "resource already exists"),
            @ApiResponse(responseCode = "500", description = "unknown error")
    })
    ResponseEntity<?> moveResource(@RequestParam (name = "from", required = false) String from,
                                   @RequestParam (name = "to", required = false) String to);

    @GetMapping("/search")
    @Operation(summary = "resource search")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "invalid query"),
            @ApiResponse(responseCode = "500", description = "unknown error")
    })
    ResponseEntity<?> searchResource(@RequestParam (name = "query", required = false) String query);
}
