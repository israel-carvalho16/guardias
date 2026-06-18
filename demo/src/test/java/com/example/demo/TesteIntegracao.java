package com.example.demo;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach; 
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders; 
import org.springframework.web.context.WebApplicationContext; 



@SpringBootTest
public class TesteIntegracao {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext; 

    // REQUISITO 2 e 3: Injeção real do repositório usando @Autowired
    @Autowired
    private Repository_feed repository_feed; 

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }
              @Test
    public void deveSalvarContatoNoBancoDeDadosRealEmMemoria() throws Exception {
        // REQUISITO 5: Disparar requisição enviando todos os parâmetros de forma encadeada corretamente
        mockMvc.perform(post("/contato/enviar")
                .param("nome", "Gabriel Geraldo")
                .param("email", "gabriel.geraldo@exemplo.com")
                .param("mensagem", "Envio de teste de integração real.")
                .param("avaliacao", "5")) // Garante o envio do parâmetro exigido pelo Controller
                .andExpect(status().isOk());

        // REQUISITO 6: Busca direta no banco H2 usando o repositório real
        var contatosGravados = repository_feed.findAll();
        
        // Validação programática
        assertEquals(1, contatosGravados.size(), "O banco de dados deveria conter exatamente 1 registro.");
        
        var contatoSalvo = contatosGravados.get(0);
        assertNotNull(contatoSalvo.getId(), "O ID deveria ter sido gerado pelo banco H2.");

        // Asserção do nome original enviado
        assertEquals("Gabriel Geraldo", contatoSalvo.getNome(), 
                "O nome gravado no banco H2 não corresponde ao enviado.");
    }
}