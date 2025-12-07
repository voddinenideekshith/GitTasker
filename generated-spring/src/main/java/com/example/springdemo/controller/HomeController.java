package com.example.springdemo.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof OAuth2User oauth2User) {
                model.addAttribute("userName", oauth2User.getAttribute("login"));
                model.addAttribute("avatarUrl", oauth2User.getAttribute("avatar_url"));
            }
        }
        return "index";
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}
