package com.example.user.comment;

import com.example.board.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public class CommentRepoistory extends JpaRepository<Comment, Long> {
    List<Comment> findByPost(Post post);

    // 게시글 ID로 댓글 조회 (fetch join 없이 간단하게)
    List<Comment> findByPostId(Long postId);

    // 특정 사용자가 작성한 댓글 조회
    List<Comment> findByAuthorId(Long authorId);
}
