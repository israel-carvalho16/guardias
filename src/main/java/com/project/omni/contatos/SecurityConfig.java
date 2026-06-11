package com.project.omni.contatos;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration("contatosSecurityConfig")
public class SecurityConfig {

    @Bean
public SecurityFilterChain contatosFilterChain(HttpSecurity http) throws Exception {
    // mude o nome para contatosFilterChain
    return http.build();
}
}