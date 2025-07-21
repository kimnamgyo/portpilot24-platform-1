package com.example.user.service;


import com.example.user.dto.UserDto;
import com.example.user.domain.User;
import com.example.user.exception.BusinessException;
import com.example.user.exception.ErrorCode;
import com.example.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserDto.SignupRequest signupRequest;
    private User user;

    @BeforeEach
    void setUp() {
        signupRequest = UserDto.SignupRequest.builder()
                .email("test@example.com")
                .name("테스트유저")
                .password("1234")
                .agreeTerms(true)
                .build();

        user = User.builder()
                .userId(1L)
                .email("test@example.com")
                .name("테스트유저")
                .passwordHash("hashedPassword")
                .role(User.Role.USER)
                .isTermsAgreed(true)
                .build();
    }

    @Test
    void 회원가입_성공() {
        // given
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // when
        UserDto.SignupResponse response = userService.signup(signupRequest);

        // then
        assertThat(response.getId()).isEqualTo(1);
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void 회원가입_이메일_중복_실패() {
        // given
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.signup(signupRequest))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATE_EMAIL);
    }

    @Test
    void 회원가입_약관_미동의_실패() {
        // given
        signupRequest = UserDto.SignupRequest.builder()
                .email("test@example.com")
                .name("테스트유저")
                .password("1234")
                .agreeTerms(false)
                .build();

        // when & then
        assertThatThrownBy(() -> userService.signup(signupRequest))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.TERMS_NOT_AGREED);
    }

    @Test
    void 사용자_정보_조회_성공() {
        // given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        // when
        UserDto.UserInfo userInfo = userService.getUserInfo("test@example.com");

        // then
        assertThat(userInfo.getId()).isEqualTo(1);
        assertThat(userInfo.getEmail()).isEqualTo("test@example.com");
        assertThat(userInfo.getName()).isEqualTo("테스트유저");
        assertThat(userInfo.getRole()).isEqualTo(User.Role.USER);
    }

    @Test
    void 사용자_정보_조회_실패() {
        // given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.getUserInfo("test@example.com"))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
    }
}