package com.Klusterthon.Medbot.controller;

import com.Klusterthon.Medbot.dto.response.ApiResponse;
import com.Klusterthon.Medbot.email.dto.EmailRequest;
import com.Klusterthon.Medbot.email.service.EmailServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/email")
@AllArgsConstructor
public class EmailController {

        private EmailServiceImpl emailServiceImpl;

//        @PostMapping("send")
//        public String sendVerificationLink(){
//            return emailServiceImpl.sendVerificationLink(id);
//        }

        @GetMapping("")
        public ApiResponse verifyEmail(@RequestParam(name = "token")  String token) {
            return emailServiceImpl.verifyEmail(token);
        }
        @PostMapping("")
        public boolean sendVerificationLink(@RequestBody EmailRequest request) {
            return emailServiceImpl.sendVerificationLink(request);
        }

//        @GetMapping("verifyForForgotPassword/{token}")
//        public ResponseEntity<?> verifyEmailForForgotPassword(@PathVariable(name = "token") String verificationToken) {
//            return emailServiceImpl.verifyEmailForForgotPassword(verificationToken);
//        }
//
//        @GetMapping("getAllEmailToken")
//        public List<Email> getAllEmailToken(){
//            return emailServiceImpl.getAllEmailToken();
//        }



}
