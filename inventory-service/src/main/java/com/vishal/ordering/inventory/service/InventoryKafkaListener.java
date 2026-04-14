package com.vishal.ordering.inventory.service;

import com.vishal.ordering.common.event.OrderCreatedEvent;
import com.vishal.ordering.common.event.ReleaseInventoryEvent;
import com.vishal.ordering.common.event.Topics;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class InventoryKafkaListener {

    private final InventoryService inventoryService;

    public InventoryKafkaListener(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @KafkaListener(topics = Topics.ORDER_CREATED, groupId = "${spring.application.name}")
    public void onOrderCreated(OrderCreatedEvent event) {
        inventoryService.reserveInventory(event);
    }

    @KafkaListener(topics = Topics.RELEASE_INVENTORY, groupId = "${spring.application.name}")
    public void onReleaseInventory(ReleaseInventoryEvent event) {
        inventoryService.releaseInventory(event);
    }
}
