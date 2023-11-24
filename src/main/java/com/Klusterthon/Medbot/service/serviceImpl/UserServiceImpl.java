package com.Klusterthon.Medbot.service.serviceImpl;

import com.Klusterthon.Medbot.dto.request.UpdateUserRequest;
import com.Klusterthon.Medbot.dto.request.UserRegistrationRequest;
import com.Klusterthon.Medbot.dto.response.ApiResponse;
import com.Klusterthon.Medbot.dto.response.UpdateUserResponse;
import com.Klusterthon.Medbot.dto.response.UserResponse;
import com.Klusterthon.Medbot.dto.response.UserResponseBody;
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
import com.Klusterthon.Medbot.service.UserService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements UserService {
    private EmailService emailService;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private EmailRepository emailRepository;

    @Autowired
    public UserServiceImpl(EmailService emailService, UserRepository userRepository,
                           RoleRepository roleRepository, PasswordEncoder passwordEncoder,
                           EmailRepository emailRepository) {
        this.emailService = emailService;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailRepository = emailRepository;
    }

    @Override
    public ApiResponse signup(UserRegistrationRequest request) {
        if (userRepository.existsByEmailAndStatus(request.getEmail(),RecordStatus.ACTIVE)) {
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

    @Override
    public ApiResponse forgotPassword(String email) {
        User user = userRepository.findByEmailIgnoreCaseAndStatus(email, RecordStatus.ACTIVE).get();
        if (user.getEmail() == null) {
            throw new CustomException("Email does not exist", HttpStatus.OK);
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

    @Override
    public ApiResponse updateUser(UpdateUserRequest request) {
        User user = getUser(request.getId());
        user.setFirstName(getFirstName(request.getFirstName()));
        user.setLastName(getLastName(request.getLastName()));
        user.setBloodGroup(request.getBloodGroup());
        user.setGender(getGender(request.getGender()));
        user.setAge(getAge(request.getAge()));
        user.setAdditionalInfo(request.getAdditionalInfo());
        userRepository.save(user);
        return ApiResponse.builder()
                .code(HttpStatus.OK.toString())
                .message("User updated successfully")
                .data(convertToUpdateUserResponse(user))
                .build();
    }
    private String getFirstName(String firstname){
        if(firstname==null || firstname.isEmpty()){
            throw new CustomException("Please pass first name",HttpStatus.BAD_REQUEST);
        }
        return firstname;
    }private String getLastName(String lastName){
        if(lastName==null || lastName.isEmpty()){
            throw new CustomException("Please pass last name",HttpStatus.BAD_REQUEST);
        }
        return lastName;
    }private String getGender(String gender){
        if(gender==null || gender.isEmpty()){
            throw new CustomException("Please pass gender",HttpStatus.BAD_REQUEST);
        }
        return gender;
    }private int getAge(Integer age){
        if(age==null || age<1){
            throw new CustomException("Please pass age. Age must not be less than 1",HttpStatus.BAD_REQUEST);
        }
        return age;
    }private String getAddInfo(String addInfo){
        if(addInfo==null || addInfo.isEmpty()){
            throw new CustomException("Please pass additional Info",HttpStatus.BAD_REQUEST);
        }
        return addInfo;
    }

    @Override
    public ApiResponse deleteUser(Long id) {
        User user = getUser(id);
        user.setStatus(RecordStatus.DELETED);
        Email email = emailRepository.findByUser(user);
        email.setStatus(EmailStatus.NOT_VERIFIED);
        emailRepository.save(email);
        userRepository.save(user);
        return ApiResponse.builder()
                .code(HttpStatus.OK.toString())
                .message("User deleted successfully")
                .data(null)
                .build();
    }

    @Override
    public ApiResponse getUserById(Long id) {
        User user = getUser(id);
        return ApiResponse.builder()
                .code(HttpStatus.OK.toString())
                .message("User retrieved successfully")
                .data(convertToUpdateUserResponse(user))
                .build();
    }

    private User getUser(Long id) {
        return userRepository.findByIdAndStatus(id, RecordStatus.ACTIVE)
                .orElseThrow(() -> new CustomException("User with id: " + id + " not found", HttpStatus.BAD_REQUEST));
    }

    private UpdateUserResponse convertToUpdateUserResponse(User user) {
        return UpdateUserResponse.builder()
                .id(user.getId())
                .age(user.getAge())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .status(user.getStatus())
                .additionalInfo(user.getAdditionalInfo())
                .role(user.getRole())
                .gender(user.getGender())
                .email(user.getEmail())
                .bloodGroup(user.getBloodGroup())
                .build();
    }
    private boolean isInputValid(String input, String regex) {
        return Pattern.compile(regex)
                .matcher(input)
                .matches();
    }


}
