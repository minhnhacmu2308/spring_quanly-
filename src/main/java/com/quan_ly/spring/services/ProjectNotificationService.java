package com.quan_ly.spring.services;

import com.quan_ly.spring.enums.Priority;
import com.quan_ly.spring.models.Notification;
import com.quan_ly.spring.models.Project;
import com.quan_ly.spring.models.User;
import com.quan_ly.spring.utils.SendNotificationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectNotificationService {

  @Autowired
  private NotificationService notificationService;

  @Autowired
  private SendNotificationUtil sendNotificationUtil;

  public void notifyAssigneeChange(User oldUser, User newUser, String role, Project project) {
    // Handle removal notification
    if (oldUser != null && !oldUser.equals(newUser)) {
      sendRemovalNotification(oldUser, role, project);
    }

    // Handle new assignment notification
    if (newUser != null && !newUser.equals(oldUser)) {
      sendAssignmentNotification(newUser, role, project);
    }
  }

  public void notifyProjectDeletion(Project project) {
    notifyUserOfDeletion(project.getManager(), project);
    notifyUserOfDeletion(project.getPlanner(), project);
    notifyUserOfDeletion(project.getFieldStaff(), project);
    notifyUserOfDeletion(project.getAccountant(), project);
    notifyUserOfDeletion(project.getRiskSolver(), project);
  }

  private void sendRemovalNotification(User user, String role, Project project) {
    Notification notification = createNotification(
        "You have been removed from a project",
        String.format("You are no longer assigned to project \"%s\" as %s.",
            project.getProjectName(), role),
        Priority.LOW,
        user);
    saveAndSendNotification(notification);
  }

  private void sendAssignmentNotification(User user, String role, Project project) {
    Notification notification = createNotification(
        "You have been assigned to a project",
        SendNotificationUtil.buildProjectNotificationContent(project, role),
        Priority.LOW,
        user);
    saveAndSendNotification(notification);
  }

  private void notifyUserOfDeletion(User user, Project project) {
    if (user == null)
      return;

    Notification notification = createNotification(
        "Project removed",
        String.format("The project \"%s\" you were participating in has been deleted.",
            project.getProjectName()),
        Priority.MEDIUM,
        user);
    saveAndSendNotification(notification);
  }

  private Notification createNotification(String title, String content, Priority priority, User user) {
    Notification notification = new Notification();
    notification.setTitle(title);
    notification.setContent(content);
    notification.setPriority(priority);
    notification.setUser(user);
    return notification;
  }

  private void saveAndSendNotification(Notification notification) {
    notificationService.saveNotification(notification);
    sendNotificationUtil.sendNotificationToUser(notification.getUser().getEmail(), notification);
  }
}