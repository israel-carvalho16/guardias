package com.project.omni.Blog.service;

import  com.project.omni.Blog.dto.request.CommentRequest;
import  com.project.omni.Blog.dto.response.CommentResponse;
import  com.project.omni.Blog.exception.ResourceNotFoundException;
import  com.project.omni.Blog.model.Comment;
import  com.project.omni.Blog.model.Post;
import  com.project.omni.Blog.model.User;
import  com.project.omni.Blog.repository.CommentRepository;
import  com.project.omni.Blog.repository.PostRepository;
import  com.project.omni.Blog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public CommentResponse create(Long postId, CommentRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post não encontrado: " + postId));

        User author = getAuthenticatedUser();

        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setPost(post);
        comment.setAuthor(author);

        return toResponse(commentRepository.save(comment));
    }

    public List<CommentResponse> findByPost(Long postId) {
        return commentRepository.findByPostId(postId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public void delete(Long commentId) {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comentário não encontrado: " + commentId));

        boolean isAdmin = SecurityContextHolder.getContext()
                .getAuthentication().getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        boolean isOwner = comment.getAuthor().getEmail().equals(email);

        if (!isAdmin && !isOwner) {
            throw new RuntimeException("Sem permissão para deletar este comentário");
        }

        commentRepository.delete(comment);
    }

    private User getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
    }

    private CommentResponse toResponse(Comment comment) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setContent(comment.getContent());
        response.setAuthorName(comment.getAuthor().getName());
        response.setCreatedAt(comment.getCreatedAt());
        return response;
    }
}