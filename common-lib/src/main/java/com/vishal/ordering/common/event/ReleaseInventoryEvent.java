package com.vishal.ordering.common.event;

public record ReleaseInventoryEvent(
        String orderId,
        String productCode,
        Integer quantity
) {
}
