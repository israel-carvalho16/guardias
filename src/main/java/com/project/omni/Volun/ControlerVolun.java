package com.project.omni.Volun;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ControlerVolun {

    private final Repository_Voluntário Repository_Voluntário;

    ControlerVolun(Repository_Voluntário Repository_Voluntário) {
        this.Repository_Voluntário = Repository_Voluntário;
    }

    // 1. REMOVIDO O @GETMAPPING DAQUI: Ele foi centralizado na rota pública /login do PageController

    // 2. CORRIGIDO: Rota POST alterada para o prefixo público do JWT (/api/auth/volun-form)
    @PostMapping("/api/auth/volun-form")
    public String salvarDados(@RequestParam("name") String nome,
                              @RequestParam("email") String email,
                              @RequestParam("cpf") String cpf,
                              @RequestParam("phone") String phone,
                              @RequestParam("gender") String gender,
                              @RequestParam("password") String password) {
        
        V novoV = new V();
        novoV.setNome(nome);
        novoV.setEmail(email);
        // O banco do grupo usa o campo de links temporário:
        novoV.setLinks("CPF: " + cpf + " | Tel: " + phone + " | Gênero: " + gender);

        Repository_Voluntário.save(novoV);

        // Após salvar, redireciona o usuário com segurança de volta para a Home pública
        return "redirect:/pagina1"; 
    }
}
