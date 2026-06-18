package com.dev.cloud_file_storage.controllers;

import com.dev.cloud_file_storage.dto.PersonResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@RequestMapping("/api/user")
public class UserController {

    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public PersonResponseDto getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        return new PersonResponseDto(userDetails.getUsername());
    }
}
