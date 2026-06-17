package com.project.omni.Admin;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration("adminSecurityConfig")
public class SecurityConfigAdmin {

@Bean
public SecurityFilterChain adminFilterChain(HttpSecurity http) throws Exception {
    
    return http.build();
}
}