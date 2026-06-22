package com.dev.cloud_file_storage.controllers;

import com.dev.cloud_file_storage.dto.PersonDto;
import com.dev.cloud_file_storage.dto.PersonRegistrationDto;
import com.dev.cloud_file_storage.dto.PersonResponseDto;
import com.dev.cloud_file_storage.services.AuthService;
import com.dev.cloud_file_storage.services.PersonDetailsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final PersonDetailsService personDetailsService;
    private final AuthService authService;

    @PostMapping("/sign-in")
    public ResponseEntity<PersonResponseDto> signIn(@Valid @RequestBody PersonDto personDto) {
        PersonResponseDto personResponseDto = authService.login(personDto);
        return ResponseEntity.status(HttpStatus.OK).body(personResponseDto);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<PersonResponseDto> signUp(@RequestBody PersonRegistrationDto personRegistrationDto) {
        PersonResponseDto personResponseDto = authService.register(personRegistrationDto);
//        personDetailsService.register(personRegistrationDto);
//        String username = personRegistrationDto.username();
        return ResponseEntity.status(HttpStatus.CREATED).body(personResponseDto);
    }

    @PostMapping("/sign-out")
    public ResponseEntity<Void> signOut() {
        return ResponseEntity.noContent().build();
    }
}
