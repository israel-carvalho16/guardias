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

    @GetMapping("/VolunForm")
    public String abrirPagina() {
        return "VolunForm"; 
    }

    @PostMapping("/VolunForm/enviar")
    public String salvarDados(@RequestParam("nome") String nome,
                              @RequestParam("email") String email,
                              @RequestParam("links") String links
                              ) {
        
        V novoV = new V();
        novoV.setNome(nome);
        novoV.setEmail(email);
        novoV.setLinks(links);

        Repository_Voluntário.save(novoV);

        return "redirect:/VolunForm"; 
    }
}