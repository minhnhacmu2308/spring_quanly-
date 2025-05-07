package com.quan_ly.spring.controllers;

import com.quan_ly.spring.models.Notification;
import com.quan_ly.spring.models.User;
import com.quan_ly.spring.services.NotificationService;
import com.quan_ly.spring.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice

public class GlobalModelAttribute {
    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    @ModelAttribute
    public void addGlobalAttributes(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            List<Notification> notifications = notificationService.getNotificationsByUser(user);
            boolean hasUnread = notifications.stream().anyMatch(n -> !Boolean.TRUE.equals(n.getIsRead()));
            model.addAttribute("notifications", notifications);
            model.addAttribute("hasUnread", hasUnread);
        }
    }
}
