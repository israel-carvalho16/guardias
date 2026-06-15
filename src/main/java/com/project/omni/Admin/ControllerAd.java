package com.project.omni.Admin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

public class ControllerAd {
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
        novoRepo.setSenha(senha);;
        

        return "redirect:/AdminForm"; 
    }
}
