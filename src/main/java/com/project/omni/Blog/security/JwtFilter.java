package com.project.omni.Blog.security;

import com.project.omni.Blog.security.jwt.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie; // IMPORTANTE: Importação do Cookie adicionada
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

        // Se a requisição for para as APIs gerenciais, ignora o filtro e passa direto
        String pathURI = request.getRequestURI();
        if (pathURI != null && (pathURI.startsWith("/api/admin") || pathURI.startsWith("/api/admin/"))) {
            filterChain.doFilter(request, response);
            return;
        }

        // CORREÇÃO: Nova lógica para buscar o token JWT diretamente nos Cookies do Navegador
        String token = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("AUTH_TOKEN".equals(cookie.getName())) { // Nome do cookie definido no login
                    token = cookie.getValue();
                    break;
                }
            }
        }

        // Se o Cookie não existir ou estiver vazio, deixa a requisição prosseguir.
        // O Spring Security aplicará o 403 depois apenas se a rota acessada for privada.
        if (token == null || token.trim().isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        // Verificação estrutural básica do JWT obtido no cookie
        if (token.chars().filter(ch -> ch == '.').count() != 2) {
            filterChain.doFilter(request, response);
            return;
        }

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
