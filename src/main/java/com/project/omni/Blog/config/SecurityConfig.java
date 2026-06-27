package com.project.omni.Blog.config;

import com.project.omni.Blog.security.JwtFilter; 
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
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

    // Desliga o Spring Security para os arquivos físicos iniciais de carregamento do Tomcat
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/index.html", "/index", "/favicon.ico");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // 1. Recursos estáticos livres de mídia e estilização
                .requestMatchers("/css/**", "/js/**", "/img/**", "/Font/**", "/video/**", "/uploads/**").permitAll()

                
                // 2. Apenas rotas lógicas limpas na lista de requisições permitidas
                .requestMatchers("/", "/login", "/register", "/pagina1", "/AdminForm", "/admin-dashboard",
                                 "/noticias", "/projeto", "/evento", "/mg", "/ce", "/contatos", "/orgaoambiental","/noticiaAberta").permitAll()
                
                // 3. Endpoints públicos da API (CORRIGIDO: Incluídas as rotas de postagens)
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/posts/**").permitAll() // <--- LIBERAÇÃO DA API DE POSTS PARA SUMIR O ERRO 403
                
                // Qualquer outro recurso restrito exigirá autenticação por Token
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
