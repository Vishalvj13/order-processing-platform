package com.vishal.ordering.order.service;

import com.vishal.ordering.common.event.InventoryFailedEvent;
import com.vishal.ordering.common.event.InventoryReservedEvent;
import com.vishal.ordering.common.event.PaymentCompletedEvent;
import com.vishal.ordering.common.event.PaymentFailedEvent;
import com.vishal.ordering.common.event.Topics;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderSagaListener {

    private final OrderService orderService;

    public OrderSagaListener(OrderService orderService) {
        this.orderService = orderService;
    }

    @KafkaListener(topics = Topics.INVENTORY_RESERVED, groupId = "${spring.application.name}")
    public void onInventoryReserved(InventoryReservedEvent event) {
        orderService.handleInventoryReserved(event);
    }

    @KafkaListener(topics = Topics.INVENTORY_FAILED, groupId = "${spring.application.name}")
    public void onInventoryFailed(InventoryFailedEvent event) {
        orderService.handleInventoryFailed(event);
    }

    @KafkaListener(topics = Topics.PAYMENT_COMPLETED, groupId = "${spring.application.name}")
    public void onPaymentCompleted(PaymentCompletedEvent event) {
        orderService.handlePaymentCompleted(event);
    }

    @KafkaListener(topics = Topics.PAYMENT_FAILED, groupId = "${spring.application.name}")
    public void onPaymentFailed(PaymentFailedEvent event) {
        orderService.handlePaymentFailed(event);
    }
}
