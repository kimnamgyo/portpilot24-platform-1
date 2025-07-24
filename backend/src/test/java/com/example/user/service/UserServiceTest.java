package com.example.user.service;

import com.example.user.domain.User;
import com.example.user.dto.UserDto;
import com.example.user.exception.BusinessException;
import com.example.user.exception.ErrorCode;
import com.example.user.repository.UserRepository;
import jakarta.persistence.OptimisticLockException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserDetailsService userDetailsService;

    private User user;
    private UserDto.SignupRequest signupRequest;
    private UserDto.LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .userId(1L)
                .email("test@example.com")
                .passwordHash("encodedPassword")
                .name("testuser")
                .role(User.Role.USER)
                .isActive(true)
                .isTermsAgreed(true)
                .refreshToken("some-refresh-token")
                .build();

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
    void signup_success() {
        // given
        given(userRepository.existsByEmail(anyString())).willReturn(false);
        given(passwordEncoder.encode(anyString())).willReturn("encodedPassword");
        given(userRepository.save(any(User.class))).willReturn(user);

        // when
        UserDto.SignupResponse response = userService.signup(signupRequest);

        // then
        assertThat(response.getEmail()).isEqualTo(signupRequest.getEmail());
        verify(userRepository).existsByEmail(signupRequest.getEmail());
        verify(passwordEncoder).encode(signupRequest.getPassword());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 중복")
    void signup_fail_duplicateEmail() {
        // given
        given(userRepository.existsByEmail(anyString())).willReturn(true);

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> userService.signup(signupRequest));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.DUPLICATE_EMAIL);
    }

    @Test
    @DisplayName("회원가입 실패 - 약관 미동의")
    void signup_fail_termsNotAgreed() {
        // given
        signupRequest.setAgreeTerms(false);

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> userService.signup(signupRequest));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.TERMS_NOT_AGREED);
    }

    @Test
    @DisplayName("회원가입 실패 - DB 저장 시 동시성 문제")
    void signup_fail_concurrencyIssue() {
        // given
        given(userRepository.existsByEmail(anyString())).willReturn(false);
        given(passwordEncoder.encode(anyString())).willReturn("encodedPassword");
        given(userRepository.save(any(User.class))).willThrow(DataIntegrityViolationException.class);

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> userService.signup(signupRequest));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.DUPLICATE_EMAIL);
    }

    @Test
    @DisplayName("로그인 성공")
    void login_success() {
        // given
        UserDetails userDetails = mock(UserDetails.class);
        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).willReturn(null);
        given(userRepository.findByEmail(anyString())).willReturn(Optional.of(user));
        given(userDetailsService.loadUserByUsername(anyString())).willReturn(userDetails);
        given(jwtUtil.generateToken(any(UserDetails.class))).willReturn("access-token");
        given(jwtUtil.generateRefreshToken(any(UserDetails.class))).willReturn("refresh-token");

        // when
        UserDto.LoginResponse response = userService.login(loginRequest);

        // then
        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("로그인 실패 - 비활성화된 사용자")
    void login_fail_userNotActive() {
        // given
        user.setIsActive(false);
        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).willReturn(null);
        given(userRepository.findByEmail(anyString())).willReturn(Optional.of(user));

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> userService.login(loginRequest));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_ACTIVE);
    }

    @Test
    @DisplayName("토큰 재발급 성공")
    void refreshToken_success() {
        // given
        UserDto.RefreshTokenRequest request = UserDto.RefreshTokenRequest.builder().refreshToken("some-refresh-token").build();
        UserDetails userDetails = mock(UserDetails.class);
        given(jwtUtil.validateToken(request.getRefreshToken())).willReturn(true);
        given(jwtUtil.getUsernameFromToken(request.getRefreshToken())).willReturn(user.getEmail());
        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));
        given(userDetailsService.loadUserByUsername(user.getEmail())).willReturn(userDetails);
        given(jwtUtil.generateToken(userDetails)).willReturn("new-access-token");
        given(jwtUtil.generateRefreshToken(userDetails)).willReturn("new-refresh-token");

        // when
        UserDto.LoginResponse response = userService.refreshToken(request);

        // then
        assertThat(response.getAccessToken()).isEqualTo("new-access-token");
        assertThat(response.getRefreshToken()).isEqualTo("new-refresh-token");
        assertThat(user.getRefreshToken()).isEqualTo("new-refresh-token"); // verify() 대신 assertThat() 사용
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("토큰 재발급 실패 - 유효하지 않은 토큰")
    void refreshToken_fail_invalidToken() {
        // given
        UserDto.RefreshTokenRequest request = UserDto.RefreshTokenRequest.builder().refreshToken("invalid-token").build();
        given(jwtUtil.validateToken(request.getRefreshToken())).willReturn(false);

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> userService.refreshToken(request));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_TOKEN);
    }

    @Test
    @DisplayName("토큰 재발급 실패 - 저장된 토큰과 불일치")
    void refreshToken_fail_tokenMismatch() {
        // given
        UserDto.RefreshTokenRequest request = UserDto.RefreshTokenRequest.builder().refreshToken("different-token").build();
        given(jwtUtil.validateToken(request.getRefreshToken())).willReturn(true);
        given(jwtUtil.getUsernameFromToken(request.getRefreshToken())).willReturn(user.getEmail());
        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> userService.refreshToken(request));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_TOKEN);
    }

    @Test
    @DisplayName("토큰 재발급 실패 - 동시성 문제")
    void refreshToken_fail_concurrency() {
        // given
        UserDto.RefreshTokenRequest request = UserDto.RefreshTokenRequest.builder().refreshToken("some-refresh-token").build();
        UserDetails userDetails = mock(UserDetails.class);
        given(jwtUtil.validateToken(request.getRefreshToken())).willReturn(true);
        given(jwtUtil.getUsernameFromToken(request.getRefreshToken())).willReturn(user.getEmail());
        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));
        given(userDetailsService.loadUserByUsername(user.getEmail())).willReturn(userDetails);
        given(jwtUtil.generateToken(userDetails)).willReturn("new-access-token");
        given(jwtUtil.generateRefreshToken(userDetails)).willReturn("new-refresh-token");
        doThrow(OptimisticLockException.class).when(userRepository).save(any(User.class));

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> userService.refreshToken(request));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.CONCURRENT_REQUEST);
    }

    @Test
    @DisplayName("로그아웃 성공")
    void logout_success() {
        // given
        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));

        // when
        UserDto.MessageResponse response = userService.logout(user.getEmail());

        // then
        assertThat(response.getMessage()).isEqualTo("로그아웃 완료");
        assertThat(user.getRefreshToken()).isNull(); // verify() 대신 assertThat() 사용
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("내 정보 조회 성공")
    void getUserInfo_success() {
        // given
        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));

        // when
        UserDto.UserInfo userInfo = userService.getUserInfo(user.getEmail());

        // then
        assertThat(userInfo.getEmail()).isEqualTo(user.getEmail());
        assertThat(userInfo.getName()).isEqualTo(user.getName());
    }

    @Test
    @DisplayName("사용자 목록 조회 성공")
    void getUserList_success() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(Collections.singletonList(user), pageable, 1);
        given(userRepository.findAll(pageable)).willReturn(userPage);

        // when
        Page<UserDto.UserListItem> result = userService.getUserList(pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    @DisplayName("사용자 역할 변경 성공")
    void updateUserRole_success() {
        // given
        Long userId = 1L;
        UserDto.RoleUpdateRequest request = UserDto.RoleUpdateRequest.builder().role(User.Role.ADMIN).build();
        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        // when
        UserDto.MessageResponse response = userService.updateUserRole(userId, request);

        // then
        assertThat(response.getMessage()).isEqualTo("수정 완료");
        assertThat(user.getRole()).isEqualTo(User.Role.ADMIN); // verify() 대신 assertThat() 사용
    }

    @Test
    @DisplayName("사용자 활성화/비활성화/삭제 성공")
    void userStatusChange_success() {
        // Activate
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        userService.activateUser(1L);
        assertThat(user.getIsActive()).isTrue(); // verify() 대신 assertThat() 사용

        // Inactivate
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        userService.inActivateUser(1L);
        assertThat(user.getIsActive()).isFalse(); // verify() 대신 assertThat() 사용

        // Delete
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        userService.deleteUser(1L);
        verify(userRepository).delete(user);
    }

    @Test
    @DisplayName("사용자 상태 변경 실패 - 사용자를 찾을 수 없음")
    void userStatusChange_fail_userNotFound() {
        // given
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());
        UserDto.RoleUpdateRequest request = UserDto.RoleUpdateRequest.builder().role(User.Role.ADMIN).build();

        // when & then
        assertThrows(BusinessException.class, () -> userService.updateUserRole(1L, request));
        assertThrows(BusinessException.class, () -> userService.activateUser(1L));
        assertThrows(BusinessException.class, () -> userService.inActivateUser(1L));
        assertThrows(BusinessException.class, () -> userService.deleteUser(1L));
    }

    @Test
    @DisplayName("비밀번호 변경 성공")
    void changePassword_success() {
        // given
        UserDto.PasswordChangeRequest request = new UserDto.PasswordChangeRequest();
        request.setCurrentPassword("password");
        request.setNewPassword("newPassword");
        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())).willReturn(true);
        given(passwordEncoder.encode(request.getNewPassword())).willReturn("newEncodedPassword");

        // when
        UserDto.MessageResponse response = userService.changePassword(user.getEmail(), request);

        // then
        assertThat(response.getMessage()).isEqualTo("비밀번호 변경 완료");
        assertThat(user.getPasswordHash()).isEqualTo("newEncodedPassword"); // verify() 대신 assertThat() 사용
    }

    @Test
    @DisplayName("비밀번호 변경 실패 - 현재 비밀번호 불일치")
    void changePassword_fail_invalidPassword() {
        // given
        UserDto.PasswordChangeRequest request = new UserDto.PasswordChangeRequest();
        request.setCurrentPassword("wrongPassword");
        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())).willReturn(false);

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> userService.changePassword(user.getEmail(), request));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_PASSWORD);
    }

    @Test
    @DisplayName("이메일 사용 가능 여부 확인")
    void checkEmailAvailability() {
        // given
        given(userRepository.existsByEmail("available@example.com")).willReturn(false);
        given(userRepository.existsByEmail("taken@example.com")).willReturn(true);

        // when
        UserDto.EmailCheckResponse availableResponse = userService.checkEmailAvailability("available@example.com");
        UserDto.EmailCheckResponse takenResponse = userService.checkEmailAvailability("taken@example.com");

        // then
        assertThat(availableResponse.getIsAvailable()).isTrue();
        assertThat(takenResponse.getIsAvailable()).isFalse();

        // when & then - invalid input
        BusinessException exception = assertThrows(BusinessException.class, () -> userService.checkEmailAvailability(null));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_INPUT);
    }

    @Test
    @DisplayName("이메일로 사용자 조회 성공")
    void getUserByEmail_success() {
        // given
        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));

        // when
        User foundUser = userService.getUserByEmail(user.getEmail());

        // then
        assertThat(foundUser).isEqualTo(user);
    }

    @Test
    @DisplayName("이메일로 사용자 조회 실패 - 비활성화된 유저")
    void getUserByEmail_fail_notActive() {
        // given
        user.setIsActive(false);
        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> userService.getUserByEmail(user.getEmail()));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_ACTIVE);
    }
}
