package com.Klusterthon.Medbot.controller;

import com.Klusterthon.Medbot.dto.response.ApiResponse;
import com.Klusterthon.Medbot.email.dto.EmailRequest;
import com.Klusterthon.Medbot.email.service.EmailServiceImpl;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/email")
@AllArgsConstructor
public class EmailController {

        private EmailServiceImpl emailServiceImpl;

        @GetMapping("")
        public ApiResponse verifyEmail(@RequestParam(name = "token")  String token) {
            return emailServiceImpl.verifyEmail(token);
        }
        @PostMapping("")
        public ApiResponse resendEmail(@RequestBody EmailRequest request) {
            return emailServiceImpl.resendEmail(request);
        }

//        @GetMapping("verifyForForgotPassword/{token}")
//        public ResponseEntity<?> verifyEmailForForgotPassword(@PathVariable(name = "token") String verificationToken) {
//            return emailServiceImpl.verifyEmailForForgotPassword(verificationToken);
//        }
//



}
