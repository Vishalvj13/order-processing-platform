package com.vishal.ordering.payment.service;

import com.vishal.ordering.common.event.InventoryReservedEvent;
import com.vishal.ordering.common.event.Topics;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentKafkaListener {

    private final PaymentService paymentService;

    public PaymentKafkaListener(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @KafkaListener(topics = Topics.INVENTORY_RESERVED, groupId = "${spring.application.name}")
    public void onInventoryReserved(InventoryReservedEvent event) {
        paymentService.processPayment(event);
    }
}
