package com.quan_ly.spring.utils;

import com.quan_ly.spring.models.Notification;
import com.quan_ly.spring.models.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class SendNotificationUtil {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    public void sendNotificationToUser(String email, Notification notification) {
        // Gửi thông báo đến user qua WebSocket
        String destination = "/topic/notifications/"+email;

        messagingTemplate.convertAndSend(destination, notification);
    }

    public static String buildProjectNotificationContent(Project project, String role) {
        StringBuilder sb = new StringBuilder();

        sb.append("A new project has been created!\n\n");
        sb.append("Project Name: ").append(project.getProjectName()).append("\n");
        sb.append("Project Manager: ").append(project.getManager().getFullName()).append("\n\n");
        sb.append("Budget: ").append(project.getBudget()).append(" VND\n");
        sb.append("Start Date: ").append(project.getStartDate()).append("\n");
        sb.append("Status: ").append(project.getStatus()).append("\n\n");
        sb.append("You have been assigned the role of: ").append(role).append(".\n");

        return sb.toString();
    }
}
