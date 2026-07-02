package com.dev.cloud_file_storage.dto.resource_dto;

import com.dev.cloud_file_storage.enums.ResourceType;
import lombok.*;

@Getter
@ToString
@NoArgsConstructor
public class FileDto extends DirectoryDto{
    private int size;

    public FileDto(String path, String name, int size, ResourceType type) {
        super(path, name, type);
        this.size = size;
    }
}
