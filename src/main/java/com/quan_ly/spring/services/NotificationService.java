package com.quan_ly.spring.services;

import com.quan_ly.spring.models.Notification;
import com.quan_ly.spring.models.User;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface NotificationService {
    List<Notification> getAllNotifications();
    Optional<Notification> getNotificationById(Long id);
    Notification saveNotification(Notification notification);
    void deleteNotification(Long id);
    public List<Notification> getNotificationsByUser(User user);
    public Page<Notification> getNotificationsByUser(User user, int page, int size);

}
