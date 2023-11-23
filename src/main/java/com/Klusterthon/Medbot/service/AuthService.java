package com.Klusterthon.Medbot.service;


import com.Klusterthon.Medbot.dto.request.UserRegistrationRequest;
import com.Klusterthon.Medbot.dto.response.ApiResponse;
import com.Klusterthon.Medbot.dto.response.AuthResponse;
import com.Klusterthon.Medbot.dto.response.UserResponse;

public interface AuthService {
    ApiResponse login(UserRegistrationRequest profile);
    ApiResponse signup(UserRegistrationRequest profile);
}
