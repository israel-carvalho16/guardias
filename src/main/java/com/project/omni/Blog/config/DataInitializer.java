package com.project.omni.Blog.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.project.omni.Blog.model.Role;
import com.project.omni.Blog.model.Role.RoleName;
import com.project.omni.Blog.repository.RoleRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        if (roleRepository.findByName(RoleName.ROLE_USER).isEmpty()) {
            Role userRole = new Role();
            userRole.setName(RoleName.ROLE_USER);
            roleRepository.save(userRole);
        }

        if (roleRepository.findByName(RoleName.ROLE_ADMIN).isEmpty()) {
            Role adminRole = new Role();
            adminRole.setName(RoleName.ROLE_ADMIN);
            roleRepository.save(adminRole);
        }
    }
}
