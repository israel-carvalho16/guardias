package com.project.omni.Blog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.project.omni.Admin.Repo;

@Controller
public class PageController {

    @GetMapping("/")
    public String home(){
        return "pagina1"; 
    }

    @GetMapping("/pagina1")
    public String pagina1(){
        return "pagina1";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // 1. Rota pública oficial que abre com sucesso o formulário de cadastro (AdminForm.html)
    @GetMapping("/register")
    public String register() {
        return "AdminForm"; 
    }

    // 2. CORRIGIDO: Rota alterada para receber o clique sob o caminho público do JWT (/api/auth/cadastrar)
    

    // 3. Rota mapeada para renderizar o segundo HTML (o painel de controle admin.html)
    @GetMapping("/admin")
    public String exibirPainelAdmin() {
        return "admin"; 
    }

    @GetMapping("/post")
    public String post() {
        return "post";
    }

    @GetMapping("/noticias")
    public String noticias() {
        return "noticias"; 
    }

    @GetMapping("/projeto")
    public String projeto() {
        return "Projeto"; 
    }

    @GetMapping("/evento")
    public String evento() {
        return "Evento"; 
    }

    @GetMapping("/contatos")
    public String contatos() {
        return "contatos"; 
    }
    
}
