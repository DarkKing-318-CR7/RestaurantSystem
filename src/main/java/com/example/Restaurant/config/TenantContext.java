package com.example.Restaurant.config;

public class TenantContext {
    private static final ThreadLocal<Long> currentBranch = new ThreadLocal<>();
    private static final ThreadLocal<String> currentUserRole = new ThreadLocal<>(); // Thêm biến lưu Role

    public static void setCurrentBranch(Long branchId) { currentBranch.set(branchId); }
    public static Long getCurrentBranch() { return currentBranch.get(); }

    public static void setCurrentUserRole(String role) { currentUserRole.set(role); } // Thêm Setter
    public static String getCurrentUserRole() { return currentUserRole.get(); } // Thêm Getter

    public static void clear() {
        currentBranch.remove();
        currentUserRole.remove(); // Nhớ xóa sạch khi xong request
    }
}