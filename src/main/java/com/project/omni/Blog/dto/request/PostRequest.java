package com.project.omni.Blog.dto.request;

import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PostRequest {

    @NotBlank(message = "O título é obrigatório")
    private String title;

    @NotBlank(message = "O conteúdo é obrigatório")
    private String content;

    @NotBlank(message = "A categoria é obrigatória")
    private String category;

    // NOVO CAMPO: Recebe o arquivo binário da imagem física vindo do painel HTML
    private MultipartFile image; 
}
