package com.Klusterthon.Medbot.service.serviceImpl;

import com.Klusterthon.Medbot.dto.response.ApiResponse;
import com.Klusterthon.Medbot.email.dto.EmailRequest;
import com.Klusterthon.Medbot.email.service.EmailService;
import com.Klusterthon.Medbot.exception.CustomException;
import com.Klusterthon.Medbot.model.User;
import com.Klusterthon.Medbot.repository.UserRepository;
import com.Klusterthon.Medbot.service.UserService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class UserServiceImpl implements UserService {
    private EmailService emailService;
    private UserRepository userRepository;

    public ApiResponse forgotPassword(String email) {
        User user = userRepository.findByEmailIgnoreCase(email).get();
        if(user.getEmail() == null){
            throw new CustomException("Email does not exist",HttpStatus.OK);
        }
        String subject = "Reset Password";
        String text = "Hi there,\n" +
                "\n" +
                "We received a request to reset your password for your BodyBuddy account. To proceed with the password reset, please click on the link below:\n\n";
        EmailRequest emailRequest = EmailRequest.builder()
                .userEmail(email)
                .text(text)
                .subject(subject)
                .build();
        try {
            emailService.sendVerificationLink(emailRequest);
        } catch (Exception e) {
            throw new CustomException("Error sending mail", HttpStatus.BAD_REQUEST);
        }
        return ApiResponse.builder()
                .code(HttpStatus.OK.toString())
                .data(null)
                .message("mail sent successfully")
                .build();
    }

//    public ApiResponse resetPassword(){
//
//    }
}
