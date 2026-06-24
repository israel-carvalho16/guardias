package com.project.omni.Admin;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

// COMENTE ESTA LINHA PARA DESATIVAR O CONFLITO:
// @Configuration("adminSecurityConfig")
public class SecurityConfigAdmin {

    // Você pode deixar o método aqui comentado ou apagá-lo depois
    // @Bean
    // public SecurityFilterChain adminFilterChain(HttpSecurity http) throws Exception {
    //     return http.build();
    // }
}