package com.dev.cloud_file_storage.controllers;

import com.dev.cloud_file_storage.dto.PersonDto;
import com.dev.cloud_file_storage.dto.PersonRegistrationDto;
import com.dev.cloud_file_storage.dto.PersonResponseDto;
import com.dev.cloud_file_storage.services.PersonDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final PersonDetailsService personDetailsService;

    @PostMapping("/sign-in")
    public ResponseEntity<PersonResponseDto> login(@RequestBody PersonDto personDto) {
        String username = personDto.getUsername();
        return ResponseEntity.status(HttpStatus.OK).body(new PersonResponseDto(username));
    }

    @PostMapping("/sign-up")
    public ResponseEntity<PersonResponseDto> signUp(@RequestBody PersonRegistrationDto personRegistrationDto) {
        System.out.println(personRegistrationDto);
        personDetailsService.register(personRegistrationDto);
        String username = personRegistrationDto.username();
        return ResponseEntity.status(HttpStatus.CREATED).body(new PersonResponseDto(username));
        //todo обработать валидацию
        //todo обработать случай, когда в БД имеется такой login
    }

    @PostMapping("/sign-out")
    public ResponseEntity<Void> signOut() {
        return ResponseEntity.noContent().build();
    }
}
