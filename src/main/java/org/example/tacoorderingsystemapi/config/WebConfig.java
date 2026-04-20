package org.example.tacoorderingsystemapi.config;

import org.example.tacoorderingsystemapi.interceptor.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/c/**") // 拦截所有 C 端请求
                .excludePathPatterns("/api/c/auth/login") // 放行登录接口
                .excludePathPatterns("/api/c/product/menu"); // 放行菜单接口 (用户没登录也能看菜单)
    }
}