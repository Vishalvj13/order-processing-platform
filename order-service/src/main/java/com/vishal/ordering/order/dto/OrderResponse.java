package com.vishal.ordering.order.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record OrderResponse(
        String orderId,
        String productCode,
        Integer quantity,
        BigDecimal amount,
        String customerEmail,
        String status,
        String failureReason,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
