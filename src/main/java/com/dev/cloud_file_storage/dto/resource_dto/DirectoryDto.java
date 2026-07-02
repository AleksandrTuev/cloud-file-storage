package com.dev.cloud_file_storage.dto.resource_dto;

import com.dev.cloud_file_storage.enums.ResourceType;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class DirectoryDto {
    private String path;
    private String name;
    private ResourceType type;
}
