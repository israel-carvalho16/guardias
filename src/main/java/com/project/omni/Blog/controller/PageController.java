package com.project.omni.Blog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    // Rota Raiz - Abre a página inicial
    @GetMapping("/")
    public String home() {
        return "index"; 
    }

    // Rota Alternativa para a Página Inicial
    @GetMapping("/index")
    public String pagina1() {
        return "index";
    }

    // Rota do Formulário de Login (Abre o arquivo VolunForm.html)
    @GetMapping("/login")
    public String login() {
        return "VolunForm"; 
    }

    // Rota do Formulário de Cadastro (Abre o arquivo AdminForm.html)
    @GetMapping("/register")
    public String register() {
        return "AdminForm"; 
    }

    // Rota do Painel de Controle (Abre o arquivo admin.html)
    @GetMapping("/admin")
    public String exibirPainelAdmin() {
        return "admin"; 
    }
    // Rota de Órgãos Ambientais (Abre o arquivo orgaoambiental.html)
    @GetMapping("/post")
    public String post() {
        return "orgaoambiental"; 
    }

    // Rota do Hub de Notícias
    @GetMapping("/noticias")
    public String noticias() {
        return "noticias"; 
    }

    // Rota Institucional do Projeto
    @GetMapping("/projeto")
    public String projeto() {
        return "Projeto"; 
    }

    // Rota do Núcleo Regional de Minas Gerais
    @GetMapping("/mg")
    public String nucleoMG() {
        return "MG"; 
    }

    // Rota do Núcleo Regional do Ceará
    @GetMapping("/ce")
    public String nucleoCE() {
        return "CE"; 
    }

    // Rota de Eventos
    @GetMapping("/evento")
    public String evento() {
        return "Evento"; 
    }

    // Rota da Página de Contatos
    @GetMapping("/contatos")
    public String contatos() {
        return "contatos"; 
    }
   @GetMapping("/noticiaAberta")
    public String exibirNoticiaAberta() {
    return "noticiaAberta"; // Deve retornar o nome exato do seu arquivo html
}

}