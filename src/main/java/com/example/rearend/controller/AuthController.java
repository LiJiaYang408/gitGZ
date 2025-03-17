package com.example.rearend.controller;


import com.example.rearend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password) {
        if (userService.validateUser(username, password)) {
            return "Login successful!";
        } else {
            return "Invalid username or password!";
        }
    }
}