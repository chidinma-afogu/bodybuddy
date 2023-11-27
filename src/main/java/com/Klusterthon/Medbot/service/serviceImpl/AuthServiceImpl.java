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
    private JwtTokenProvider jwtTokenProvider;
    private TokenRepository tokenRepository;
    private EmailRepository emailRepository;
    @Override
    public ApiResponse login(UserRegistrationRequest request) {
        // Retrieve the user based on the provided email
        User foundUser = userRepository.findByEmailIgnoreCaseAndStatus(request.getEmail(), RecordStatus.ACTIVE)
                .orElseThrow(() -> new CustomException("Bad credentials", HttpStatus.BAD_REQUEST));

        // Authenticate the user's credentials
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate the access token
        String accessToken = jwtTokenProvider.generateToken(authentication);

        // Check if token generation succeeded before proceeding
        if (accessToken != null && !accessToken.isEmpty()) {
            // Revoke existing user tokens and save new token
            revokeAllUserTokens(foundUser);
            saveUserToken(authentication, foundUser);

            // Construct the response body
            AuthResponseBody responseBody = AuthResponseBody.builder()
                    .accessToken(accessToken)
                    .userRole(foundUser.getRole())
                    .user(User.builder()
                            .status(foundUser.getStatus())
                            .id(foundUser.getId())
                            .additionalInfo(foundUser.getAdditionalInfo())
                            .gender(foundUser.getGender())
                            .age(foundUser.getAge())
                            .email(foundUser.getEmail())
                            .firstName(foundUser.getFirstName())
                            .lastName(foundUser.getLastName())
                            .bloodGroup(foundUser.getBloodGroup())
                            .build())
                    .build();

            // Construct and return the ApiResponse
            AuthResponse authResponse = AuthResponse.builder()
                    .responseCode(String.valueOf(HttpStatus.OK))
                    .responseMessage("Token successfully generated")
                    .body(responseBody)
                    .build();

            return new ApiResponse(HttpStatus.OK.toString(), authResponse.getResponseMessage(), authResponse.getBody(), HttpStatus.OK);
        } else {
            // Handle token generation failure
            return new ApiResponse(HttpStatus.UNAUTHORIZED.toString(), "Failed to generate token", null, HttpStatus.UNAUTHORIZED);
        }
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



}
