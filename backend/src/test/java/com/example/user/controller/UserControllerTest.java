package com.example.user.controller;

import com.example.config.SecurityConfig;
import com.example.user.domain.User;
import com.example.user.dto.UserDto;
import com.example.user.exception.BusinessException;
import com.example.user.exception.ErrorCode;
import com.example.user.service.JwtAuthenticationEntryPoint;
import com.example.user.service.JwtAuthenticationFilter;
import com.example.user.service.JwtUtil;
import com.example.user.service.UserDetailsServiceImpl;
import com.example.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService; // @MockBean 대신 @Autowired로 변경

    // 테스트를 위한 별도의 설정 클래스
    @TestConfiguration
    static class TestConfig {

        // 컨트롤러가 의존하는 UserService를 모의 객체로 등록합니다.
        @Bean
        public UserService userService() {
            return Mockito.mock(UserService.class);
        }

        // SecurityConfig가 의존하는 빈들을 모의 객체로 직접 생성하여 등록합니다.
        @Bean
        public JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint() {
            return Mockito.mock(JwtAuthenticationEntryPoint.class);
        }

        @Bean
        public JwtUtil jwtUtil() {
            return Mockito.mock(JwtUtil.class);
        }

        @Bean
        public UserDetailsServiceImpl userDetailsService() {
            return Mockito.mock(UserDetailsServiceImpl.class);
        }

        @Bean
        public JwtAuthenticationFilter jwtAuthenticationFilter() {
            // JwtAuthenticationFilter는 다른 모의 객체들을 의존하므로, 주입하여 생성합니다.
            return new JwtAuthenticationFilter(jwtUtil(), userDetailsService());
        }
    }

    private UserDto.SignupRequest signupRequest;
    private UserDto.LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        signupRequest = UserDto.SignupRequest.builder()
                .email("test@example.com")
                .password("password")
                .name("testuser")
                .agreeTerms(true)
                .build();

        loginRequest = UserDto.LoginRequest.builder()
                .email("test@example.com")
                .password("password")
                .build();
    }

    @Test
    @DisplayName("회원가입 성공")
    void signup_success() throws Exception {
        UserDto.SignupResponse response = UserDto.SignupResponse.builder()
                .id(1L)
                .email("test@example.com")
                .build();
        given(userService.signup(any(UserDto.SignupRequest.class))).willReturn(response);

        mockMvc.perform(post("/api/users/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 중복")
    void signup_fail_duplicateEmail() throws Exception {
        given(userService.signup(any(UserDto.SignupRequest.class)))
                .willThrow(new BusinessException(ErrorCode.DUPLICATE_EMAIL));

        mockMvc.perform(post("/api/users/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(ErrorCode.DUPLICATE_EMAIL.getMessage()));
    }

    @Test
    @DisplayName("로그인 성공")
    void login_success() throws Exception {
        UserDto.LoginResponse response = UserDto.LoginResponse.builder()
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .build();
        given(userService.login(any(UserDto.LoginRequest.class))).willReturn(response);

        mockMvc.perform(post("/api/users/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"));
    }

    @Test
    @DisplayName("로그아웃 성공")
    @WithMockUser(username = "test@example.com")
    void logout_success() throws Exception {
        UserDto.MessageResponse response = UserDto.MessageResponse.builder().message("로그아웃 완료").build();
        given(userService.logout(anyString())).willReturn(response);

        mockMvc.perform(post("/api/users/logout")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("로그아웃 완료"));
    }

    @Test
    @DisplayName("토큰 재발급 성공")
    void refreshToken_success() throws Exception {
        UserDto.RefreshTokenRequest request = UserDto.RefreshTokenRequest.builder().refreshToken("old-refresh-token").build();
        UserDto.LoginResponse response = UserDto.LoginResponse.builder()
                .accessToken("new-access-token")
                .refreshToken("new-refresh-token")
                .build();
        given(userService.refreshToken(any(UserDto.RefreshTokenRequest.class))).willReturn(response);

        mockMvc.perform(post("/api/users/refresh")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new-access-token"))
                .andExpect(jsonPath("$.refreshToken").value("new-refresh-token"));
    }

    @Test
    @DisplayName("내 정보 조회 성공")
    @WithMockUser(username = "test@example.com")
    void getUserInfo_success() throws Exception {
        UserDto.UserInfo response = UserDto.UserInfo.builder()
                .id(1L)
                .email("test@example.com")
                .name("testuser")
                .role("USER")
                .build();
        given(userService.getUserInfo("test@example.com")).willReturn(response);

        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @DisplayName("관리자 - 사용자 목록 조회 성공")
    @WithMockUser(roles = "ADMIN")
    void getUserList_success() throws Exception {
        UserDto.UserListItem userListItem = UserDto.UserListItem.builder().id(1L).email("test@example.com").build();
        Page<UserDto.UserListItem> response = new PageImpl<>(Collections.singletonList(userListItem));
        given(userService.getUserList(any(Pageable.class))).willReturn(response);

        mockMvc.perform(get("/api/admin/users")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].email").value("test@example.com"));
    }

    @Test
    @DisplayName("관리자 - 사용자 목록 조회 실패 (권한 없음)")
    @WithMockUser(roles = "USER")
    void getUserList_fail_forbidden() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("루트 관리자 - 사용자 역할 변경 성공")
    @WithMockUser(roles = "ROOT")
    void updateUserRole_success() throws Exception {
        Long userId = 1L;
        UserDto.RoleUpdateRequest request = UserDto.RoleUpdateRequest.builder().role(User.Role.ADMIN).build();
        UserDto.MessageResponse response = UserDto.MessageResponse.builder().message("수정 완료").build();
        given(userService.updateUserRole(userId, request)).willReturn(response);

        mockMvc.perform(patch("/api/admin/users/{userId}/role", userId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("수정 완료"));
    }

    @Test
    @DisplayName("루트 관리자 - 사용자 역할 변경 실패 (권한 없음)")
    @WithMockUser(roles = "ADMIN")
    void updateUserRole_fail_forbidden() throws Exception {
        Long userId = 1L;
        UserDto.RoleUpdateRequest request = UserDto.RoleUpdateRequest.builder().role(User.Role.ADMIN).build();

        mockMvc.perform(patch("/api/admin/users/{userId}/role", userId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("관리자 - 사용자 활성화 성공")
    @WithMockUser(roles = "ADMIN")
    void activateUser_success() throws Exception {
        Long userId = 1L;
        UserDto.MessageResponse response = UserDto.MessageResponse.builder().message("사용자 활성화 완료").build();
        given(userService.activateUser(userId)).willReturn(response);

        mockMvc.perform(patch("/api/admin/users/{userId}/activate", userId).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("사용자 활성화 완료"));
    }

    @Test
    @DisplayName("관리자 - 사용자 비활성화 성공")
    @WithMockUser(roles = "ADMIN")
    void inActivateUser_success() throws Exception {
        Long userId = 1L;
        UserDto.MessageResponse response = UserDto.MessageResponse.builder().message("사용자 비활성화 완료").build();
        given(userService.inActivateUser(userId)).willReturn(response);

        mockMvc.perform(patch("/api/admin/users/{userId}/inactivate", userId).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("사용자 비활성화 완료"));
    }

    @Test
    @DisplayName("사용자 비밀번호 변경 성공")
    @WithMockUser(username = "test@example.com")
    void changePassword_success() throws Exception {
        UserDto.PasswordChangeRequest request = new UserDto.PasswordChangeRequest();
        request.setCurrentPassword("current-password");
        request.setNewPassword("new-password");
        UserDto.MessageResponse response = UserDto.MessageResponse.builder().message("비밀번호 변경 완료").build();
        given(userService.changePassword("test@example.com", request)).willReturn(response);

        mockMvc.perform(patch("/api/users/password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("비밀번호 변경 완료"));
    }

    @Test
    @DisplayName("관리자 - 사용자 삭제 성공")
    @WithMockUser(roles = "ADMIN")
    void deleteUser_success() throws Exception {
        Long userId = 1L;
        UserDto.MessageResponse response = UserDto.MessageResponse.builder().message("사용자 삭제 완료").build();
        given(userService.deleteUser(userId)).willReturn(response);

        mockMvc.perform(delete("/api/admin/users/{userId}", userId).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("사용자 삭제 완료"));
    }

    @Test
    @DisplayName("관리자 - 사용자 삭제 실패 (권한 없음)")
    @WithMockUser(roles = "USER")
    void deleteUser_fail_forbidden() throws Exception {
        Long userId = 1L;
        mockMvc.perform(delete("/api/admin/users/{userId}", userId).with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("이메일 사용 가능 여부 확인 - 사용 가능")
    void checkEmail_available() throws Exception {
        String email = "new@example.com";
        UserDto.EmailCheckResponse response = UserDto.EmailCheckResponse.available();
        given(userService.checkEmailAvailability(email)).willReturn(response);

        mockMvc.perform(get("/api/users/check-email").param("email", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isAvailable").value(true));
    }

    @Test
    @DisplayName("이메일 사용 가능 여부 확인 - 사용 불가")
    void checkEmail_notAvailable() throws Exception {
        String email = "test@example.com";
        UserDto.EmailCheckResponse response = UserDto.EmailCheckResponse.notAvailable();
        given(userService.checkEmailAvailability(email)).willReturn(response);

        mockMvc.perform(get("/api/users/check-email").param("email", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isAvailable").value(false));
    }
}
