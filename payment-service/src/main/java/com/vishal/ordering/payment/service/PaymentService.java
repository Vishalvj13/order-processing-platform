package com.vishal.ordering.payment.service;

import com.vishal.ordering.common.event.InventoryReservedEvent;
import com.vishal.ordering.common.event.PaymentCompletedEvent;
import com.vishal.ordering.common.event.PaymentFailedEvent;
import com.vishal.ordering.common.event.Topics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.payment.max-success-amount:1000.00}")
    private BigDecimal maxSuccessAmount;

    @Value("${app.payment.processing-delay-ms:500}")
    private long processingDelayMs;

    public PaymentService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void processPayment(InventoryReservedEvent event) {
        sleepQuietly();

        if (event.amount().compareTo(maxSuccessAmount) <= 0) {
            String transactionId = "TXN-" + UUID.randomUUID();
            log.info("Payment successful for order {}", event.orderId());
            kafkaTemplate.send(
                    Topics.PAYMENT_COMPLETED,
                    event.orderId(),
                    new PaymentCompletedEvent(event.orderId(), event.amount(), transactionId)
            );
            return;
        }

        log.info("Payment failed for order {}", event.orderId());
        kafkaTemplate.send(
                Topics.PAYMENT_FAILED,
                event.orderId(),
                new PaymentFailedEvent(
                        event.orderId(),
                        event.amount(),
                        "Payment declined because amount exceeded demo threshold of " + maxSuccessAmount
                )
        );
    }

    private void sleepQuietly() {
        try {
            Thread.sleep(processingDelayMs);
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
        }
    }
}
