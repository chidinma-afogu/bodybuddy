package com.Klusterthon.Medbot.email.entity;

import com.Klusterthon.Medbot.model.AuditModel;
import com.Klusterthon.Medbot.model.User;
import com.Klusterthon.Medbot.model.enums.EmailStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "email_entity")
public class Email extends AuditModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String token;
    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    private EmailStatus status;
    @ManyToOne
    private User user;
}
