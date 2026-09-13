//package com.example.Restaurant.config;
//
//import com.example.Restaurant.security.JwtUtils;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.stereotype.Component;
//import org.springframework.web.servlet.HandlerInterceptor;
//
//@Component
//public class BranchInterceptor implements HandlerInterceptor {
//
//    private final JwtUtils jwtUtils;
//
//    public BranchInterceptor(JwtUtils jwtUtils) {
//        this.jwtUtils = jwtUtils;
//    }
//
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
//        // Lấy token từ header Authorization (Định dạng: Bearer <token>)
//        String authHeader = request.getHeader("Authorization");
//
//        if (authHeader != null && authHeader.startsWith("Bearer ")) {
//            String token = authHeader.substring(7); // Cắt chữ "Bearer " đi
//            try {
//                // Giải mã lấy branchId và role
//                Long branchId = jwtUtils.getBranchIdFromToken(token);
//                String role = jwtUtils.getRoleFromToken(token);
//
//                // Lưu vào Context
//                TenantContext.setCurrentBranch(branchId);
//                TenantContext.setCurrentUserRole(role);
//            } catch (Exception e) {
//                // Token hết hạn hoặc sai chữ ký
//                System.out.println("Token không hợp lệ: " + e.getMessage());
//            }
//        }
//        return true;
//    }
//
//    @Override
//    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
//        TenantContext.clear();
//    }
//}