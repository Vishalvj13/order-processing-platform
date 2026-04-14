package com.vishal.ordering.inventory.service;

import com.vishal.ordering.common.event.*;
import com.vishal.ordering.inventory.dto.InventoryResponse;
import com.vishal.ordering.inventory.entity.InventoryItemEntity;
import com.vishal.ordering.inventory.repository.InventoryRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public InventoryService(InventoryRepository inventoryRepository,
                            KafkaTemplate<String, Object> kafkaTemplate) {
        this.inventoryRepository = inventoryRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional(readOnly = true)
    public InventoryResponse getInventory(String productCode) {
        InventoryItemEntity item = inventoryRepository.findByProductCode(productCode)
                .orElseThrow(() -> new NoSuchElementException("Inventory item not found: " + productCode));

        return new InventoryResponse(
                item.getProductCode(),
                item.getAvailableQuantity(),
                item.getUpdatedAt()
        );
    }

    @Transactional
    public void reserveInventory(OrderCreatedEvent event) {
        InventoryItemEntity item = inventoryRepository.findForUpdateByProductCode(event.productCode())
                .orElse(null);

        if (item == null) {
            kafkaTemplate.send(
                    Topics.INVENTORY_FAILED,
                    event.orderId(),
                    new InventoryFailedEvent(
                            event.orderId(),
                            event.productCode(),
                            event.quantity(),
                            event.customerEmail(),
                            "Product does not exist"
                    )
            );
            return;
        }

        if (item.getAvailableQuantity() < event.quantity()) {
            kafkaTemplate.send(
                    Topics.INVENTORY_FAILED,
                    event.orderId(),
                    new InventoryFailedEvent(
                            event.orderId(),
                            event.productCode(),
                            event.quantity(),
                            event.customerEmail(),
                            "Insufficient stock"
                    )
            );
            return;
        }

        item.setAvailableQuantity(item.getAvailableQuantity() - event.quantity());
        inventoryRepository.save(item);

        kafkaTemplate.send(
                Topics.INVENTORY_RESERVED,
                event.orderId(),
                new InventoryReservedEvent(
                        event.orderId(),
                        event.productCode(),
                        event.quantity(),
                        event.amount(),
                        event.customerEmail()
                )
        );
    }

    @Transactional
    public void releaseInventory(ReleaseInventoryEvent event) {
        inventoryRepository.findForUpdateByProductCode(event.productCode())
                .ifPresent(item -> {
                    item.setAvailableQuantity(item.getAvailableQuantity() + event.quantity());
                    inventoryRepository.save(item);
                });
    }
}
