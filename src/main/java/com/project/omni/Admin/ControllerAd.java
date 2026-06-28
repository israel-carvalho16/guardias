package com.project.omni.Admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Map;
import java.util.Optional;

@Controller
public class ControllerAd {

    @Autowired
    private Repository_admin repositoryAdmin;

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

    // ADAPTADO: Agora valida estritamente usando apenas E-mail e Senha!
    @PostMapping("/admin/login-api")
    public ResponseEntity<?> validarLoginAdmin(@RequestBody Map<String, String> dados) {
        String email = dados.get("email") != null ? dados.get("email").trim() : "";
        String senha = dados.get("senha") != null ? dados.get("senha").trim() : "";

        System.out.println("=== TENTATIVA DE LOGIN TRADICIONAL ===");
        System.out.println("E-mail inserido: " + email);

        Optional<Repo> adminOpt = repositoryAdmin.findByEmail(email);

        if (adminOpt.isPresent()) {
            Repo admin = adminOpt.get();
            
            // Valida apenas se a senha bate com a cadastrada no Neon
            if (admin.getSenha().trim().equals(senha)) {
                System.out.println("Login efetuado com sucesso!");
                return ResponseEntity.ok(Map.of("sucesso", true));
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
