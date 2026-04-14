package com.vishal.ordering.common.event;

import java.math.BigDecimal;

public record OrderCreatedEvent(
        String orderId,
        String productCode,
        Integer quantity,
        BigDecimal amount,
        String customerEmail
) {
}
