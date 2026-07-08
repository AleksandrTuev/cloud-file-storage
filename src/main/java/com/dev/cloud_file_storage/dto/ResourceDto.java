package com.dev.cloud_file_storage.dto;

import com.dev.cloud_file_storage.enums.ResourceType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResourceDto {
    private String path;
    private String name;
    private long size;
    private ResourceType type;
}
