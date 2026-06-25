package com.project.omni.Admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;

@Controller  // ← ADICIONAR
public class ControllerAd {

    @Autowired  // ← ADICIONAR
    private Repository_admin repositoryAdmin;

    @GetMapping("/AdminForm")
    public String abrirPagina() {
        return "AdminForm";
    }

    @PostMapping("/AdminForm/enviar")
    public String salvarDados(@RequestParam("nome") String nome,
                              @RequestParam("email") String email,
                              @RequestParam("CPF") int CPF,
                              @RequestParam("senha") String senha) {

        Repo novoRepo = new Repo();
        novoRepo.setNome(nome);
        novoRepo.setEmail(email);
        novoRepo.setCPF(CPF);
        novoRepo.setSenha(senha);

        repositoryAdmin.save(novoRepo);  // ← ADICIONAR esta linha

        return "redirect:/AdminForm";
    }
}