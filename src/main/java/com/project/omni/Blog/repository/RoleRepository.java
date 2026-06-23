package com.project.omni.Blog.repository;

import com.project.omni.Blog.model.Role;
import com.project.omni.Blog.model.Role.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}