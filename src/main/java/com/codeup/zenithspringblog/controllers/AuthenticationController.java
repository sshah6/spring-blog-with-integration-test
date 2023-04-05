package com.codeup.zenithspringblog.controllers;

import jakarta.persistence.GeneratedValue;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthenticationController {

    @GetMapping("/login")
    public String returnLoginPage() {
        return "users/login";
    }

}
