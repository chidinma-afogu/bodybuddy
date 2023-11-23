package com.Klusterthon.Medbot.controller;

import com.Klusterthon.Medbot.dto.request.UserRegistrationRequest;
import com.Klusterthon.Medbot.dto.response.ApiResponse;
import com.Klusterthon.Medbot.dto.response.AuthResponse;
import com.Klusterthon.Medbot.dto.response.UserResponse;
import com.Klusterthon.Medbot.repository.RoleRepository;
import com.Klusterthon.Medbot.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/auth")
@AllArgsConstructor
public class AuthController {
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody UserRegistrationRequest profile) {
        return ResponseEntity.ok(authService.login(profile));
    }
    @PostMapping("logout")
    public String logout() {
        return "logout successful";
    }


}
