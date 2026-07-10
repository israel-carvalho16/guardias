package com.project.omni.Volun;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface Repository_Voluntário extends JpaRepository<V, Long> {
    
    // CORREÇÃO CRÍTICA: Atualizado de tb_voluntario para tb_inscrevaSe para refletir a alteração no banco Neon
    @Query(value = "SELECT * FROM tb_inscrevaSe ORDER BY id DESC", nativeQuery = true)
    List<V> findAllVoluntarios();
}