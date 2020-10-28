package com.roman.sprboot.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController {

    @GetMapping("/admin")
    public String admin(){
        return "admin";
    }
}
