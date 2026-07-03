package com.project.omni.Blog.security;

import com.project.omni.Blog.security.jwt.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie; // Mantida a importação dos cookies
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

        String pathURI = request.getRequestURI();
        if (pathURI != null && (pathURI.startsWith("/api/admin") || pathURI.startsWith("/api/admin/"))) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = null;

        // CORREÇÃO 1: Tenta buscar primeiro no cabeçalho Header (Usado pelo JavaScript do Painel Admin)
        final String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7).trim();
        } 
        // CORREÇÃO 2: Se não achar no Header, busca nos Cookies (Usado pela navegação das páginas HTML)
        else if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("AUTH_TOKEN".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        // Se não encontrar o token em nenhum dos dois locais, deixa a requisição seguir
        if (token == null || token.trim().isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        // Verificação estrutural básica do JWT
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

        // CORREÇÃO 3: Removemos o /admin-dashboard daqui para permitir que o filtro leia o Header JWT do Painel
        return path.equals("/AdminForm") 
            || path.equals("/AdminForm/enviar") 
            || path.equals("/login.html")
            || path.equals("/register.html")
            || path.equals("/index.html")
            || path.equals("/");
    }
}
