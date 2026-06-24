package com.project.omni.contatos;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class Controler {

    private final Repository_feed repositoryFeed;

    Controler(Repository_feed repositoryFeed) {
        this.repositoryFeed = repositoryFeed;
    }

    @GetMapping("/contato")
    public String abrirPagina() {
        return "contatos"; 
    }

    @PostMapping("/contato/enviar")
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

        return "redirect:/contato"; 
    }
}