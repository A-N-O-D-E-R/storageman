package com.anode.storage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.anode.storage.entity.notification.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdAndRead(Long userId, Boolean read);
    List<Notification> findByUserId(Long userId);
}
