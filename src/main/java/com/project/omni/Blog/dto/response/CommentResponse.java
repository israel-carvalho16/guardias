package com.project.omni.Blog.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

import com.project.omni.Blog.model.User;

@Data
public class CommentResponse {
    private Long id;
    private String content;
    private String authorName;
    private LocalDateTime createdAt;
}