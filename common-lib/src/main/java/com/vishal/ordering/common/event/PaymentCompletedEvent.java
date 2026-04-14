package com.vishal.ordering.common.event;

import java.math.BigDecimal;

public record PaymentCompletedEvent(
        String orderId,
        BigDecimal amount,
        String transactionId
) {
}
