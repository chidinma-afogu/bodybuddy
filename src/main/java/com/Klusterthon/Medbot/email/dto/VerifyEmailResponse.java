package com.Klusterthon.Medbot.email.dto;

import com.Klusterthon.Medbot.model.enums.EmailStatus;
import com.Klusterthon.Medbot.model.enums.RecordStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VerifyEmailResponse {
        private Long id;
        private String email;
        private EmailStatus status;
        private Timestamp verifiedAt;
}
