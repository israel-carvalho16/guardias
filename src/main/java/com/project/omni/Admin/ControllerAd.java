package com.project.omni.Admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ControllerAd {

    // Rota limpa que abre o formulário de cadastro do administrador
    @GetMapping("/AdminForm")
    public String abrirPagina() {
        return "AdminForm"; // Abre o arquivo AdminForm.html
    }

    // Rota oficial do painel administrativo
    @GetMapping("/admin-dashboard")
    public String exibirPainel() {
        return "admin-dashboard"; // Abre o arquivo admin-dashboard.html
    }
}
