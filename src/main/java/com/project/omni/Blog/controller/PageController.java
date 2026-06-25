package com.project.omni.Blog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    // Rota raiz (/) — Corrigido: Removido o .html
    @GetMapping("/")
    public String home(){
        return "pagina1"; 
    }

    // Rota secundária para o link "Início" do menu apontar para o mesmo lugar
    @GetMapping("/pagina1")
    public String pagina1(){
        return "pagina1";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    // Corrigido: Removido o .html
    @GetMapping("/admin")
    public String admin() {
        return "admin"; 
    }

    @GetMapping("/post")
    public String post() {
        return "post";
    }

    // 🔴 ADICIONADO: Rota para a página de Notícias
    @GetMapping("/noticias")
    public String noticias() {
        return "noticias"; 
    }

    // 🔴 ADICIONADO: Rota para a página de Projetos
    @GetMapping("/projeto")
    public String projeto() {
        return "Projeto"; // Mantenha a primeira letra maiúscula se o arquivo se chamar Projeto.html
    }

    // 🔴 ADICIONADO: Rota para a página de Eventos
    @GetMapping("/evento")
    public String evento() {
        return "Evento"; // Mantenha a primeira letra maiúscula se o arquivo se chamar Evento.html
    }
}
