package com.dev.cloud_file_storage.services;

import com.dev.cloud_file_storage.dto.PersonDto;
import com.dev.cloud_file_storage.dto.PersonRegistrationDto;
import com.dev.cloud_file_storage.dto.PersonResponseDto;
import com.dev.cloud_file_storage.mapper.PersonMapper;
import com.dev.cloud_file_storage.models.Person;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final PersonMapper personMapper;
    private final PersonService personService;

    public PersonResponseDto register(PersonRegistrationDto personRegistrationDto) {
        Person person = personMapper.toPerson(personRegistrationDto);
        personService.create(person);
        authenticate(personRegistrationDto.username(), personRegistrationDto.password());
        return new PersonResponseDto(personRegistrationDto.username());
    }

    public PersonResponseDto login(PersonDto personDto) {
        authenticate(personDto.username(), personDto.password());
        return new PersonResponseDto(personDto.username());
    }

    private void authenticate(String username, String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                username, password);
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
