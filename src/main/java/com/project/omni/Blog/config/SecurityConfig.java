package com.project.omni.Blog.config;

import com.project.omni.Blog.security.JwtFilter; 
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter; 

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // 1. Recursos estáticos físicos (CSS, JS, Imagens, Ícones)
                        .requestMatchers(
                                "/css/**",
                                "/js/**",
                                "/img/**",
                                "/favicon.ico"
                        ).permitAll()

                        // 2. URLs de páginas públicas (SEM .html)
                        .requestMatchers(
                                "/",
                                "/pagina1",
                                "/login",
                                "/register",
                                "/admin",
                                "/post",
                                "/noticias",
                                "/projeto",
                                "/evento",
                                "/error",
                                "/contatos",
                                "/AdminForm", 
                                "/AdminForm/enviar"
                        ).permitAll()

                        // 3. Regras de API controladas pelo Token JWT
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/posts/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/posts/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/posts/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/posts/**").hasRole("ADMIN")
                        .requestMatchers("/api/comments/**").hasAnyRole("USER", "ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
