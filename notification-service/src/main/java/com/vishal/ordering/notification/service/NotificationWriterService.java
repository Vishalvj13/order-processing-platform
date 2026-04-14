package com.vishal.ordering.notification.service;

import com.vishal.ordering.common.event.OrderNotificationEvent;
import com.vishal.ordering.notification.entity.NotificationLogEntity;
import com.vishal.ordering.notification.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationWriterService {

    private static final Logger log = LoggerFactory.getLogger(NotificationWriterService.class);

    private final NotificationRepository notificationRepository;

    public NotificationWriterService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public void storeNotification(OrderNotificationEvent event) {
        NotificationLogEntity logEntity = new NotificationLogEntity();
        logEntity.setOrderId(event.orderId());
        logEntity.setCustomerEmail(event.customerEmail());
        logEntity.setSubject(event.subject());
        logEntity.setMessage(event.message());

        notificationRepository.save(logEntity);
        log.info("Stored notification for order {}", event.orderId());
    }
}
