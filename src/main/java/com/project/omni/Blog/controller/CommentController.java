package com.project.omni.Blog.controller;

import com.project.omni.Blog.dto.request.CommentRequest;
import com.project.omni.Blog.dto.response.CommentResponse;
import com.project.omni.Blog.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/post/{postId}")
    public ResponseEntity<CommentResponse> create(@PathVariable Long postId,
                                                  @Valid @RequestBody CommentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.create(postId, request));
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentResponse>> findByPost(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.findByPost(postId));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> delete(@PathVariable Long commentId) {
        commentService.delete(commentId);
        return ResponseEntity.noContent().build();
    }
}