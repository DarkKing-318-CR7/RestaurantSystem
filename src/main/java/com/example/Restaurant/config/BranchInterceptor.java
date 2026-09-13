package com.example.Restaurant.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class BranchInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Trích xuất branchId từ Header của API gửi lên
        String branchId = request.getHeader("X-Branch-Id");
        if (branchId != null) {
            TenantContext.setCurrentBranch(Long.valueOf(branchId));
        }
        return true; // Cho phép đi tiếp vào Controller
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // Dọn dẹp bộ nhớ sau khi API trả kết quả xong để tránh tràn RAM
        TenantContext.clear();
    }

}
