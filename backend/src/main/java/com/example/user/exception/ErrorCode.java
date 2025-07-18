package com.example.user.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // User 관련
    USER_NOT_FOUND(404, "사용자를 찾을 수 없습니다."),
    DUPLICATE_EMAIL(409, "이미 사용중인 이메일입니다."),
    TERMS_NOT_AGREED(400, "약관에 동의해주세요."),
    INVALID_PASSWORD(401, "비밀번호가 올바르지 않습니다."),

    // 인증 관련
    UNAUTHORIZED(401, "인증이 필요합니다."),
    FORBIDDEN(403, "권한이 없습니다."),
    INVALID_TOKEN(401, "유효하지 않은 토큰입니다.");

    private final int status;
    private final String message;
}