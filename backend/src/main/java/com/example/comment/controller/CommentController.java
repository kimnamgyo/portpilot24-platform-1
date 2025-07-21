package com.example.comment.controller;

import com.example.comment.domain.Comment;
import com.example.comment.dto.CommentRequestDto;
import com.example.comment.dto.CommentResponseDto;
import com.example.comment.service.CommentService;
import com.example.user.domain.User;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * 댓글 작성
     */
    @PostMapping("/{postId}")
    public ResponseEntity<?> createComment(@PathVariable Long postId,
            @AuthenticationPrincipal User user,
            @RequestBody CommentRequestDto request) {
        Comment comment = commentService.createComment(postId, user.getUserId().longValue(), request.getContent());
        return ResponseEntity.ok(new CommentResponseDto(comment, user.getUserId().longValue())); // ✅ 수정
    }

    /**
     * 댓글 조회 (인증 필요)
     */
    @GetMapping("/{postId}")
    public ResponseEntity<?> getComments(@PathVariable Long postId,
            @AuthenticationPrincipal User user) {
        Long currentUserId = user != null ? user.getUserId().longValue() : -1L;

        List<Comment> comments = commentService.getCommentsByPostId(postId);
        List<CommentResponseDto> response = comments.stream()
                .map(comment -> new CommentResponseDto(comment, currentUserId))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * 댓글 수정
     */
    @PatchMapping("/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable Long commentId,
            @RequestParam String content,
            @AuthenticationPrincipal User user) {
        try {
            commentService.updateComment(commentId, content, user);
            return ResponseEntity.ok("수정 완료");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 댓글 삭제
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId,
            @AuthenticationPrincipal User user) {
        try {
            commentService.deleteComment(commentId, user);
            return ResponseEntity.ok("삭제 완료");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
