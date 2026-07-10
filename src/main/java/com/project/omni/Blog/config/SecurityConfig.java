package com.project.omni.Blog.config;

import com.project.omni.Blog.security.JwtFilter; 
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        
        // 1. REMOVA o bloco antigo de exceptionHandling que redirecionava para /error/403

        .authorizeHttpRequests(auth -> auth
            // 2. ADICIONE as rotas nativas de erro aqui no início dos matches permitidos
            .requestMatchers("/error", "/error/**").permitAll()
            
            .requestMatchers("/css/**", "/js/**", "/img/**", "/uploads/**", "/CE_/**","/UBER/**").permitAll()
            .requestMatchers("/403/**").permitAll() 
            
            .requestMatchers(
                "/", "/index**", "/index.html",
                "/carregamento**", "/carregamento.html",
                "/contatos**", "/Contatos**",
                "/evento**", "/Evento**",
                "/mg**", "/MG**",
                "/ce**", "/CE**",
                "/noticias**", "/Noticias**",
                "/noticiaAberta**", "/NoticiaAberta**",
                "/orgaoambiental**", "/Orgaoambiental**",
                "/post**", "/Post**",
                "/projeto**", "/Projeto**",
                "/volunform**", "/VolunForm**", "/Volunform**",
                "/login**", "/Login**",
                "/register**", "/Register**",
                "/protagonismo-feminino**", "/Protagonismo-feminino**"
            ).permitAll()
            
            .requestMatchers("/AdminForm", "/admin-dashboard", "/admin/novo-admin").permitAll()
            .requestMatchers("/api/auth/**", "/auth/**").permitAll()
            .requestMatchers("/api/admin/**").hasRole("ADMIN")
            .requestMatchers(org.springframework.http.HttpMethod.GET, "/posts/**", "/comments/**").permitAll()
            
            .requestMatchers(org.springframework.http.HttpMethod.POST, "/posts/**", "/comments/**").authenticated()
            .requestMatchers(org.springframework.http.HttpMethod.PUT, "/posts/**").authenticated()
            .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/posts/**", "/comments/**").authenticated()
            
            .anyRequest().authenticated()
        )
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
}

}
