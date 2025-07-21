package com.example.user.service;

import com.example.user.dto.UserDto;
import com.example.user.domain.User;
import com.example.user.exception.BusinessException;
import com.example.user.exception.ErrorCode;
import com.example.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Transactional
    public UserDto.SignupResponse signup(UserDto.SignupRequest request) {
        // 이메일 중복 확인
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }

        // 약관 동의 확인
        if (!request.getAgreeTerms()) {
            throw new BusinessException(ErrorCode.TERMS_NOT_AGREED);
        }

        // 사용자 생성
        User user = User.builder()
                .email(request.getEmail())
                .name(request.getName())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(User.Role.USER)
                .isTermsAgreed(request.getAgreeTerms())
                .isActive(false) // 기본값은 false로 설정
                .build();



        User savedUser = userRepository.save(user);

        return UserDto.SignupResponse.builder()
                .id(savedUser.getUserId())
                .email(savedUser.getEmail())
                .build();
    }

    public UserDto.LoginResponse login(UserDto.LoginRequest request) {
        // 인증 처리
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user =userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if(!user.getIsActive()) {
            throw new BusinessException(ErrorCode.USER_NOT_ACTIVE);
        }

        // JWT 토큰 생성
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = jwtUtil.generateToken(userDetails);

        return UserDto.LoginResponse.builder()
                .token(token)
                .build();
    }

    public UserDto.MessageResponse logout() {
        // JWT는 stateless이므로 서버에서 특별한 처리 없음
        // 클라이언트에서 토큰 삭제 처리
        return UserDto.MessageResponse.builder()
                .message("로그아웃 완료")
                .build();
    }

    @Transactional(readOnly = true)
    public UserDto.UserInfo getUserInfo(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return UserDto.UserInfo.builder()
                .id(user.getUserId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .build();
    }

    @Transactional(readOnly = true)
    public Page<UserDto.UserListItem> getUserList(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(user -> UserDto.UserListItem.builder()
                        .id(user.getUserId())
                        .email(user.getEmail())
                        .build());
    }

    @Transactional
    public UserDto.MessageResponse updateUserRole(Integer userId, UserDto.RoleUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        user.updateRole(request.getRole());

        return UserDto.MessageResponse.builder()
                .message("수정 완료")
                .build();
    }

    @Transactional
    public UserDto.MessageResponse activateUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        user.setIsActive(true); // 활성화 상태로 변경

        return UserDto.MessageResponse.builder()
                .message("사용자 활성화 완료")
                .build();
    }

    @Transactional
    public UserDto.MessageResponse inActivateUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        user.setIsActive(false); // 활성화 상태로 변경

        return UserDto.MessageResponse.builder()
                .message("사용자 비활성화 완료")
                .build();
    }

    @Transactional
    public UserDto.MessageResponse deleteUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        userRepository.delete(user); // 사용자 삭제

        return UserDto.MessageResponse.builder()
                .message("사용자 삭제 완료")
                .build();
    }

    @Transactional
    public UserDto.MessageResponse changePassword(String email, UserDto.PasswordChangeRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));

        return UserDto.MessageResponse.builder()
                .message("비밀번호 변경 완료")
                .build();
    }
}

