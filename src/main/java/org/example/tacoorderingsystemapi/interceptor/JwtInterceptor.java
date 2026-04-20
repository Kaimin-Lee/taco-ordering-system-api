package org.example.tacoorderingsystemapi.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.tacoorderingsystemapi.util.JwtUtil;
import org.example.tacoorderingsystemapi.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 从 HTTP Header 中获取 Token (规范格式: Authorization: Bearer <token>)
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // 截取掉 "Bearer "
            try {
                // 2. 解析 Token 获取用户 ID
                Long userId = jwtUtil.getUserId(token);
                // 3. 存入 ThreadLocal
                UserContext.setUserId(userId);
                return true; // 放行
            } catch (Exception e) {
                // Token 过期或伪造
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }
        }

        // 没带 Token，直接拒绝
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return false;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 请求处理完毕后，务必清理 ThreadLocal，防止内存泄漏和串号问题
        UserContext.clear();
    }
}