
package com.project.omni.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import com.project.omni.model.Usuario;
import com.project.omni.repository.UsuarioRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Usuario usuario = repository.findByEmail(username);
                
        if (usuario == null) {
        	throw new UsernameNotFoundException("Usuário não encontrado");
        }
        return User.builder()
        		.username(usuario.getEmail())
        		.password(usuario.getSenha())
        		.roles("USER")
        		.build();
        		
    }
}