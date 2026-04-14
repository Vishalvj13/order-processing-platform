package com.vishal.ordering.common.event;

public final class Topics {

    public static final String ORDER_CREATED = "order.created";
    public static final String INVENTORY_RESERVED = "inventory.reserved";
    public static final String INVENTORY_FAILED = "inventory.failed";
    public static final String PAYMENT_COMPLETED = "payment.completed";
    public static final String PAYMENT_FAILED = "payment.failed";
    public static final String RELEASE_INVENTORY = "inventory.release";
    public static final String ORDER_NOTIFICATION = "order.notification";

    private Topics() {
    }
}
