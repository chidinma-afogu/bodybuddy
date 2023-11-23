package com.Klusterthon.Medbot.email.service;

import com.Klusterthon.Medbot.dto.response.ApiResponse;
import com.Klusterthon.Medbot.email.dto.EmailRequest;

public interface EmailService {
//    List<Email> getAllEmailToken();
ApiResponse verifyEmail(String token);
    boolean sendVerificationLink(EmailRequest request);


//        String sendVerificationLinkForForgotPassword(String email);
//    ResponseEntity<?> verifyEmailForForgotPassword(String verificationToken);



}
