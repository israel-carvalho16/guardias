package com.project.omni.Blog.service;

import org.springframework.stereotype.Service;
import com.project.omni.Blog.dto.request.PostRequest;
import com.project.omni.Blog.dto.response.PostResponse;
import com.project.omni.Blog.model.Post;
import com.project.omni.Blog.model.User; // Certifique-se de importar a Model User
import com.project.omni.Blog.repository.PostRepository;
import com.project.omni.Blog.repository.UserRepository; // 🔥 IMPORTADO: Seu repositório existente
import com.project.omni.Claud.CloudinaryService;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository; 
    private final CloudinaryService cloudinaryService; 
    private final UserRepository userRepository; // 🔥 ADICIONADO: O Lombok vai injetar o repositório aqui automaticamente

    public List<PostResponse> findAll() {
        return postRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public PostResponse findById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post não encontrado com o ID: " + id));
        return convertToResponse(post);
    }

    public PostResponse create(PostRequest request) {
        try {
            String urlImagem = null;
            if (request.getFoto() != null && !request.getFoto().isEmpty()) {
                urlImagem = cloudinaryService.uploadImagem(request.getFoto());
            }

            Post post = new Post();
            post.setTitle(request.getTitle());
            post.setContent(request.getContent());
            post.setCategory(request.getCategory()); // 🔥 Adicione o mapeamento da Categoria que estava faltando!
            post.setImageUrl(urlImagem); 

            // 🔥 ADICIONADO: Busca o Administrador (ID 1, visto nos seus logs anteriores)
            User autor = userRepository.findById(1L)
                    .orElseThrow(() -> new RuntimeException("Administrador padrão (ID 1) não encontrado no banco Neon.Tech"));
            
            post.setAuthor(autor); // 🔥 ADICIONADO: Vincula o autor à notícia para não dar o erro de Constraint no Banco!

            Post salvo = postRepository.save(post);
            return convertToResponse(salvo);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao fazer upload da imagem para o Cloudinary", e);
        }
    }

    public PostResponse update(Long id, PostRequest request) {
        try {
            Post postExistente = postRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Post não encontrado"));

            postExistente.setTitle(request.getTitle());
            postExistente.setContent(request.getContent());
            postExistente.setCategory(request.getCategory()); // 🔥 Atualiza a categoria também se necessário

            if (request.getFoto() != null && !request.getFoto().isEmpty()) {
                String novaUrl = cloudinaryService.uploadImagem(request.getFoto());
                postExistente.setImageUrl(novaUrl);
            }

            Post atualizado = postRepository.save(postExistente);
            return convertToResponse(atualizado);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao atualizar imagem no Cloudinary", e);
        }
    }

    public void delete(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post não encontrado com o ID: " + id));
        postRepository.delete(post);
    }

 private PostResponse convertToResponse(Post post) {
    PostResponse response = new PostResponse();
    response.setId(post.getId());
    response.setTitle(post.getTitle());
    response.setContent(post.getContent());
    
    // 🔥 ESSA LINHA É CRUCIAL! Se ela não existir, a imagem não vai para o Front-End:
    response.setImageUrl(post.getImageUrl()); 
    
    response.setCategory(post.getCategory()); 
    return response;
}
}

    
