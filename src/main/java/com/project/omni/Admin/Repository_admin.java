package com.project.omni.Admin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface Repository_admin extends JpaRepository<Repo, Long> {
    // Busca o admin pelo e-mail
    Optional<Repo> findByEmail(String email);
    
    // Busca o admin pelo CPF
    Optional<Repo> findByCpf(String CPF);
}
