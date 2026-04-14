package com.vishal.ordering.notification.dto;

import java.time.OffsetDateTime;

public record NotificationResponse(
        Long id,
        String orderId,
        String customerEmail,
        String subject,
        String message,
        OffsetDateTime sentAt
) {
}
