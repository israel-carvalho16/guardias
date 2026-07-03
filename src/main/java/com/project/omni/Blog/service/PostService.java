
package com.project.omni.Blog.service;

import com.project.omni.Blog.dto.request.PostRequest;
import com.project.omni.Blog.dto.response.CommentResponse;
import com.project.omni.Blog.dto.response.PostResponse;
import com.project.omni.Blog.exception.ResourceNotFoundException;
import com.project.omni.Blog.model.Post;
import com.project.omni.Blog.model.User;
import com.project.omni.Blog.repository.PostRepository;
import com.project.omni.Blog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // Injeta o caminho absoluto configurado no seu application.properties
    @Value("${app.upload.dir:C:/Users/Pichau/Documents/projetos/guardias/uploads/}")
    private String uploadDir;

    public List<PostResponse> findAll() {
        return postRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public PostResponse findById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post não encontrado: " + id));
        return toResponse(post);
    }

    public PostResponse create(PostRequest request) {
        User author = getAuthenticatedUser();

        Post post = new Post();
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setCategory(request.getCategory());
        post.setAuthor(author);

        // Processa o upload do arquivo binário recebido do painel
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            String fileName = uploadFile(request.getImage());
            post.setImageUrl(fileName); // Grava o nome do arquivo gerado no banco Neon
        }

        return toResponse(postRepository.save(post));
    }

    public PostResponse update(Long id, PostRequest request) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post não encontrado: " + id));

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setCategory(request.getCategory());

        // CORREÇÃO: Se uma nova imagem foi selecionada, limpa a imagem velha do disco do PC
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            // Se o post já tinha uma imagem associada, deleta o arquivo físico antigo
            if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
                try {
                    Path caminhoImagemVelha = Paths.get(this.uploadDir).resolve(post.getImageUrl());
                    Files.deleteIfExists(caminhoImagemVelha);
                } catch (IOException e) {
                    System.err.println("Aviso: Não foi possível remover o arquivo físico antigo: " + post.getImageUrl());
                }
            }
            
            // Faz o upload do arquivo novo
            String fileName = uploadFile(request.getImage());
            post.setImageUrl(fileName);
        }

        return toResponse(postRepository.save(post));
    }

    public void delete(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post não encontrado: " + id));
        
        // CORREÇÃO EXTRA: Ao deletar o post completo, apaga também o arquivo de imagem associado a ele
        if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
            try {
                Path caminhoImagem = Paths.get(this.uploadDir).resolve(post.getImageUrl());
                Files.deleteIfExists(caminhoImagem);
            } catch (IOException e) {
                System.err.println("Aviso: Falha ao remover o arquivo físico no delete.");
            }
        }
        
        postRepository.delete(post);
    }

    /**
     * Motor de persistência física de arquivos em disco
     */
    private String uploadFile(MultipartFile file) {
        try {
            // Garante que a pasta destino exista no diretório do Windows
            Path uploadPath = Paths.get(this.uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Descobre a extensão original (.png, .jpg, .webp)
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            // Gera um hash único universal (UUID) para evitar sobrescrever arquivos de mesmo nome
            String uniqueFileName = UUID.randomUUID().toString() + extension;
            Path targetLocation = uploadPath.resolve(uniqueFileName);

            // Copia o fluxo de bytes brutos direto para a pasta final
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return uniqueFileName; // Retorna o nome final persistido
        } catch (IOException ex) {
            throw new RuntimeException("Falha de gravação de mídia no servidor: " + ex.getMessage(), ex);
        }
    }

    private User getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        // CORREÇÃO: Admins ficam numa tabela separada (admin), não na tabela "users" do Blog.
        // Se o autor autenticado ainda não tiver um registro de User (ex: é um admin),
        // criamos um automaticamente para que ele possa ser vinculado como autor do post.
        return userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User novoUsuario = new User();
                    novoUsuario.setEmail(email);
                    novoUsuario.setName(email);
                    // Senha não é usada para login por esse caminho (autenticação é via JWT do admin)
                    novoUsuario.setPassword("N/A");
                    return userRepository.save(novoUsuario);
                });
    }

    private PostResponse toResponse(Post post) {
        PostResponse response = new PostResponse();
        response.setId(post.getId());
        response.setTitle(post.getTitle());
        response.setContent(post.getContent());
        response.setCategory(post.getCategory());
        // MAPEADO: Repassa o link/nome da imagem para o DTO de resposta do Front-End
        response.setImageUrl(post.getImageUrl());
        response.setAuthorName(post.getAuthor().getName());
        response.setCreatedAt(post.getCreatedAt());

        List<CommentResponse> comments = post.getComments().stream()
                .map(comment -> {
                    CommentResponse cr = new CommentResponse();
                    cr.setId(comment.getId());
                    cr.setContent(comment.getContent());
                    cr.setAuthorName(comment.getAuthor().getName());
                    cr.setCreatedAt(comment.getCreatedAt());
                    return cr;
                })
                .collect(Collectors.toList());

        response.setComments(comments);
        return response;
    }
}
