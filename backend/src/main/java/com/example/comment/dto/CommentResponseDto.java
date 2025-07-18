package com.example.comment.dto;

import java.time.LocalDateTime;

import com.example.comment.domain.Comment;

public class CommentResponseDto {

    private Long id;
    private String content;
    private String authorName;
    private boolean isMine;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CommentResponseDto(Comment comment, Long currentUserId) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.authorName = comment.getAuthor().getName(); // 또는 getEmail()
        this.isMine = comment.getAuthor().getId().equals(currentUserId);
        this.createdAt = comment.getCreatedAt();
        this.updatedAt = comment.getUpdatedAt();
    }
}