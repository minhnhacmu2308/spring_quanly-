package com.quan_ly.spring.utils;

import com.quan_ly.spring.constants.CommonConstant;
import com.quan_ly.spring.models.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Objects;
@Component
public class AuthMiddleware implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute(CommonConstant.SESSION_USER);
        // Kiểm tra trạng thái đăng nhập của người dùng
        if (!isUserLoggedIn(request)) {
            // Chuyển hướng người dùng đến trang đăng nhập hoặc trả về lỗi 401 Unauthorized
            response.sendRedirect("/login");
            return false; // Chặn xử lý request tiếp theo
        }
        return true; // Cho phép xử lý request tiếp theo
    }

    private boolean isUserLoggedIn(HttpServletRequest request) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute(CommonConstant.SESSION_USER);
        if (Objects.nonNull(user)) {
            return true;
        }
        return  false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        // Code xử lý sau khi request đã được xử lý bởi Controller, trước khi trả về response
    }

}