package com.quan_ly.spring.controllers;

import com.quan_ly.spring.models.Notification;
import com.quan_ly.spring.models.User;
import com.quan_ly.spring.services.NotificationService;
import com.quan_ly.spring.services.ProjectService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

@Controller
public class HomeController {
    @Autowired
    NotificationService notificationService;

    @GetMapping({"/", "/user/home"})
    public ModelAndView index(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        List<Notification> notifications = notificationService.getNotificationsByUser(user);

        if (user != null) {
            // Kiểm tra có thông báo chưa đọc không
            boolean hasUnread = notifications.stream().anyMatch(n -> !Boolean.TRUE.equals(n.getIsRead()));
            model.addAttribute("hasUnread", hasUnread);
            return new ModelAndView("public/home");
        }
        return new ModelAndView(new RedirectView("/auth/login"));
    }
}
