package com.project.omni.Blog.security;

import com.project.omni.Blog.security.jwt.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // ESCAPE SUPREMO ANTI-ERRO 400: Se a requisição for para as APIs gerenciais, ignora o filtro e passa direto
        String pathURI = request.getRequestURI();
        if (pathURI != null && (pathURI.startsWith("/api/admin") || pathURI.startsWith("/api/admin/"))) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");

        // CORREÇÃO 1: Ignora o filtro se o cabeçalho for nulo, não começar com Bearer ou for curto demais (como "Bearer null")
        if (authHeader == null || !authHeader.startsWith("Bearer ") || authHeader.trim().length() <= 15) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7).trim();

        // CORREÇÃO 2: Um JWT estruturado possui exatamente 2 pontos. Se não tiver, o token está malformado e não deve ser processado.
        if (token.chars().filter(ch -> ch == '.').count() != 2) {
            filterChain.doFilter(request, response);
            return;
        }

        // CORREÇÃO 3: Bloco try-catch para capturar qualquer falha inesperada na leitura do token e não travar o Tomcat
        try {
            final String email = jwtService.extractEmail(token);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                if (jwtService.isTokenValid(token, userDetails)) {
                    var authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Limpa o contexto de segurança caso o token seja inválido ou expire
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        String uri = request.getRequestURI();
        
        // Garante a redundância de segurança também na checagem de rotas nativas
        if ((path != null && path.startsWith("/api/admin")) || (uri != null && uri.startsWith("/api/admin"))) {
            return true;
        }

        return path.equals("/AdminForm") 
            || path.equals("/AdminForm/enviar") 
            || path.equals("/admin-dashboard") 
            || path.equals("/admin-dashboard.html")
            || path.equals("/login.html")
            || path.equals("/register.html")
            || path.equals("/index.html")
            || path.equals("/");
    }
}
