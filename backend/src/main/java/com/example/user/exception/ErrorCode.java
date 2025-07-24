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
    INVALID_TOKEN(401, "유효하지 않은 토큰입니다."),
    USER_NOT_ACTIVE(403, "사용자가 활성화되지 않았습니다."),
    CONCURRENT_REQUEST(409, "동시 요청으로 인해 충돌이 발생했습니다. 잠시 후 다시 시도해주세요."),
    TOKEN_EXPIRED(401, "토큰이 만료되었습니다. 다시 로그인해주세요."),
    REFRESH_TOKEN_NOT_FOUND(404, "리프레시 토큰을 찾을 수 없습니다."),

    // 기타
    INTERNAL_SERVER_ERROR(500, "서버 내부 오류가 발생했습니다."),
    BAD_REQUEST(400, "잘못된 요청입니다."),
    RESOURCE_NOT_FOUND(404, "요청한 리소스를 찾을 수 없습니다."),
    METHOD_NOT_ALLOWED(405, "허용되지 않은 HTTP 메소드입니다."),
    CONFLICT(409, "요청이 충돌을 일으켰습니다."),
    SERVICE_UNAVAILABLE(503, "서비스를 사용할 수 없습니다."),
    RATE_LIMIT_EXCEEDED(429, "요청이 너무 많습니다. 잠시 후 다시 시도해주세요."),
    INVALID_INPUT(400, "입력값이 유효하지 않습니다."),
    DATABASE_ERROR(500, "데이터베이스 오류가 발생했습니다."),
    OPERATION_NOT_SUPPORTED(501, "지원하지 않는 작업입니다."),
    INVALID_PARAMETER(400, "잘못된 파라미터입니다."),
    MISSING_PARAMETER(400, "필수 파라미터가 누락되었습니다."),
    UNEXPECTED_ERROR(500, "예상치 못한 오류가 발생했습니다."),
    INVALID_CREDENTIALS(401, "잘못된 자격 증명입니다."),
    ACCOUNT_LOCKED(423, "계정이 잠겼습니다. 관리자에게 문의하세요."),
    PASSWORD_TOO_WEAK(400, "비밀번호가 너무 약합니다. 더 강력한 비밀번호를 사용하세요."),
    EMAIL_NOT_VERIFIED(403, "이메일이 인증되지 않았습니다. 인증 후 다시 시도해주세요."),
    ACCOUNT_DISABLED(403, "계정이 비활성화되었습니다. 관리자에게 문의하세요."),
    INVALID_REQUEST(400, "잘못된 요청입니다."),
    OPERATION_FAILED(500, "작업이 실패했습니다. 나중에 다시 시도해주세요."),
    UNAUTHORIZED_ACCESS(401, "인증되지 않은 접근입니다."),
    INVALID_FILE_TYPE(415, "지원하지 않는 파일 형식입니다."),
    FILE_TOO_LARGE(413, "파일 크기가 너무 큽니다. 제한을 초과했습니다."),
    INSUFFICIENT_STORAGE(507, "저장 공간이 부족합니다. 나중에 다시 시도해주세요."),
    INVALID_DATE_FORMAT(400, "날짜 형식이 올바르지 않습니다."),
    INVALID_JSON_FORMAT(400, "JSON 형식이 올바르지 않습니다."),
    UNSUPPORTED_MEDIA_TYPE(415, "지원하지 않는 미디어 타입입니다."),
    INVALID_OPERATION(400, "유효하지 않은 작업입니다."),
    UNEXPECTED_EXCEPTION(500, "예상치 못한 예외가 발생했습니다. 관리자에게 문의하세요."),
    INVALID_EMAIL(400, "유효하지 않은 이메일 형식입니다."),;
    // 상태 코드와 메시지를 저장







    private final int status;
    private final String message;
}