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

    // Rota POST corrigida: Sem processamento de CPF e adaptada para a nova estrutura
    @PostMapping("/api/auth/volun-form")
    @ResponseBody // Permite retornar um ResponseEntity direto dentro de um @Controller comum
    public ResponseEntity<?> salvarDados(@RequestBody Map<String, String> dados) {
        try {
            // Extrai as strings do JSON enviado pelo JavaScript (CPF removido com sucesso)
            String nome = dados.get("name");
            String email = dados.get("email");
            String phone = dados.get("phone");
            String gender = dados.get("gender");

            // Validação corrigida: CPF não é mais obrigatório e nem verificado
            if (nome == null || email == null) {
                return ResponseEntity.badRequest().body("Campos obrigatórios ausentes no formulário.");
            }

            V novoV = new V();
            novoV.setNome(nome);
            novoV.setEmail(email);
            
            // CONCATENAÇÃO CORRIGIDA: Salva apenas Telefone e Gênero dentro de 'links'
            novoV.setLinks("Tel: " + phone + " | Gênero: " + gender);
            novoV.setStatus("PENDENTE"); 

            Repository_Voluntário.save(novoV);

            // Retorna sucesso para o JavaScript saber que deu tudo certo
            Map<String, String> resposta = new HashMap<>();
            resposta.put("mensagem", "Inscrição realizada com sucesso!");
            return ResponseEntity.ok(resposta);

        } catch (Exception e) {
            System.out.println("Erro ao salvar inscrição: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Erro no servidor: " + e.getMessage());
        }
    }

    // ==========================================================================
    // ROTAS GERENCIAIS - INTEGRAÇÃO COM O OMNI DEVPANEL
    // ==========================================================================

    /**
     * Endpoint para listar todas as inscrições cadastradas na tabela do painel
     */
    @GetMapping({"/api/admin/voluntarios", "/api/admin/voluntarios/"})
    @ResponseBody 
    public ResponseEntity<?> listarTodosParaOAdmin() {
        try {
            // Busca os dados da tabela (JPA usará o mapeamento atualizado da classe V.java)
            List<V> lista = Repository_Voluntário.findAll();
            
            // Converte a lista usando Getters Java direto na memória de forma segura
            List<Map<String, Object>> dadosSimplificados = lista.stream().map(v -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", v.getId());
                map.put("nome", v.getNome() != null ? v.getNome() : "Sem nome");
                map.put("email", v.getEmail() != null ? v.getEmail() : "Sem e-mail");
                map.put("links", v.getLinks() != null ? v.getLinks() : "Sem links");
                map.put("status", v.getStatus() != null ? v.getStatus() : "PENDENTE");
                return map;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(dadosSimplificados);
        } catch (Exception e) {
            System.out.println("Erro na listagem de inscritos: " + e.getMessage());
            return ResponseEntity.badRequest().body("Erro ao processar dados: " + e.getMessage());
        }
    }

    /**
     * Endpoint para o administrador Alterar o Status (Aprovar / Recusar) de uma inscrição
     */
    @PutMapping({"/api/admin/voluntarios/{id}/status", "/api/admin/voluntarios/{id}/status/"})
    @ResponseBody 
    public ResponseEntity<?> atualizarStatus(@PathVariable Long id, @RequestParam("status") String novoStatus) {
        try {
            V voluntario = Repository_Voluntário.findById(id)
                    .orElseThrow(() -> new RuntimeException("Inscrição não encontrada com o ID: " + id));
            
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