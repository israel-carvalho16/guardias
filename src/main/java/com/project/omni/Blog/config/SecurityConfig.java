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
                .requestMatchers("/css/**", "/js/**", "/img/**", "/font/**", "/video/**", "/uploads/**", "/Núcleo - Juazeiro do Norte-CE/Saúde em ação/**", "/Núcleo - Uberlândia-MG/Evento - Dia da água (22-03) - OK/**", "/Núcleo - Uberlândia-MG/Evento - Mostra Extencionista da biologia (26-04)").permitAll()

                // 2. Apenas rotas lógicas limpas na lista de requisições permitidas
                .requestMatchers("/", "/login", "/register", "/pagina1", "/AdminForm", "/admin-dashboard",
                                 "/noticias", "/projeto", "/evento", "/mg", "/ce", "/contatos", "/orgaoambiental","/noticiaAberta").permitAll()
                
                // 3. Liberação total para as visualizações de páginas da pasta /admin
                .requestMatchers("/admin/**").permitAll() 
                
                // Se o seu AuthController também perdeu o prefixo /api, use "/auth/**"
                .requestMatchers("/api/auth/**", "/auth/**").permitAll() 
                
                // Libera os métodos GET públicos de posts e comentários (Visualização do Blog)
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/posts/**", "/comments/**").permitAll()
                
                // Bloqueia modificações (POST, PUT, DELETE) para exigir autenticação JWT válida
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/posts/**", "/comments/**").authenticated()
                .requestMatchers(org.springframework.http.HttpMethod.PUT, "/posts/**").authenticated()
                .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/posts/**", "/comments/**").authenticated()

                // Mantém regras legadas se houver outros controllers admin
                .requestMatchers("/api/admin/**").permitAll()
                
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }   
}
