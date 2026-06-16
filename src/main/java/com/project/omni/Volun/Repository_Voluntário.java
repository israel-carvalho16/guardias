
package com.project.omni.Volun;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Repository_Voluntário extends JpaRepository<V, Long> {
    
}
