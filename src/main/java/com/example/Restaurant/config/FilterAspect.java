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
        Long branchId = TenantContext.getCurrentBranch();
        if (branchId != null) {
            // Lấy Hibernate Session từ EntityManager và bật Filter
            Session session = entityManager.unwrap(Session.class);
            session.enableFilter("branchFilter").setParameter("branchIdParam", branchId);
        }
    }
}