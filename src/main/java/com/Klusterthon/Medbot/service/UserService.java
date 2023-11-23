package com.Klusterthon.Medbot.service;

import com.Klusterthon.Medbot.dto.request.UpdateUserRequest;
import com.Klusterthon.Medbot.dto.request.UserRegistrationRequest;
import com.Klusterthon.Medbot.dto.response.ApiResponse;

public interface UserService {
    ApiResponse signup(UserRegistrationRequest profile);
    ApiResponse updateUser(UpdateUserRequest request);
    ApiResponse forgotPassword(String email);
    ApiResponse deleteUser(Long id);
    ApiResponse getUserById(Long id);
}
