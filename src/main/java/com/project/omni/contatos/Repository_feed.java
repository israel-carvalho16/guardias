
package com.project.omni.contatos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Repository_feed extends JpaRepository<Feed, Long> {
    
}
