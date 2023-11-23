package com.Klusterthon.Medbot.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateUserRequest {
    @NotBlank(message = "please pass first name")
    @NotNull(message = "please pass first name")
    private String firstName;
    @NotBlank(message = "please pass last name")
    @NotNull(message = "please pass last name")
    private String lastName;
    @NotBlank(message = "please pass gender")
    @NotNull(message = "please pass gender")
    private String gender;
    @NotBlank(message = "please pass blood group")
    @NotNull(message = "please pass blood group")
    private String bloodGroup;
    @Min(value = 1,message = "age must be greater than 1")
    private int age;
    @NotBlank(message = "please pass additionalInfo")
    @NotNull(message = "please pass additionalInfo")
    private String additionalInfo;
    private Long id;
}
