package com.example.Restaurant.service;

import com.example.Restaurant.dto.LoginRequest;
import com.example.Restaurant.model.User;
import com.example.Restaurant.repository.UserRepository;
import com.example.Restaurant.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthService authService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new User();
        sampleUser.setId(1L);
        sampleUser.setUsername("staff1");
        sampleUser.setPassword("encoded_pass");
        sampleUser.setBranchId(1L);
        sampleUser.setRole("STAFF");
    }

    @Test
    void testLoginSuccess() {
        LoginRequest request = new LoginRequest();
        request.setUsername("staff1");
        request.setPassword("123456");

        when(userRepository.findByUsername("staff1")).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("123456", "encoded_pass")).thenReturn(true);
        when(jwtUtils.generateToken("staff1", 1L, "STAFF")).thenReturn("mocked_token");

        String token = authService.login(request);
        assertEquals("mocked_token", token);
    }

    @Test
    void testLoginUserNotFound() {
        LoginRequest request = new LoginRequest();
        request.setUsername("unknown");
        request.setPassword("123456");

        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.login(request));
        assertEquals("Không tìm thấy tài khoản!", exception.getMessage());
    }

    @Test
    void testLoginWrongPassword() {
        LoginRequest request = new LoginRequest();
        request.setUsername("staff1");
        request.setPassword("wrong");

        when(userRepository.findByUsername("staff1")).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("wrong", "encoded_pass")).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.login(request));
        assertEquals("Sai mật khẩu!", exception.getMessage());
    }
}
