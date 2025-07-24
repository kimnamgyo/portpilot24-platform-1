package com.example.comment.dto;

import jakarta.validation.constraints.NotBlank;

public class CommentRequestDto {

    @NotBlank(message = "댓글 내용은 비어있을 수 없습니다.")
    private String content;

    public CommentRequestDto() {
    }

    public CommentRequestDto(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
