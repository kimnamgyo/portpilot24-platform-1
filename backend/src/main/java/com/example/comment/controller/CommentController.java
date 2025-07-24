package com.example.comment.controller;

import com.example.comment.domain.Comment;
import com.example.comment.dto.CommentRequestDto;
import com.example.comment.dto.CommentResponseDto;
import com.example.comment.service.CommentService;
import com.example.user.domain.User;

import com.example.user.repository.UserRepository;
import com.example.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;
    private final UserService userService;

    public CommentController(CommentService commentService, UserService userService) {
        this.commentService = commentService;
        this.userService = userService;
    }

    /**
     * 댓글 작성
     */
    @PostMapping("/{postId}")
    public ResponseEntity<?> createComment(@PathVariable Long postId,
            Authentication authentication,
            @RequestBody CommentRequestDto request) {
        String email = authentication.getName();
        User user = userService.getUserByEmail(email);
        Comment comment = commentService.createComment(postId, user.getUserId().longValue(), request.getContent());
        return ResponseEntity.ok(new CommentResponseDto(comment, user.getUserId().longValue())); // ✅ 수정
    }

    /**
     * 댓글 조회 (인증 필요)
     */
    @GetMapping("/{postId}")
    public ResponseEntity<?> getComments(@PathVariable Long postId,
                                         Authentication authentication) {
        // 인증된 사용자 정보 가져오기
        String email = authentication.getName();
        User user = userService.getUserByEmail(email);
        Long currentUserId = user != null ? user.getUserId() : -1L;

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
            Authentication authentication) {
        try {
            String email = authentication.getName();
            User user = userService.getUserByEmail(email);
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
            Authentication authentication) {
        try {
            String email = authentication.getName();
            User user = userService.getUserByEmail(email);
            commentService.deleteComment(commentId, user);
            return ResponseEntity.ok("삭제 완료");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/admin/{commentId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROOT')")
    public ResponseEntity<?> deleteCommentByAdmin(@PathVariable Long commentId,
                                                  Authentication authentication) {
        try {
            commentService.deleteCommentById(commentId);
            return ResponseEntity.ok("관리자에 의한 댓글 삭제 완료");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
