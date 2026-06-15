package com.project.omni.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.project.omni.model.Usuario;
import com.project.omni.service.UsuarioService;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @GetMapping("/cadastro")
    public String cadastro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "cadastro";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Usuario usuario, Model model) {

        try {
            service.salvar(usuario); // corrigido para bater com o service

            return "redirect:/login";
        } catch (RuntimeException e) {

            model.addAttribute("erro", e.getMessage()); 
            model.addAttribute("usuario", usuario);
            
            return "cadastro";
        }
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/home")
    public String home() {
        return "home";
    }
}