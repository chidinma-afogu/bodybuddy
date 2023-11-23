package com.Klusterthon.Medbot.service.serviceImpl;


import com.Klusterthon.Medbot.dto.request.UserRegistrationRequest;
import com.Klusterthon.Medbot.dto.response.*;
import com.Klusterthon.Medbot.email.dto.EmailRequest;
import com.Klusterthon.Medbot.email.entity.Email;
import com.Klusterthon.Medbot.email.repository.EmailRepository;
import com.Klusterthon.Medbot.email.service.EmailService;
import com.Klusterthon.Medbot.exception.CustomException;
import com.Klusterthon.Medbot.model.Role;
import com.Klusterthon.Medbot.model.User;
import com.Klusterthon.Medbot.model.enums.EmailStatus;
import com.Klusterthon.Medbot.model.enums.RecordStatus;
import com.Klusterthon.Medbot.repository.RoleRepository;
import com.Klusterthon.Medbot.repository.UserRepository;
import com.Klusterthon.Medbot.security.JwtTokenProvider;
import com.Klusterthon.Medbot.security.token.Token;
import com.Klusterthon.Medbot.security.token.TokenRepository;
import com.Klusterthon.Medbot.security.token.TokenType;
import com.Klusterthon.Medbot.service.AuthService;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {
    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtTokenProvider jwtTokenProvider;
    private RoleRepository roleRepository;
    private TokenRepository tokenRepository;
    private EmailService emailService;
    private EmailRepository emailRepository;
    @Override
    public ApiResponse login(UserRegistrationRequest request) {
        User foundUser = userRepository.findByEmailIgnoreCase(request.getEmail())
                .orElseThrow(()->new CustomException("Bad credentials",
                        HttpStatus.BAD_REQUEST));
        Email email = emailRepository.findByUser(foundUser);
        if(email.getStatus() == EmailStatus.NOT_VERIFIED || email.getStatus() == EmailStatus.INACTIVE){
            throw new CustomException("Email not verified",HttpStatus.BAD_REQUEST);
        }
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        AuthResponse authResponse = AuthResponse.builder()
                .responseCode(String.valueOf(HttpStatus.OK))
                .responseMessage("Token successfully generated")
                .body(AuthResponseBody.builder()
                        .accessToken(jwtTokenProvider.generateToken(authentication))
                        .build())
                .build();
        ApiResponse apiResponse = new ApiResponse(HttpStatus.OK.toString(),
                authResponse.getResponseMessage(),authResponse.getBody(),HttpStatus.OK);
        revokeAllUserTokens(foundUser);
        saveUserToken(authentication, foundUser);
        return apiResponse;
    }

    @Override
    public ApiResponse signup(UserRegistrationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw  new CustomException("Email is already taken", HttpStatus.BAD_REQUEST);
        }

        if(request.getEmail().equals(Strings.EMPTY)){
            throw  new CustomException("Email should be passed", HttpStatus.BAD_REQUEST);
        }

        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        if (!isInputValid(request.getEmail(), emailRegex)) {
            throw  new CustomException("Invalid email address", HttpStatus.BAD_REQUEST);
        }

        User user = User.builder()
                .email(request.getEmail().toLowerCase())
                .password(passwordEncoder.encode(request.getPassword()))
                .status(RecordStatus.INACTIVE)
                .build();
        Role role = roleRepository.findByRoleName("ROLE_USER");
        user.setRoles(Collections.singleton(role));
        user.setRole(role.getRoleName());
        userRepository.save(user);

        Email email = Email.builder()
                .email(user.getEmail())
                .status(EmailStatus.NOT_VERIFIED)
                .token(null)
                .user(user)
                .build();
        emailRepository.save(email);

        UserResponse userResponse = UserResponse.builder()
                .responseCode(String.valueOf(HttpStatus.CREATED))
                .responseMessage("User created successfully")
                .body(UserResponseBody.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .status(user.getStatus())
                        .createdBy(user.getEmail())
                        .createdAt(user.getCreatedAt())
                        .build())
                .build();
        try {
            String subject = "Email Verification";
            String text = "Please click the link below to verify your email";
            EmailRequest emailRequest = EmailRequest.builder()
                    .userEmail(user.getEmail())
                    .subject(subject)
                    .text(text)
                    .build();
            emailService.sendVerificationLink(emailRequest);
        }catch (Exception e){
            throw new CustomException("Error sending email"+e,HttpStatus.BAD_REQUEST);
        }
        return new ApiResponse(HttpStatus.CREATED.toString(),
                userResponse.getResponseMessage(),userResponse.getBody(),HttpStatus.CREATED);
    }
    private void saveUserToken(Authentication authentication, User foundUser) {
        var token = Token.builder()
                .user(foundUser)
                .token(jwtTokenProvider.generateToken(authentication))
                .tokenType(TokenType.BEARER)
                .revoked(false)
                .expired(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokensById(user.getId());
        if (validUserTokens.isEmpty()) {
            return;
        }
        validUserTokens.forEach(t -> {
            t.setExpired(true);
            t.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    private boolean isInputValid(String input, String regex) {
        return Pattern.compile(regex)
                .matcher(input)
                .matches();
    }

}
