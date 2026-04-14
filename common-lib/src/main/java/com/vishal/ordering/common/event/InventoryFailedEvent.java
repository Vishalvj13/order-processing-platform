package com.vishal.ordering.common.event;

public record InventoryFailedEvent(
        String orderId,
        String productCode,
        Integer quantity,
        String customerEmail,
        String reason
) {
}
