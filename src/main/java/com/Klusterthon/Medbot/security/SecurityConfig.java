package com.Klusterthon.Medbot.security;

import com.Klusterthon.Medbot.model.User;
import com.Klusterthon.Medbot.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

@Configuration(proxyBeanMethods = true)
@EnableMethodSecurity
@EnableWebSecurity
@Slf4j
public class SecurityConfig implements AuthorizationServerConfigurer {
    private CustomUserDetailsService customUserDetailsService;
    private JwtAuthenticationEntryPoint authenticationEntrypoint;
    private JwtAuthenticationFilter authenticationFilter;
    private UserDetailsService userDetailsService;
    private final LogoutHandler logoutHandler;
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private UserRepository userRepository;

   @Autowired
    public SecurityConfig(CustomUserDetailsService customUserDetailsService,UserRepository userRepository,HttpServletResponse response,HttpServletRequest request,JwtAuthenticationEntryPoint authenticationEntrypoint, JwtAuthenticationFilter authenticationFilter, UserDetailsService userDetailsService, LogoutHandler logoutHandler) throws Exception {
        this.customUserDetailsService = customUserDetailsService;
        this.authenticationEntrypoint = authenticationEntrypoint;
        this.authenticationFilter = authenticationFilter;
        this.userDetailsService = userDetailsService;
//        this.authenticationManager = authenticationConfiguration.getAuthenticationManager();
        this.logoutHandler = logoutHandler;
        this.request = request;
        this.response = response;
        this.userRepository=userRepository;
    }
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

   @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

   @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri;

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE + 1)
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())

                .exceptionHandling(e -> e
                        .authenticationEntryPoint(authenticationEntrypoint)
                )

                .sessionManagement((sessionManagement) ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )


                .authorizeHttpRequests(authorize -> {
                    authorize
                            .requestMatchers("api/v1/auth/signup").permitAll()
                            .requestMatchers("api/v1/auth/login").permitAll()
                            .requestMatchers("/favicon.ico").permitAll()
                            .requestMatchers("/oauth/token/**").permitAll()
                            .requestMatchers("/oauth/authorize/**").permitAll()
                            .requestMatchers("api/v1/email/verify/**").permitAll()
                            .anyRequest().permitAll();
                })

//                .authenticationProvider(authenticationProvider())
                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
//                .oauth2Login(Customizer.withDefaults())
//                .formLogin(Customizer.withDefaults())

                .logout((logout) ->
                        logout.deleteCookies("remove")
                                .invalidateHttpSession(false)
                                .logoutUrl("/api/v1/auth/logout")
//                                .addLogoutHandler(logoutHandler)
                                .logoutSuccessHandler((request, response, authentication) ->
                                        SecurityContextHolder.clearContext())
                )

                .httpBasic(Customizer.withDefaults());


        return httpSecurity.build();
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        oauthServer.checkTokenAccess("isAuthenticated()");

    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient(clientId)
                .secret(passwordEncoder().encode(clientSecret))
                .authorizedGrantTypes("refresh_token", "authorization_code", "password", "client_credentials")
                .scopes("read", "write")
                .autoApprove(true)
                .redirectUris(redirectUri);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoint) throws Exception {
        AuthenticationConfiguration authenticationConfiguration = new AuthenticationConfiguration();
        endpoint
                .pathMapping("/oauth/token", "/api/v1/authenticate")
//                .pathMapping("/oauth/check_token", "/api/v1/validate-token")
                .authenticationManager(authenticationConfiguration.getAuthenticationManager())
                .userDetailsService(userDetailsService)
                .tokenEnhancer(endpoint.getTokenEnhancer())
                .tokenStore(endpoint.getTokenStore());
//                .exceptionTranslator(webResponseExceptionTranslator());
    }

//    @Bean
//    public WebResponseExceptionTranslator webResponseExceptionTranslator() {
//        return new DefaultWebResponseExceptionTranslator() {
//            @Override
//            public ResponseEntity<OAuth2Exception> translate(Exception e) throws Exception {
//                OAuth2Exception oAuth2Exception = (OAuth2Exception) e;
//                log.info("oAuth2Exception : "+oAuth2Exception);
//                String message = oAuth2Exception.getMessage();
//                response.resetBuffer();
//                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                response.setHeader("Content-Type", "application/json");
//                Map<String, String> responseMap = new HashMap<>();
//
////                if (message.equalsIgnoreCase("Bad credentials")) {
////                    String email = request.getParameter("email");
////                    Optional<User> optionalUser = userRepository.findByEmailIgnoreCase(email);
////                    if (optionalUser.isPresent()) {
////                        User user = optionalUser.get();
//////                        PasswordPolicy passwordPolicy = user.getCompany().getPasswordPolicy();
////
//////                        if (user.getStatus().equalsIgnoreCase(UserStatuses.active.name()) && user.isAccountNonLocked()) {
//////                            if (Objects.nonNull(passwordPolicy.getMaxInvalidLoginAttempt())) {
//////                                if (user.getFailedAttempt() < passwordPolicy.getMaxInvalidLoginAttempt() - 1) {
//////                                    userService.increaseFailedAttempts(user);
//////                                } else {
//////                                    userService.lock(user);
//////                                    String msg = "Your account has been locked due to " +
//////                                            passwordPolicy.getMaxInvalidLoginAttempt() + " failed attempts."
//////                                            + " It will be unlocked after "
//////                                            + passwordPolicy.getLockTimeDurationInHours() + " hours";
//////                                    userActivityService.addUserActivity(UserActivity.builder()
//////                                            .user(user)
//////                                            .activityLog(msg)
//////                                            .activityType(UserActivities.failed_login.name())
//////                                            .build());
////                                    responseMap.put("error", "invalid_grant");
////                                    responseMap.put("error_description", msg);
////                                    new ObjectMapper().writeValue(response.getOutputStream(), responseMap);
//////                                }
////                            }
////                        } else if (!user.isAccountNonLocked()) {
////                            if (userService.unlockWhenTimeExpired(user)) {
////                                String msg = "Your account has been unlocked. Please try to login again";
//////                                userActivityService.addUserActivity(UserActivity.builder()
//////                                        .user(user)
//////                                        .activityLog(msg)
//////                                        .activityType(UserActivities.failed_login.name())
//////                                        .build());
////                                responseMap.put("error", "invalid_grant");
////                                responseMap.put("error_description", msg);
////                                new ObjectMapper().writeValue(response.getOutputStream(), responseMap);
////                            } else {
////                                responseMap.put("error", "invalid_grant");
////                                responseMap.put("error_description", "Your account has been locked due to " + passwordPolicy.getMaxInvalidLoginAttempt() + " failed attempts."
////                                        + " It will be unlocked after " + ChronoUnit.MINUTES.between(LocalDateTime.now(), user.getLockTime().plusHours(passwordPolicy.getLockTimeDurationInHours())) + " minutes.");
////                                new ObjectMapper().writeValue(response.getOutputStream(), responseMap);
////                            }
////                        }
////                    } else {
////                        responseMap.put("error", "invalid_grant");
////                        responseMap.put("error_description", "Bad credentials");
////                        new ObjectMapper().writeValue(response.getOutputStream(), responseMap);
////                    }
////                }
//                return new ResponseEntity<>(oAuth2Exception, HttpStatus.UNAUTHORIZED);
//            }
//        };
//    }
}
