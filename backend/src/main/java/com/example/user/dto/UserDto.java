package com.example.user.dto;

import com.example.user.domain.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;


public class UserDto {

    @Data
    @Builder
    public static class SignupRequest {
        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        private String email;

        @NotBlank(message = "이름은 필수입니다.")
        @Size(min = 2, max = 20, message = "이름은 2자 이상 20자 이하여야 합니다.")
        private String name;

        @NotBlank(message = "비밀번호는 필수입니다.")
        @Size(min = 4, max = 20, message = "비밀번호는 4자 이상 20자 이하여야 합니다.")
        private String password;

        @NotNull(message = "약관 동의는 필수입니다.")
        private Boolean agreeTerms;
    }

    @Data
    @Builder
    public static class SignupResponse {
        private Long id;
        private String email;
    }

    @Builder
    @Data
    public static class LoginRequest {
        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        private String email;

        @NotBlank(message = "비밀번호는 필수입니다.")
        private String password;
    }

    @Data
    @Builder
    public static class LoginResponse {
        private String accessToken;
        private String refreshToken;
    }

    @Data
    @Builder
    public static class RefreshTokenRequest {
        @NotBlank(message = "리프레시 토큰은 필수입니다.")
        private String refreshToken;
    }

    @Data
    @Builder
    public static class UserInfo {
        private Long id;
        private String email;
        private String name;
        private String role;
    }

    @Data
    @Builder
    public static class UserListItem {
        private Long id;
        private String email;
        private String name;
        private User.Role role;
        private Boolean isActive;
        private Boolean isTermsAgreed;
    }

    @Builder
    @Data
    public static class RoleUpdateRequest {
        @NotNull(message = "역할은 필수입니다.")
        private User.Role role;
    }


    @Data
    @Builder
    public static class MessageResponse {
        private String message;
    }

    @Data
    public static class PasswordChangeRequest {
        private String currentPassword;
        private String newPassword;
    }

    @Data
    @Builder
    public static class EmailCheckResponse {
        private Boolean isAvailable;
        private String message;

        public static EmailCheckResponse available() {
            return EmailCheckResponse.builder()
                    .isAvailable(true)
                    .message("사용 가능한 이메일입니다.")
                    .build();
        }

        public static EmailCheckResponse notAvailable() {
            return EmailCheckResponse.builder()
                    .isAvailable(false)
                    .message("이미 사용중인 이메일입니다.")
                    .build();
        }

    }
}