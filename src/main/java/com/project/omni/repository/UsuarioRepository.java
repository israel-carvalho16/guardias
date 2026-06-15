package com.project.omni.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.omni.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Usuario findByEmail(String email);
}
