package com.vishal.ordering.notification.controller;

import com.vishal.ordering.notification.dto.NotificationResponse;
import com.vishal.ordering.notification.service.NotificationQueryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationQueryService notificationQueryService;

    public NotificationController(NotificationQueryService notificationQueryService) {
        this.notificationQueryService = notificationQueryService;
    }

    @GetMapping
    public List<NotificationResponse> findAll() {
        return notificationQueryService.findAll();
    }

    @GetMapping("/order/{orderId}")
    public List<NotificationResponse> findByOrderId(@PathVariable String orderId) {
        return notificationQueryService.findByOrderId(orderId);
    }
}
