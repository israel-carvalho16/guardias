package com.project.omni;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.mockito.Mockito;

@SpringBootTest
@AutoConfigureMockMvc
public class testedeerro {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RepositoryFeed repositoryFeed;

    @Test
    public void deveRetornarErroAoEnviarContatoQuandoFalharAoSalvarNoBanco() throws Exception {
        // Configuração do Mock
        Mockito.when(repositoryFeed.save(Mockito.any(Feed.class)))
               .thenThrow(new RuntimeException("Erro no banco"));

        // Execução e validação
        mockMvc.perform(MockMvcRequestBuilders.post("/contato/enviar")
               .contentType(MediaType.APPLICATION_JSON)
               .content("{\"nome\": \"Teste\", \"mensagem\": \"Olá\"}"))
               .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }
}
