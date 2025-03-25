package com.iguroo.task.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iguroo.task.dto.LoginDto;
import com.iguroo.task.dto.UserDto;
import com.iguroo.task.service.AuthService;

@RestController

@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;
    
    
    // Register an ADMIN - Only an existing ADMIN can call this API
    @PostMapping("/register/admin/{adminId}")
    public ResponseEntity<String> registerAdmin(@RequestBody UserDto userDto, @PathVariable Long adminId) {
        String response = authService.registerAdmin(userDto, adminId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register( @RequestBody UserDto userDto) {
        String response = authService.register(userDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDto loginDto) {
        String response = authService.login(loginDto);
        return ResponseEntity.ok(response); // Returning token as JSON
    }
}