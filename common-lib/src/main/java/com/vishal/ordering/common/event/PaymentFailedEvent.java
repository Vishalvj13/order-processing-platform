package com.vishal.ordering.common.event;

import java.math.BigDecimal;

public record PaymentFailedEvent(
        String orderId,
        BigDecimal amount,
        String reason
) {
}
