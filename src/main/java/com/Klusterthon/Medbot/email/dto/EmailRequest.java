package com.Klusterthon.Medbot.email.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmailRequest {
    private String userEmail;
    private String subject;
    private String text;
}
