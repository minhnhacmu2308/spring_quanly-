package com.quan_ly.spring.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);

        // Kiểm tra xem user đã đăng nhập chưa
        if (session != null && session.getAttribute("user") != null) {
            return true; // Cho phép đi tiếp
        }

        // Nếu chưa login thì chuyển hướng đến trang login
        response.sendRedirect("/auth/login");
        return false;
    }
}