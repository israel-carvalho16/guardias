package com.project.omni.Admin;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration("adminSecurityConfig")
@Order(1) 
public class SecurityConfigAdmin {

    @Bean
    public SecurityFilterChain adminFilterChain(HttpSecurity http) throws Exception {
        http
            // CORREÇÃO DEFINITIVA: Liberamos qualquer rota que comece com /admin e a tela de login
            .securityMatcher("/AdminForm", "/admin/**")
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll()) 
            .csrf(csrf -> csrf.disable()); // Mantém desativado para o formulário manual passar direto

        return http.build();
    }
}
