package com.project.omni.Admin;

import com.project.omni.Blog.security.jwt.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class ControllerAd {

    private final Repository_admin repositoryAdmin;
    private final JwtService jwtService;

    // Construtor corrigido: Removemos a dependência do userDetailsService que causava o bloqueio por Authorities nulas
    ControllerAd(Repository_admin repositoryAdmin, JwtService jwtService) {
        this.repositoryAdmin = repositoryAdmin;
        this.jwtService = jwtService;
    }

    @GetMapping("/AdminForm")
    public String abrirPagina() {
        return "AdminForm"; 
    }

    @GetMapping("/admin-dashboard")
    public String exibirPainel() {
        return "admin-dashboard"; 
    }

    @GetMapping("/admin/novo-admin")
    public String abrirPaginaCadastro() {
        return "AdminCadastro"; 
    }

    @PostMapping("/admin/login-api")
    @ResponseBody // Garante o retorno correto do JSON estruturado para o front-end
    public ResponseEntity<?> validarLoginAdmin(@RequestBody Map<String, String> dados) {
        String email = dados.get("email") != null ? dados.get("email").trim() : "";
        String senha = dados.get("senha") != null ? dados.get("senha").trim() : "";

        System.out.println("=== TENTATIVA DE LOGIN TRADICIONAL ===");
        System.out.println("E-mail inserido: " + email);

        // ESCAPE SUPREMO DE TESTES: Se for o admin mestre local, valida imediatamente com super permissões
        if ("admin@omni.com".equals(email) && "123456".equals(senha)) {
            UserDetails userDetails = User.builder()
                    .username(email)
                    .password(senha)
                    .authorities(List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ADMIN")))
                    .build();
            
            String token = jwtService.generateToken(userDetails);
            return ResponseEntity.ok(Map.of("sucesso", true, "token", token));
        }

        // Fluxo normal consultando a tabela do banco de dados admin (Texto Limpo)
        Optional<Repo> adminOpt = repositoryAdmin.findByEmail(email);

        if (adminOpt.isPresent()) {
            Repo admin = adminOpt.get();
            
            // Valida apenas se a senha bate com a cadastrada no Neon (Suporta texto limpo conforme seu banco)
            if (admin.getSenha().trim().equals(senha)) {
                System.out.println("Login efetuado com sucesso!");

                // CORREÇÃO VISUAL E DE SEGURANÇA: Cria o UserDetails inserindo explicitamente as permissões de administrador
                // Isso impede o erro 403 Forbidden nos botões de Editar e Excluir
                UserDetails userDetails = User.builder()
                        .username(admin.getEmail())
                        .password(admin.getSenha())
                        .authorities(List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ADMIN")))
                        .build();
                
                String token = jwtService.generateToken(userDetails);

                return ResponseEntity.ok(Map.of("sucesso", true, "token", token));
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("erro", "Dados incorretos!"));
    }

    @PostMapping("/admin/salvar-novo")
    public String salvarNovoAdmin(@RequestParam String nome, 
                                  @RequestParam String email, 
                                  @RequestParam String cpf, 
                                  @RequestParam String senha) {
        Repo novoAdmin = new Repo();
        novoAdmin.setNome(nome);
        novoAdmin.setEmail(email);
        novoAdmin.setCpf(cpf);
        novoAdmin.setSenha(senha); 
        repositoryAdmin.save(novoAdmin);
        return "redirect:/admin-dashboard"; 
    }
}
