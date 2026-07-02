package com.project.omni.Volun;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Controller
@CrossOrigin(origins = "*", allowedHeaders = "*") // IMPEDE O ERRO DE BLOQUEIO DE CORS NO NAVEGADOR
public class ControlerVolun {

    private final Repository_Voluntário Repository_Voluntário;

    ControlerVolun(Repository_Voluntário Repository_Voluntário) {
        this.Repository_Voluntário = Repository_Voluntário;
    }

    // Rota POST original: Continua como @Controller para fazer o redirecionamento HTML perfeito
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
        novoV.setLinks("CPF: " + cpf + " | Tel: " + phone + " | Gênero: " + gender);
        novoV.setStatus("PENDENTE"); 

        Repository_Voluntário.save(novoV);

        return "redirect:/pagina1"; 
    }

    // ==========================================================================
    // NOVAS ROTAS GERENCIAIS - INTEGRAÇÃO COM O OMNI DEVPANEL (JSON REST)
    // ==========================================================================

    /**
     * Endpoint para listar todos os voluntários cadastrados na tabela do painel
     * CORREÇÃO DEFINITIVA: Usa o findAll() padrão do JPA blindando fallbacks contra colunas inexistentes no banco Neon
     */
    @GetMapping({"/api/admin/voluntarios", "/api/admin/voluntarios/"})
    @ResponseBody 
    public ResponseEntity<?> listarTodosParaOAdmin() {
        try {
            // Retorna ao método nativo do JPA para evitar erros de ResultSet de queries customizadas
            List<V> lista = Repository_Voluntário.findAll();
            
            // Converte a lista usando Getters Java direto na memória e trata nulos com segurança
            List<Map<String, Object>> dadosSimplificados = lista.stream().map(v -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", v.getId());
                map.put("nome", v.getNome() != null ? v.getNome() : "Sem nome");
                map.put("email", v.getEmail() != null ? v.getEmail() : "Sem e-mail");
                map.put("links", v.getLinks() != null ? v.getLinks() : "Sem links");
                
                // Tratamento preventivo caso o registro antigo no banco possua valor nulo na coluna nova
                map.put("status", v.getStatus() != null ? v.getStatus() : "PENDENTE");
                return map;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(dadosSimplificados);
        } catch (Exception e) {
            System.out.println("Erro na listagem de voluntários: " + e.getMessage());
            return ResponseEntity.badRequest().body("Erro ao processar dados: " + e.getMessage());
        }
    }

    /**
     * Endpoint para o administrador Alterar o Status (Aprovar / Recusar) de um voluntário
     */
    @PutMapping({"/api/admin/voluntarios/{id}/status", "/api/admin/voluntarios/{id}/status/"})
    @ResponseBody 
    public ResponseEntity<?> atualizarStatus(@PathVariable Long id, @RequestParam("status") String novoStatus) {
        try {
            V voluntario = Repository_Voluntário.findById(id)
                    .orElseThrow(() -> new RuntimeException("Voluntário não encontrado com o ID: " + id));
            
            voluntario.setStatus(novoStatus.toUpperCase().trim());
            V atualizado = Repository_Voluntário.save(voluntario);
            
            Map<String, Object> response = new HashMap<>();
            response.put("id", atualizado.getId());
            response.put("status", atualizado.getStatus());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao atualizar status: " + e.getMessage());
        }
    }
}
