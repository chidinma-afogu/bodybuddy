package com.Klusterthon.Medbot.dto.response;

import com.Klusterthon.Medbot.model.enums.RecordStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseBody implements Serializable {
    private Long id;
    private String email;
    private RecordStatus status;
    private String createdBy;
    private Timestamp createdAt;
}
