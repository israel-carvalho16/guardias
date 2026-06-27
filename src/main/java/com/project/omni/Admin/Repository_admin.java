
package com.project.omni.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface Repository_admin extends JpaRepository<Repo, Long> {
    Optional<Repo> findByEmail(String email);
}