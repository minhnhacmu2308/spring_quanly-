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
}
