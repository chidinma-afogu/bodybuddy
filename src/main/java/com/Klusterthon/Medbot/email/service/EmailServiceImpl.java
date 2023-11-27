package com.Klusterthon.Medbot.email.service;

import com.Klusterthon.Medbot.dto.response.ApiResponse;
import com.Klusterthon.Medbot.email.dto.EmailRequest;
import com.Klusterthon.Medbot.email.dto.VerifyEmailResponse;
import com.Klusterthon.Medbot.email.entity.Email;
import com.Klusterthon.Medbot.email.repository.EmailRepository;
import com.Klusterthon.Medbot.exception.CustomException;
import com.Klusterthon.Medbot.model.User;
import com.Klusterthon.Medbot.model.enums.EmailStatus;
import com.Klusterthon.Medbot.model.enums.RecordStatus;
import com.Klusterthon.Medbot.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

@Service

@Slf4j
public class EmailServiceImpl implements EmailService {
    private JavaMailSender mailSender;
    private UserRepository userRepository;
    private EmailRepository emailRepository;

    @Autowired
    public EmailServiceImpl(JavaMailSender mailSender, UserRepository userRepository, EmailRepository emailRepository) {
        this.mailSender = mailSender;
        this.userRepository = userRepository;
        this.emailRepository = emailRepository;
    }

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Override
    public boolean sendVerificationLink(EmailRequest request) {
        String verificationToken = UUID.randomUUID().toString();
        Email email = emailRepository.findByEmailIgnoreCase(request.getUserEmail())
                .orElseThrow(() -> new CustomException("Email does not exist", HttpStatus.BAD_REQUEST));
        email.setToken(verificationToken);
        emailRepository.save(email);
        String baseUrl = "https://bodybuddy.onrender.com";

        String verificationLink = baseUrl + "/api/v1/email?token=" + verificationToken;
        try {
            MimeMessage message = mailSender.createMimeMessage();
            var messageHelper = new MimeMessageHelper(message);
            messageHelper.setFrom(senderEmail, "BodyBuddy");
            messageHelper.setTo(request.getUserEmail());
            messageHelper.setSubject(request.getSubject());
            messageHelper.setText(request.getText() + "\n\n" + verificationLink, true);
            mailSender.send(message);
            return true;
        } catch (MailException e) {
            throw new CustomException("Error sending email", HttpStatus.BAD_REQUEST);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public ApiResponse resendEmail(EmailRequest request){
        String subject = "Email Verification";
        String text = "Please click the link below to verify your email";
        sendVerificationLink(EmailRequest.builder()
                .userEmail(request.getUserEmail())
                .subject(subject)
                .text(text)
                .build());
        return ApiResponse.builder()
                .code(String.valueOf(HttpStatus.OK.value()))
                .message("Email sent successfully")
                .data(null)
                .build();
    }

    @Override
    public ApiResponse verifyEmail(String token) {
        if (token == null || token.isEmpty()) {
            throw new CustomException("please pass in verification token", HttpStatus.BAD_REQUEST);
        }
        Email email = emailRepository.findByTokenAndStatus(token, EmailStatus.NOT_VERIFIED)
                .orElseThrow(() -> new CustomException("Email has been verified", HttpStatus.BAD_GATEWAY));
        if (!token.equals(email.getToken())) {
            throw new CustomException("Invalid token", HttpStatus.BAD_REQUEST);
        }
        User user = userRepository.findByEmailAndStatus(email.getEmail(),RecordStatus.INACTIVE);
        user.setStatus(RecordStatus.ACTIVE);
        email.setStatus(EmailStatus.VERIFIED);
        userRepository.save(user);
        emailRepository.save(email);
        VerifyEmailResponse verifyEmailResponse = VerifyEmailResponse.builder()
                .id(email.getId())
                .email(user.getEmail())
                .verifiedAt(email.getCreatedAt())
                .status(email.getStatus())
                .build();
        return ApiResponse.builder()
                .code(HttpStatus.OK.toString())
                .message("Email verified successfully")
                .data(verifyEmailResponse)
                .build();
    }
}
//        RedirectView redirectView = new RedirectView();
//        redirectView.setUrl("http://localhost:3000/services");//login page
//        return redirectView; // Redirect to the success URL after email verification

// Create a RedirectView for the unsuccessful verification case
//    RedirectView redirectView = new RedirectView();
//        redirectView.setUrl("http://localhost:3000/auth/login");
//        return redirectView; // Redirect to the unsuccessful URL
//}
//
//   @Override
//    public List<Email> getAllEmailToken(){
//        return emailRepository.findAll();
//    }
//
//
//
//    //TODO : write method for email verification
//    @Override
//    public String sendVerificationLinkForForgotPassword(String email) {
//        // Generate a verification token
//        String verificationToken = UUID.randomUUID().toString();
//
//        // Save the verification token along with the user's ID in a database or cache
//        User profile = userRepository.findByEmail(email).orElseThrow();
//        log.info("Email found" + profile.getEmail());
//        profile.setEmailVerificationToken(verificationToken);
//        profile.setEmailVerificationStatus(false);
//        userRepository.save(profile);
//
//        // Compose the verification link with the token
//        String verificationLink = "http://ecoguard.us-east-1.elasticbeanstalk.com/api/v1/email/verifyForForgotPassword/" + verificationToken;
//        try {
//            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
//            simpleMailMessage.setFrom(senderEmail);
//            simpleMailMessage.setTo(email);
//            simpleMailMessage.setSubject("Change Password");
//            simpleMailMessage.setText("Please click the following link to change your password: " + verificationLink);
//
//            mailSender.send(simpleMailMessage);
//            Email email1= new Email();
//            email1.setEmail(profile.getEmail());
//            email1.setToken(verificationToken);
//            emailRepository.save(email1);
//            return "Mail sent successfully";
//        } catch (MailException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public ResponseEntity<?> verifyEmailForForgotPassword(String verificationToken) {
//        // Retrieve the user's ID based on the verification token from the database or cache
//        User profile = userRepository.findByEmailVerificationToken(verificationToken).orElseThrow();
//
//        if (profile.getId() != null) {
//            profile.setEmailVerificationStatus(true);
//            userRepository.save(profile);
//            RedirectView redirectView = new RedirectView();
//            redirectView.setUrl("http://localhost:3000/reset-password");
//            return new ResponseEntity<>(redirectView, HttpStatus.OK); // Redirect to the success URL after email verification
//        }
//        return new ResponseEntity<>("Unsuccessful",HttpStatus.BAD_REQUEST); // Redirect to the unsuccessful URL
//    }
//}
//
