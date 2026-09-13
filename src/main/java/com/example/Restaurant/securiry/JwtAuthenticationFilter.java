package com.example.Restaurant.securiry;

import com.example.Restaurant.config.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.example.Restaurant.security.JwtUtils;


import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    public JwtAuthenticationFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                // 1. Giải mã lấy dữ liệu
                Long branchId = jwtUtils.getBranchIdFromToken(token);
                String role = jwtUtils.getRoleFromToken(token);
                String username = jwtUtils.getUsernameFromToken(token);

                // 2. Lưu vào biến toàn cục cho Hibernate Filter sử dụng
                TenantContext.setCurrentBranch(branchId);
                TenantContext.setCurrentUserRole(role);

                // 3. Cấp thẻ xanh (Authentication) với Role cho Spring Security
                List<SimpleGrantedAuthority> authorities = (role != null && !role.isEmpty())
                        ? List.of(new SimpleGrantedAuthority("ROLE_" + role))
                        : Collections.emptyList();

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(username != null ? username : "authenticated_user", null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                System.out.println("Token lỗi: " + e.getMessage());
            }
        }

        try {
            // Cho phép request đi tiếp
            filterChain.doFilter(request, response);
        } finally {
            // Luôn luôn dọn dẹp RAM sau khi xử lý xong (tránh leak ThreadLocal)
            TenantContext.clear();
        }
    }
}