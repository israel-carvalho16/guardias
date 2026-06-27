package com.project.omni.Blog.dto.response;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostResponse {
    private Long id;
    private String title;
    private String content;
    private String category;
    private String authorName;
    private LocalDateTime createdAt;
    private List<CommentResponse> comments;
    private String imageUrl;
}