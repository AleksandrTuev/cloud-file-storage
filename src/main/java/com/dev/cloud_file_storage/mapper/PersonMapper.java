package com.dev.cloud_file_storage.mapper;

import com.dev.cloud_file_storage.dto.PersonRegistrationDto;
import com.dev.cloud_file_storage.models.Person;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PersonMapper {

    @Mapping(target = "id", ignore = true)
    Person toPerson(PersonRegistrationDto personRegistrationDto);

}
