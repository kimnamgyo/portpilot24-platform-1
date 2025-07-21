package com.example.comment.repository;

import com.example.comment.domain.Comment;
import com.example.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 게시글 엔티티로 댓글 조회
    List<Comment> findByPost(Post post);

    // 게시글 ID로 댓글 조회
    List<Comment> findByPostId(Long postId);

    // 사용자 ID로 댓글 조회
    List<Comment> findByAuthorUserId(Long userId);
}
