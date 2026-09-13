package com.example.Restaurant.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class FilterAspect {

    @PersistenceContext
    private EntityManager entityManager;

    // Chạy hàm này TRƯỚC mọi hàm nằm trong package "service"
    @Before("execution(* com.example.Restaurant.service.*.*(..))")
    public void enableBranchFilter() {
        String role = TenantContext.getCurrentUserRole();

        // 1. Nếu là Tổng quản lý (ADMIN), lập tức thoát ra, KHÔNG bật Filter.
        // Tài khoản này sẽ có quyền truy vấn toàn bộ dữ liệu (FindAll sẽ ra mọi chi nhánh).
        if ("ADMIN".equals(role)) {
            return;
        }

        // 2. Nếu là Nhân viên (STAFF), tiến hành khóa truy vấn theo branchId
        Long branchId = TenantContext.getCurrentBranch();
        if (branchId != null) {
            Session session = entityManager.unwrap(Session.class);
            session.enableFilter("branchFilter").setParameter("branchIdParam", branchId);
        }
    }
}