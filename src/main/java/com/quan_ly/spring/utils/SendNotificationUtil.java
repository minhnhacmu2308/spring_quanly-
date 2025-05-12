package com.quan_ly.spring.utils;

import com.quan_ly.spring.enums.Priority;
import com.quan_ly.spring.enums.Severity;
import com.quan_ly.spring.models.Notification;
import com.quan_ly.spring.models.Project;
import com.quan_ly.spring.models.Risk;
import com.quan_ly.spring.models.User;
import com.quan_ly.spring.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class SendNotificationUtil {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    NotificationService notificationService;
    public void sendNotificationToUser(String email, Notification notification) {
        // Gửi thông báo đến user qua WebSocket
        String destination = "/topic/notifications/"+email;

        messagingTemplate.convertAndSend(destination, notification);
    }

    public static String buildProjectNotificationContent(Project project, String role) {
        return String.format(
                """
                Project Name: %s
                Project Manager: %s 
                %s
                """,
                project.getProjectName(),
                project.getManager().getFullName(),
                role
        );
    }

    public void sendNotificationToAssignee(User user, String role, Project project) {
        if (user == null) return;

        Notification notification = new Notification();
        notification.setTitle("You have been assigned to a new project");
        notification.setContent(SendNotificationUtil.buildProjectNotificationContent(project, role));
        notification.setUser(user);
        notification.setPriority(Priority.LOW);
        notification.setIsRead(false);

        // Optional: Save notification to the database if needed
        notificationService.saveNotification(notification);

        sendNotificationToUser(user.getEmail(), notification);
    }

    public static String buildRiskNotificationContent(Risk risk, String title) {
        return String.format(
                """
                %s
        
                Project: %s
                Reported By: %s
                Severity: %s
                Status: %s
        
                Information:
                %s
                """,
                title,
                risk.getProject().getProjectName(),
                risk.getReportedBy().getFullName(),
                risk.getSeverity(),
                risk.getStatus(),
                risk.getInformation()
        );
    }


    public void sendRiskNotificationToManager(Risk risk) {
        if (risk == null || risk.getProject() == null || risk.getProject().getManager() == null) return;

        User manager = risk.getProject().getManager();
        Priority priority = Priority.valueOf(risk.getSeverity().name());

        Notification notification = new Notification();
        notification.setTitle("New Risk Reported in Project");
        notification.setContent(SendNotificationUtil.buildRiskNotificationContent(risk,"A new risk has been reported."));
        notification.setUser(manager);
        notification.setPriority(priority); // Rủi ro thường ưu tiên cao
        notification.setIsRead(false);

        notificationService.saveNotification(notification);

        sendNotificationToUser(manager.getEmail(), notification);
    }

    public void sendStatusRiskNotificationToCreater(Risk risk) {
        if (risk == null || risk.getProject() == null || risk.getProject().getManager() == null) return;

        User reporter = risk.getReportedBy();

        Notification notification = new Notification();
        notification.setTitle("Project risk has been approved");
        notification.setContent(SendNotificationUtil.buildRiskNotificationContent(risk,"Project risk has been approved."));
        notification.setUser(reporter);
        notification.setPriority(Priority.MEDIUM); // Rủi ro thường ưu tiên c
        notification.setIsRead(false);

        notificationService.saveNotification(notification);

        sendNotificationToUser(reporter.getEmail(), notification);
    }
}
