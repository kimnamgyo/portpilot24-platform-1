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
        private Integer id;
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
        private String token;
    }

    @Data
    @Builder
    public static class UserInfo {
        private Integer id;
        private String email;
        private String name;
        private User.Role role;
    }

    @Data
    @Builder
    public static class UserListItem {
        private Integer id;
        private String email;
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
}