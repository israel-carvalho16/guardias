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
            .authorizeHttpRequests(auth -> auth
                // 1. Libera recursos estáticos (CSS, JS, Imagens)
                .requestMatchers("/css/**", "/js/**", "/img/**", "/uploads/**", "/CE_/**","/UBER/**").permitAll()
                
                // 2. Libera todas as variações das suas páginas públicas (Case-Insensitive e extensões)
                .requestMatchers(
                    "/", "/index**", "/index.html",
                    "/pagina1**", "/pagina1.html",
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
                    "/protagonismo-feminino**"
                ).permitAll()
                
                // 3. Mantém rotas lógicas limpas que vieram do repositório remoto
                .requestMatchers("/AdminForm", "/admin-dashboard").permitAll()
                .requestMatchers("/admin/**").permitAll() 
                .requestMatchers("/api/auth/**", "/auth/**", "/admin/login-api").permitAll() 
                .requestMatchers("/api/admin/**").permitAll()
                
                // 4. Libera visualização pública de posts e comentários do Blog (Método GET)
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/posts/**", "/comments/**").permitAll()
                
                // 5. Bloqueia alterações (POST, PUT, DELETE) exigindo autenticação JWT
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/posts/**", "/comments/**").authenticated()
                .requestMatchers(org.springframework.http.HttpMethod.PUT, "/posts/**").authenticated()
                .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/posts/**", "/comments/**").authenticated()
                
                // Qualquer outra rota residual exigirá autenticação
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    } 
}
