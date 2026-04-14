package com.vishal.ordering.notification.repository;

import com.vishal.ordering.notification.entity.NotificationLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<NotificationLogEntity, Long> {
    List<NotificationLogEntity> findByOrderIdOrderByIdAsc(String orderId);
}
