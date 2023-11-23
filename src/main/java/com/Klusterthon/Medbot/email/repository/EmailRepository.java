package com.Klusterthon.Medbot.email.repository;

import com.Klusterthon.Medbot.email.entity.Email;
import com.Klusterthon.Medbot.model.User;
import com.Klusterthon.Medbot.model.enums.EmailStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailRepository extends JpaRepository<Email,Long> {
    Optional<Email> findByTokenAndStatus(String token, EmailStatus emailStatus);

    Email findByUser(User user);
    Optional<Email> findByEmailIgnoreCase(String email);

}
