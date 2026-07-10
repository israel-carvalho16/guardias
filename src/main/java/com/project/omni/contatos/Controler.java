package com.project.omni.contatos;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class Controler {

    private final Repository_feed repositoryFeed;

    Controler(Repository_feed repositoryFeed) {
        this.repositoryFeed = repositoryFeed;
    }


    @PostMapping("/contatos")
    public String salvarDados(@RequestParam("nome") String nome,
                              @RequestParam("email") String email,
                              @RequestParam("mensagem") String mensagem,
                              @RequestParam("avaliacao") Integer avaliacao) {
        
        Feed novoFeed = new Feed();
        novoFeed.setNome(nome);
        novoFeed.setEmail(email);
        novoFeed.setMensagem(mensagem);
        novoFeed.setAvaliacao(avaliacao);

        repositoryFeed.save(novoFeed);

        // Continua redirecionando para /contatos, que o PageController vai renderizar com sucesso!
        return "redirect:/contatos?sucesso"; 
    }
}
