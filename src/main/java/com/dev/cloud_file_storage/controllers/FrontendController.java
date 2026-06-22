package com.dev.cloud_file_storage.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrontendController {
    @GetMapping(value = {"/registration", "/login", "/help", "/files/**"})
    public String handleRefresh() {
        return "forward:/index.html";
    }
}
