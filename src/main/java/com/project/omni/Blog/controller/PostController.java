package com.project.omni.Blog.controller;

import com.project.omni.Blog.dto.request.PostRequest;
import com.project.omni.Blog.dto.response.PostResponse;
import com.project.omni.Blog.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping
    public ResponseEntity<List<PostResponse>> findAll() {
        return ResponseEntity.ok(postService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(postService.findById(id));
    }

    // CORRIGIDO: Agora recebe arquivos físicos via FormData do JavaScript
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostResponse> create(@Valid @ModelAttribute PostRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.create(request));
    }

    // Opcional: Se quiser atualizar o post com uma nova imagem futuramente
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostResponse> update(@PathVariable Long id,
                                               @Valid @ModelAttribute PostRequest request) {
        return ResponseEntity.ok(postService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        postService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
