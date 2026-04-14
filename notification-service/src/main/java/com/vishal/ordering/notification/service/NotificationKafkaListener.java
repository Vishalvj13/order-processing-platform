package com.vishal.ordering.notification.service;

import com.vishal.ordering.common.event.OrderNotificationEvent;
import com.vishal.ordering.common.event.Topics;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationKafkaListener {

    private final NotificationWriterService notificationWriterService;

    public NotificationKafkaListener(NotificationWriterService notificationWriterService) {
        this.notificationWriterService = notificationWriterService;
    }

    @KafkaListener(topics = Topics.ORDER_NOTIFICATION, groupId = "${spring.application.name}")
    public void onOrderNotification(OrderNotificationEvent event) {
        notificationWriterService.storeNotification(event);
    }
}
