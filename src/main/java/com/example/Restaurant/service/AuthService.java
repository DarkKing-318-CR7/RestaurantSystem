package com.example.Restaurant.service;

import com.example.Restaurant.dto.LoginRequest;
import com.example.Restaurant.model.User;
import com.example.Restaurant.repository.UserRepository;
import com.example.Restaurant.security.JwtUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    public String login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản!"));

        // So sánh mật khẩu người dùng nhập với mật khẩu đã băm (hash) trong DB
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Sai mật khẩu!");
        }

        // Tạo Token chứa branchId và role
        return jwtUtils.generateToken(user.getUsername(), user.getBranchId(), user.getRole());
    }
}