package com.vishal.ordering.notification.service;

import com.vishal.ordering.notification.dto.NotificationResponse;
import com.vishal.ordering.notification.entity.NotificationLogEntity;
import com.vishal.ordering.notification.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationQueryService {

    private final NotificationRepository notificationRepository;

    public NotificationQueryService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public List<NotificationResponse> findAll() {
        return notificationRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<NotificationResponse> findByOrderId(String orderId) {
        return notificationRepository.findByOrderIdOrderByIdAsc(orderId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private NotificationResponse toResponse(NotificationLogEntity entity) {
        return new NotificationResponse(
                entity.getId(),
                entity.getOrderId(),
                entity.getCustomerEmail(),
                entity.getSubject(),
                entity.getMessage(),
                entity.getSentAt()
        );
    }
}
