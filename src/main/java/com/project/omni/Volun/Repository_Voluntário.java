package com.project.omni.Volun;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface Repository_Voluntário extends JpaRepository<V, Long> {
    
    // CORREÇÃO CRÍTICA: Força uma consulta SQL pura na tabela, contornando o erro de acentuação do JPA
    @Query(value = "SELECT * FROM tb_voluntario ORDER BY id DESC", nativeQuery = true)
    List<V> findAllVoluntarios();
}
