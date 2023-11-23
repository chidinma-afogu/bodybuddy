package com.Klusterthon.Medbot.service.serviceImpl;

import com.Klusterthon.Medbot.exception.CustomException;
import com.Klusterthon.Medbot.model.Role;
import com.Klusterthon.Medbot.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
        private final RoleRepository roleRepository;

        @Autowired
        public DataInitializer(RoleRepository roleRepository) {
            this.roleRepository = roleRepository;
        }

        @Override
        public void run(String... args) {
            createRoleIfNotFound("ROLE_USER");
            createRoleIfNotFound("ROLE_ADMIN");
        }

    private void createRoleIfNotFound(String roleName) {
        Role role = roleRepository.findByRoleName(roleName);
        if (role == null) {
            role = new Role(roleName);
            roleRepository.save(role);
        }
    }

}
