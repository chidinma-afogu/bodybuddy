package com.Klusterthon.Medbot.dto.response;

import com.Klusterthon.Medbot.model.enums.RecordStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String gender;
    private String bloodGroup;
    private int age;
    private String additionalInfo;
    private String role;
    private String email;
    private String password;
    private RecordStatus status;
}
