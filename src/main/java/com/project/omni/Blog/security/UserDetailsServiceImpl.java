package com.project.omni.Blog.security;

import com.project.omni.Blog.repository.UserRepository;
import com.project.omni.Admin.Repository_admin; // Importar o repositório de Admin
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final Repository_admin repositoryAdmin; // Injetar repo de Admin

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1. Tenta buscar como Usuário normal
        var userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isPresent()) {
            var user = userOpt.get();
            var authorities = user.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                    .toList();
            return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
        }

        // 2. Se não achar, tenta buscar como Admin
        var adminOpt = repositoryAdmin.findByEmail(email);
        if (adminOpt.isPresent()) {
            var admin = adminOpt.get();
            // Aqui forçamos a Role de ADMIN para quem está na tabela admin
            var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));
            return new org.springframework.security.core.userdetails.User(admin.getEmail(), admin.getSenha(), authorities);
        }

        throw new UsernameNotFoundException("Usuário não encontrado: " + email);
    }
}