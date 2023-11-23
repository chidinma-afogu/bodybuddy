package com.Klusterthon.Medbot.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRegistrationRequest implements Serializable{
    private String firstName;
    private String lastName;
    @NotNull(message = "please pass email")
    @NotBlank(message = "please pass email")
    private String email;
    @NotNull(message = "please pass password")
    @NotBlank(message = "please pass password")
    private String password;
}






