package com.project.omni.Blog.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.project.omni.Blog.dto.request.PostRequest;
import com.project.omni.Blog.dto.response.PostResponse;
import com.project.omni.Blog.model.Post;
import com.project.omni.Blog.repository.PostRepository;
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
            post.setImageUrl(urlImagem); 

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

    // ==========================================================================
    // MÉTODO CONVERSOR SIMPLIFICADO E SEGURO CONTRA ERROS DE COMPILAÇÃO
    // ==========================================================================
    private PostResponse convertToResponse(Post post) {
        PostResponse response = new PostResponse();
        response.setId(post.getId());
        response.setTitle(post.getTitle());
        response.setContent(post.getContent());
        response.setImageUrl(post.getImageUrl());
        
        // Se a sua Entity possuir os métodos abaixo descomente-os para preencher o DTO:
        // response.setCategory(post.getCategory()); 
        // response.setCreatedAt(post.getCreatedAt());
        
        return response;
    }
}