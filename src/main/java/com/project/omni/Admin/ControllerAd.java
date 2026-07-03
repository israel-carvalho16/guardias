package com.project.omni.Admin;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Optional;

@Controller
public class ControllerAd {

    private final Repository_admin repositoryAdmin;
    private final PasswordEncoder passwordEncoder;

    // Injetando o PasswordEncoder configurado no SecurityConfig
    ControllerAd(Repository_admin repositoryAdmin, PasswordEncoder passwordEncoder) {
        this.repositoryAdmin = repositoryAdmin;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/admin/login-api")
    @ResponseBody
    public ResponseEntity<?> validarLoginAdmin(@RequestBody Map<String, String> dados) {
        String email = dados.get("email") != null ? dados.get("email").trim() : "";
        String senha = dados.get("senha") != null ? dados.get("senha").trim() : "";

        Optional<Repo> adminOpt = repositoryAdmin.findByEmail(email);

        if (adminOpt.isPresent()) {
            Repo admin = adminOpt.get();
            
            // Valida a senha usando o algoritmo BCrypt
            if (passwordEncoder.matches(senha, admin.getSenha())) {
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
        // CRIPTOGRAFA A SENHA ANTES DE SALVAR
        novoAdmin.setSenha(passwordEncoder.encode(senha)); 
        repositoryAdmin.save(novoAdmin);
        return "redirect:/admin-dashboard"; 
    }
}