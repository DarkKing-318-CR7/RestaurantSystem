package com.example.Restaurant.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final BranchInterceptor branchInterceptor;

    public WebConfig(BranchInterceptor branchInterceptor) {
        this.branchInterceptor = branchInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Áp dụng Interceptor cho toàn bộ các API bắt đầu bằng /api/
        registry.addInterceptor(branchInterceptor).addPathPatterns("/api/**");
    }
}