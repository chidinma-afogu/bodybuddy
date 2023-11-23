package com.Klusterthon.Medbot.controller;

import com.Klusterthon.Medbot.dto.request.UpdateUserRequest;
import com.Klusterthon.Medbot.dto.request.UserRegistrationRequest;
import com.Klusterthon.Medbot.dto.response.ApiResponse;
import com.Klusterthon.Medbot.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/user")
@AllArgsConstructor
public class UserController {
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> signup(@RequestBody UserRegistrationRequest profile){
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.signup(profile));
    }
    @PutMapping("/update")
    public ResponseEntity<ApiResponse> updateUser(@RequestBody UpdateUserRequest request){
        return ResponseEntity.status(HttpStatus.OK).body(userService.updateUser(request));
    }
    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(userService.deleteUser(id));
    } @GetMapping("{id}")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserById(id));
    }
}
