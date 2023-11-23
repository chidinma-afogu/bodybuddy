package com.Klusterthon.Medbot.security;

import com.Klusterthon.Medbot.exception.CustomException;
import com.Klusterthon.Medbot.model.User;
import com.Klusterthon.Medbot.repository.UserRepository;
import com.Klusterthon.Medbot.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.autoconfigure.observation.ObservationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CustomTokenEnhancer implements TokenEnhancer {

//    private final UaaConfig config;
    private final UserRepository userRepository;
    private final AuthService authService;
//    private final UserService userService;
//    private final UserActivityService userActivityService;
//    private final UserRepo userRepo;

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        User user = userRepository.findByEmailIgnoreCase(authentication.getName()).orElseThrow(()->new CustomException("No user found", HttpStatus.BAD_REQUEST));
//        CompletableFuture.runAsync(() -> userActivityService.addUserActivity(new UserActivity(user,UserActivities.LOGIN, UserActivities.LOGIN.name())));
//        if (user.getFailedAttempt() > 0) userService.resetFailedAttempts(user);

        final Map<String, Object> additionalInfo = new HashMap<String, Object>();
        List<GrantedAuthority> list = new ArrayList<>();
        user.getRoles().stream().map(role -> role.getRoleName().equals("ROLE_USER")).collect(Collectors.toList());
//            TokenAuthorityPayload payload = new TokenAuthorityPayload();
//            payload.setPrivileges(privileges);
            user.setRole(user.getRole());
//            payload.setRoleName(role.getRoleName());
//            payload.setCompanyName(role.getCompany().getCompanyName());
//            payload.setCompanyCode(role.getCompany().getCompanyCode());
//            payload.setCompanyId(role.getCompany().getId());
//            payload.setSamlAuth(role.getCompany().getPasswordPolicy().getSamlAuth());
//            list.add(payload);
//        });

//        if (Objects.nonNull(user.getPasswordExpiryDate())) {
//            if (LocalDateTime.now().isAfter(user.getPasswordExpiryDate())) {
//                user.setEnforcePasswordReset(true);// password expired display reset password screen.
//                userRepo.save(user);
//            }
//        }

        additionalInfo.put("rights", list);
//        additionalInfo.put("iss", config.getOauth().getIssuer());
//        additionalInfo.put("full_name", user.getFullName());
        additionalInfo.put("first_name", user.getFirstName());
        //To Do add job title to profile data
        additionalInfo.put("last_name", user.getLastName());
        additionalInfo.put("id", user.getId());
//        additionalInfo.put("company_id", user.getCompany().getId());
//        additionalInfo.put("company_code", user.getCompany().getCompanyCode());
        additionalInfo.put("email", user.getEmail());

//        additionalInfo.put("phone_number", user.getPhoneNumber());
//        PasswordPolicy passwordPolicy = user.getCompany().getPasswordPolicy();
//        boolean reset = ( Objects.nonNull(passwordPolicy.getEnforcePasswordReset()) && passwordPolicy.getEnforcePasswordReset().booleanValue() ) || user.isEnforcePasswordReset() ? true : false;
//        additionalInfo.put("enforce_password_reset", reset);
//        additionalInfo.put("mfa_enabled", (Objects.nonNull(passwordPolicy.getMfa()) && passwordPolicy.getMfa().getMfaStatus() ? true : false) );
//        additionalInfo.put("saml_auth", user.getCompany().getPasswordPolicy().getSamlAuth());
        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
        return accessToken;
    }
}
