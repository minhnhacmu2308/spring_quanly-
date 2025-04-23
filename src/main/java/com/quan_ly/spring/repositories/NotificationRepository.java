package com.quan_ly.spring.repositories;

import com.quan_ly.spring.models.Document;
import com.quan_ly.spring.models.Notification;
import com.quan_ly.spring.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

}
