package com.investflow.user.service;

import com.investflow.user.dto.AuthResponse;
import com.investflow.user.dto.LoginRequest;
import com.investflow.user.dto.RegisterRequest;
import com.investflow.user.exception.BadRequestException;
import com.investflow.user.model.Role;
import com.investflow.user.model.User;
import com.investflow.user.repository.RefreshTokenRepository;
import com.investflow.user.repository.RoleRepository;
import com.investflow.user.repository.UserRepository;
import com.investflow.user.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "refreshExpirationMs", 604800000L);
    }

    @Test
    void register_ShouldSucceed_WhenEmailIsUnique() {
        RegisterRequest request = RegisterRequest.builder()
                .email("test@example.com")
                .password("Password123!")
                .firstName("Test")
                .lastName("User")
                .build();

        Role role = Role.builder().id(1L).name("ROLE_USER").build();
        User savedUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .roles(new HashSet<>(Set.of(role)))
                .build();

        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(any())).thenReturn("hashed_password");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtTokenProvider.generateAccessToken(any(User.class))).thenReturn("mock_jwt_access_token");

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("mock_jwt_access_token", response.getAccessToken());
        assertEquals("test@example.com", response.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_ShouldThrowException_WhenEmailAlreadyExists() {
        RegisterRequest request = RegisterRequest.builder()
                .email("test@example.com")
                .password("Password123!")
                .firstName("Test")
                .lastName("User")
                .build();

        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_ShouldSucceed_WhenCredentialsValid() {
        LoginRequest request = LoginRequest.builder()
                .email("test@example.com")
                .password("Password123!")
                .build();

        Role role = Role.builder().id(1L).name("ROLE_USER").build();
        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .roles(new HashSet<>(Set.of(role)))
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(jwtTokenProvider.generateAccessToken(any(User.class))).thenReturn("mock_jwt_access_token");

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("mock_jwt_access_token", response.getAccessToken());
        assertEquals("test@example.com", response.getEmail());
    }
}
