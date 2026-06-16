

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
public class TestedeSucesso {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RepositoryFeed repositoryFeed;

    @Test
    public void deveRetornarSucessoAoEnviarContato() throws Exception {
        // 1. Simula que o salvamento no banco ocorreu com sucesso
        Mockito.when(repositoryFeed.save(Mockito.any(Feed.class)))
               .thenReturn(new Feed()); 

        // 2. Executa a requisição e espera status 200 OK
        mockMvc.perform(MockMvcRequestBuilders.post("/contato/enviar")
               .contentType(MediaType.APPLICATION_JSON)
               .content("{\"nome\": \"Teste\", \"mensagem\": \"Olá\"}"))
               .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
