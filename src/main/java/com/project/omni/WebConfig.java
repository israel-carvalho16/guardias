package com.project.omni;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 1. Mapeamento das telas do Administrador
        registry.addViewController("/AdminForm").setViewName("AdminForm");
        registry.addViewController("/admin-dashboard").setViewName("admin-dashboard");
        
        // 2. Mapeamento das telas de Login e Registro
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/register").setViewName("register");
        registry.addViewController("/pagina1").setViewName("pagina1");
        
        // 3. Mapeamento de todas as outras páginas do Menu
        registry.addViewController("/noticias").setViewName("noticias");
        registry.addViewController("/projeto").setViewName("projeto");
        registry.addViewController("/evento").setViewName("evento");
        registry.addViewController("/mg").setViewName("mg");
        registry.addViewController("/ce").setViewName("ce");
        registry.addViewController("/contatos").setViewName("contatos");
        registry.addViewController("/orgaoambiental").setViewName("orgaoambiental");
        
        // Mapeia a nova página de leitura de artigos individuais que criamos
        registry.addViewController("/noticiaAberta").setViewName("noticiaAberta");
        
        // 4. CORREÇÃO CRÍTICA: Mapeia as duas variações da página inicial (com e sem extensão)
        registry.addViewController("/").setViewName("index");
        registry.addViewController("/index.html").setViewName("index");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // NOVO CORRIGIDO: Pega o caminho da pasta raiz do projeto de forma dinâmica no PC atual
        String osPath = Paths.get("uploads").toAbsolutePath().toUri().toString();
        
        // Expõe a URL pública mapeando para a pasta local independente de usuário do Windows
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(osPath);
    }
}
