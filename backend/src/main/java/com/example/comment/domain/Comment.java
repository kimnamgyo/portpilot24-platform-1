package com.example.comment.domain;

import com.example.post.domain.Post;
import com.example.user.domain.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;  // ✅ 댓글 고유 ID

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;  // ✅ 댓글이 속한 게시글

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private Long userId;  // ✅ 댓글 작성자

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;

    // === 생성자 ===
    public Comment() {}

    public Comment(Post post, Long userId, String content) {
        this.post = post;
        this.userId = userId;
        this.content = content;
    }

    // === 비즈니스 로직 ===
    public void updateContent(String newContent) {
        this.content = newContent;
        this.updatedAt = LocalDateTime.now();
    }

    // === Getters & Setters ===
    public Long getCommentId() {
        return commentId;
    }

    public Post getPost() {
        return post;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    public Long getUserId(){
        return userId;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public void setContent(String content) {
        this.content = content;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }

}
