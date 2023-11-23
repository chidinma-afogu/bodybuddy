package com.Klusterthon.Medbot.repository;

import com.Klusterthon.Medbot.email.entity.Email;
import com.Klusterthon.Medbot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    User findByEmail(String email);
    Boolean existsByEmail(String email);

    Optional<User> findByEmailIgnoreCase(String email);

//    String findByEmailIgnoreCase(String email);
}
