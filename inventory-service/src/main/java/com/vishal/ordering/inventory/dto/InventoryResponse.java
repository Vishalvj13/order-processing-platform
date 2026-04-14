package com.vishal.ordering.inventory.dto;

import java.time.OffsetDateTime;

public record InventoryResponse(
        String productCode,
        Integer availableQuantity,
        OffsetDateTime updatedAt
) {
}
