package com.Klusterthon.Medbot.repository;

import com.Klusterthon.Medbot.model.User;
import com.Klusterthon.Medbot.model.enums.RecordStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    User findByEmailAndStatus(String email, RecordStatus recordStatus);
    Boolean existsByEmailAndStatus(String email,RecordStatus recordStatus);
    Boolean existsByEmail(String email);

    Optional<User> findByEmailIgnoreCaseAndStatus(String email,RecordStatus recordStatus);
    Optional<User> findByIdAndStatus(Long id, RecordStatus recordStatus);

//    String findByEmailIgnoreCase(String email);
}
