
package com.project.omni.Admin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Repository_admin extends JpaRepository<Repo, Long> {
    
}
