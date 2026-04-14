package com.vishal.ordering.common.event;

public record OrderNotificationEvent(
        String orderId,
        String customerEmail,
        String subject,
        String message
) {
}
