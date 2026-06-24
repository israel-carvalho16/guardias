package com.project.omni.Blog.service;

import  com.project.omni.Blog.dto.request.PostRequest;
import  com.project.omni.Blog.dto.response.CommentResponse;
import  com.project.omni.Blog.dto.response.PostResponse;
import  com.project.omni.Blog.exception.ResourceNotFoundException;
import  com.project.omni.Blog.model.Post;
import  com.project.omni.Blog.model.User;
import  com.project.omni.Blog.repository.PostRepository;
import  com.project.omni.Blog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

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
        post.setAuthor(author);

        return toResponse(postRepository.save(post));
    }

    public PostResponse update(Long id, PostRequest request) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post não encontrado: " + id));

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());

        return toResponse(postRepository.save(post));
    }

    public void delete(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post não encontrado: " + id));
        postRepository.delete(post);
    }

    private User getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
    }

    private PostResponse toResponse(Post post) {
        PostResponse response = new PostResponse();
        response.setId(post.getId());
        response.setTitle(post.getTitle());
        response.setContent(post.getContent());
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