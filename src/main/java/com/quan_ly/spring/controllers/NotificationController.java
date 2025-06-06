package com.quan_ly.spring.controllers;

import com.quan_ly.spring.constants.CommonConstant;
import com.quan_ly.spring.enums.Priority;
import com.quan_ly.spring.models.Notification;
import com.quan_ly.spring.models.Project;
import com.quan_ly.spring.models.User;
import com.quan_ly.spring.services.NotificationService;
import com.quan_ly.spring.services.ProjectService;
import com.quan_ly.spring.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.util.stream.Collectors;

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
    private MessageSource messageSource;

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

    // Lấy danh sách thông báo theo userId
    @GetMapping("/all")
    public String getNotificationsByUserId(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        Optional<User> userOptional = userService.getUserById(user.getUserId());

        if (userOptional.isPresent()) {
            List<Notification> notifications = notificationService.getNotificationsByUser(userOptional.get());

            // Sắp xếp theo priority: HIGH > MEDIUM > LOW
            notifications.sort(Comparator.comparingInt(n -> {
                Priority p = n.getPriority();
                if (p == Priority.HIGH) return 0;
                if (p == Priority.MEDIUM) return 1;
                return 2; // LOW
            }));

            model.addAttribute("notifications", notifications);
            return "public/notification";
        }

        return "public/notification";
    }

    @GetMapping("/{userId}")
    @ResponseBody
    public List<Notification> getNotificationsByUserIdTop10(@PathVariable Long userId) {
        Optional<User> userOptional = userService.getUserById(userId);
        if (userOptional.isPresent()) {
            List<Notification> notifications = notificationService.getNotificationsByUser(userOptional.get());
            return notifications.stream()
                    .sorted(Comparator.comparing(Notification::getSentAt).reversed()) // Sắp xếp giảm dần theo sentAt
                    .limit(10)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @GetMapping("/detail/{id}")
    public String viewNotificationDetail(@PathVariable Long id, Model model) {
        Optional<Notification> optionalNotification = notificationService.getNotificationById(id);
        if (optionalNotification.isPresent()) {
            Notification notification = optionalNotification.get();

            // Nếu chưa đọc thì đánh dấu là đã đọc
            if (!notification.getIsRead()) {
                notification.setIsRead(true);
                notificationService.saveNotification(notification); // Cập nhật lại DB
            }

            model.addAttribute("notification", notification);
            return "public/notification-detail";
        }
        return "redirect:/user/notifications";
    }



    // Xử lý thêm mới
    @PostMapping("/add/{projectId}")
    public String addNotification(@ModelAttribute Notification notification, @PathVariable Long projectId, RedirectAttributes redirectAttributes) {
        Optional<Project> project = projectService.getProjectById(projectId);
        notification.setUser(project.get().getManager());
        String email = project.get().getManager().getEmail();
        // Gửi thông báo đến user qua WebSocket
        String destination = "/topic/notifications/"+email;

        messagingTemplate.convertAndSend(destination, notification);
        notificationService.saveNotification(notification);
        redirectAttributes.addFlashAttribute(CommonConstant.SUCCESS_MESSAGE,
                messageSource.getMessage("notification_success", null, Locale.getDefault()));
        return "redirect:/risk/home";
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
