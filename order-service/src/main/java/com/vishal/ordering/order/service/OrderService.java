package com.vishal.ordering.order.service;

import com.vishal.ordering.common.event.*;
import com.vishal.ordering.order.dto.CreateOrderRequest;
import com.vishal.ordering.order.dto.OrderResponse;
import com.vishal.ordering.order.entity.OrderEntity;
import com.vishal.ordering.order.entity.OrderStatus;
import com.vishal.ordering.order.repository.OrderRepository;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final CacheManager cacheManager;

    public OrderService(OrderRepository orderRepository,
                        KafkaTemplate<String, Object> kafkaTemplate,
                        CacheManager cacheManager) {
        this.orderRepository = orderRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.cacheManager = cacheManager;
    }

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        OrderEntity order = new OrderEntity();
        order.setId(UUID.randomUUID().toString());
        order.setProductCode(request.productCode());
        order.setQuantity(request.quantity());
        order.setAmount(request.amount());
        order.setCustomerEmail(request.customerEmail());
        order.setStatus(OrderStatus.CREATED);
        order.setLastEvent("ORDER_CREATED");
        orderRepository.save(order);

        kafkaTemplate.send(
                Topics.ORDER_CREATED,
                order.getId(),
                new OrderCreatedEvent(
                        order.getId(),
                        order.getProductCode(),
                        order.getQuantity(),
                        order.getAmount(),
                        order.getCustomerEmail()
                )
        );

        return toResponse(order);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "orders", key = "#orderId")
    public OrderResponse getOrder(String orderId) {
        OrderEntity order = findOrder(orderId);
        return toResponse(order);
    }

    @Transactional
    public void handleInventoryReserved(InventoryReservedEvent event) {
        OrderEntity order = findOrder(event.orderId());
        if (isFinalState(order)) {
            return;
        }

        order.setStatus(OrderStatus.INVENTORY_RESERVED);
        order.setLastEvent("INVENTORY_RESERVED");
        order.setFailureReason(null);
        orderRepository.save(order);
        evictOrderCache(order.getId());
    }

    @Transactional
    public void handleInventoryFailed(InventoryFailedEvent event) {
        OrderEntity order = findOrder(event.orderId());
        if (isFinalState(order)) {
            return;
        }

        order.setStatus(OrderStatus.FAILED);
        order.setLastEvent("INVENTORY_FAILED");
        order.setFailureReason(event.reason());
        orderRepository.save(order);
        evictOrderCache(order.getId());

        kafkaTemplate.send(
                Topics.ORDER_NOTIFICATION,
                order.getId(),
                new OrderNotificationEvent(
                        order.getId(),
                        order.getCustomerEmail(),
                        "Order failed",
                        "Order %s failed because inventory could not be reserved: %s".formatted(order.getId(), event.reason())
                )
        );
    }

    @Transactional
    public void handlePaymentCompleted(PaymentCompletedEvent event) {
        OrderEntity order = findOrder(event.orderId());
        if (isFinalState(order)) {
            return;
        }

        order.setStatus(OrderStatus.COMPLETED);
        order.setLastEvent("PAYMENT_COMPLETED");
        order.setFailureReason(null);
        orderRepository.save(order);
        evictOrderCache(order.getId());

        kafkaTemplate.send(
                Topics.ORDER_NOTIFICATION,
                order.getId(),
                new OrderNotificationEvent(
                        order.getId(),
                        order.getCustomerEmail(),
                        "Order completed",
                        "Order %s has been completed successfully. Transaction Id: %s".formatted(order.getId(), event.transactionId())
                )
        );
    }

    @Transactional
    public void handlePaymentFailed(PaymentFailedEvent event) {
        OrderEntity order = findOrder(event.orderId());
        if (isFinalState(order)) {
            return;
        }

        order.setStatus(OrderStatus.FAILED);
        order.setLastEvent("PAYMENT_FAILED");
        order.setFailureReason(event.reason());
        orderRepository.save(order);
        evictOrderCache(order.getId());

        kafkaTemplate.send(
                Topics.RELEASE_INVENTORY,
                order.getId(),
                new ReleaseInventoryEvent(order.getId(), order.getProductCode(), order.getQuantity())
        );

        kafkaTemplate.send(
                Topics.ORDER_NOTIFICATION,
                order.getId(),
                new OrderNotificationEvent(
                        order.getId(),
                        order.getCustomerEmail(),
                        "Order failed",
                        "Order %s failed during payment: %s".formatted(order.getId(), event.reason())
                )
        );
    }

    private boolean isFinalState(OrderEntity order) {
        return order.getStatus() == OrderStatus.COMPLETED || order.getStatus() == OrderStatus.FAILED;
    }

    private OrderEntity findOrder(String orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found: " + orderId));
    }

    private void evictOrderCache(String orderId) {
        if (cacheManager.getCache("orders") != null) {
            cacheManager.getCache("orders").evict(orderId);
        }
    }

    private OrderResponse toResponse(OrderEntity order) {
        return new OrderResponse(
                order.getId(),
                order.getProductCode(),
                order.getQuantity(),
                order.getAmount(),
                order.getCustomerEmail(),
                order.getStatus().name(),
                order.getFailureReason(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }
}
