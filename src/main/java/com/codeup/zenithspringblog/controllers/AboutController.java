package com.codeup.zenithspringblog.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AboutController {

    @GetMapping("/about")
    public String returnAboutPage(@RequestParam int num) {
        System.out.println(num);
        return "about";
    }

}
