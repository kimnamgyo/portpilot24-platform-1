package com.example.comment.repository;

import com.example.comment.domain.Comment;
import com.example.post.domain.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 게시글 엔티티로 댓글 조회
    List<Comment> findByPost(PostEntity post);

    // 게시글 ID로 댓글 조회
    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId")
    List<Comment> findByPostId(@Param("postId") Long postId);

    // 사용자 ID로 댓글 조회
//    List<Comment> findByAuthorUserId(Long userId);
}
