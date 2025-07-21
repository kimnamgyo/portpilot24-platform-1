package com.example.user.controller;

import com.example.config.SecurityConfig;
import com.example.user.dto.UserDto;
import com.example.user.service.JwtAuthenticationEntryPoint;
import com.example.user.service.JwtUtil;
import com.example.user.service.UserDetailsServiceImpl;
import com.example.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
// 1. 실제 SecurityConfig와 함께 테스트 전용 Mock 객체 설정(TestConfig)을 함께 Import 합니다.
@Import({SecurityConfig.class, UserControllerTest.TestConfig.class})
class UserControllerTest {

    // 2. `@TestConfiguration`을 사용하여 Mock 객체를 Bean으로 등록합니다.
    @TestConfiguration
    static class TestConfig {
        @Bean
        public UserService userService() {
            return Mockito.mock(UserService.class);
        }

        @Bean
        public UserDetailsServiceImpl userDetailsService() {
            return Mockito.mock(UserDetailsServiceImpl.class);
        }

        @Bean
        public JwtUtil jwtUtil() {
            return Mockito.mock(JwtUtil.class);
        }

        @Bean
        public JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint() {
            return Mockito.mock(JwtAuthenticationEntryPoint.class);
        }
    }

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final UserService userService; // Mock 객체

    // 3. `@Autowired` 생성자를 통해 필요한 모든 의존성을 명시적으로 주입받습니다.
    @Autowired
    public UserControllerTest(MockMvc mockMvc, ObjectMapper objectMapper, UserService userService) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.userService = userService;
    }

    private UserDto.SignupRequest signupRequest;
    private UserDto.SignupResponse signupResponse;

    @BeforeEach
    void setUp() {
        signupRequest = UserDto.SignupRequest.builder()
                .email("test@example.com")
                .name("테스트유저")
                .password("1234")
                .agreeTerms(true)
                .build();

        signupResponse = UserDto.SignupResponse.builder()
                .id(1L)
                .email("test@example.com")
                .build();
    }

    @Test
    void 회원가입_성공() throws Exception {
        when(userService.signup(any(UserDto.SignupRequest.class)))
                .thenReturn(signupResponse);

        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void 로그아웃_성공() throws Exception {
        UserDto.MessageResponse response = UserDto.MessageResponse.builder()
                .message("로그아웃 완료")
                .build();

        when(userService.logout("test@example.com"))
                .thenReturn(response);

        mockMvc.perform(post("/api/users/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("로그아웃 완료"));
    }
}