package com.quan_ly.spring.controllers;

import com.quan_ly.spring.enums.Priority;
import com.quan_ly.spring.models.Notification;
import com.quan_ly.spring.models.Project;
import com.quan_ly.spring.models.User;
import com.quan_ly.spring.services.NotificationService;
import com.quan_ly.spring.services.ProjectService;
import com.quan_ly.spring.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/user/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;
    @Autowired
    ProjectService projectService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    public NotificationController(NotificationService notificationService, UserService userService) {
        this.notificationService = notificationService;
        this.userService = userService;
    }

    // Danh sách thông báo
    @GetMapping
    public String listNotifications(Model model) {
        model.addAttribute("notifications", notificationService.getAllNotifications());
        return "public/notification";
    }


    // Xử lý thêm mới
    @PostMapping("/add/{projectId}")
    public String addNotification(@ModelAttribute Notification notification, @PathVariable Long projectId) {
        Optional<Project> project = projectService.getProjectById(projectId);
        String email = project.get().getManager().getEmail();
        // Gửi thông báo đến user qua WebSocket
        String destination = "/topic/notifications/"+email;

        messagingTemplate.convertAndSend(destination, "message");
        notificationService.saveNotification(notification);
        return "redirect:/user/notifications";
    }


    // Xử lý cập nhật
    @PostMapping("/edit/{id}")
    public String updateNotification(@PathVariable Long id, @ModelAttribute Notification notification) {
        notification.setNotificationId(id); // đảm bảo ID đúng
        notificationService.saveNotification(notification);
        return "redirect:/user/notifications";
    }

    // Xóa
    @GetMapping("/delete/{id}")
    public String deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return "redirect:/user/notifications";
    }
}
