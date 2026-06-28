package com.project.omni.Blog.security;

import com.project.omni.Blog.repository.UserRepository;
import com.project.omni.Admin.Repository_admin;
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
    private final Repository_admin repositoryAdmin;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Log para acompanhar no terminal do VS Code
        System.out.println("=== DIRETRIZ DE LOGIN CRÍTICA ===");
        System.out.println("Tentando autenticar o e-mail: " + email);

        // 1. TENTATIVA PRIORITÁRIA: Busca na tabela de Administradores (admin)
        var adminOpt = repositoryAdmin.findByEmail(email);
        if (adminOpt.isPresent()) {
            var admin = adminOpt.get();
            System.out.println("Administrador localizado no banco Neon! ID: " + admin.getId());
            System.out.println("Senha cadastrada no banco: " + admin.getSenha());
            
            var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));
            String senhaTratada = "{noop}" + admin.getSenha();
            
            return new org.springframework.security.core.userdetails.User(
                    admin.getEmail(), 
                    senhaTratada, 
                    authorities
            );
        }

        // 2. SEGUNDA TENTATIVA: Se não for admin, busca como Usuário normal do Blog
        System.out.println("E-mail não é administrador. Buscando na tabela de usuários do blog...");
        var userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            var user = userOpt.get();
            var authorities = user.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                    .toList();
            return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
        }

        System.out.println("ERRO: Ninguém foi encontrado com o e-mail: " + email);
        throw new UsernameNotFoundException("Usuário não encontrado: " + email);
    }
}
