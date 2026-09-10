package com.ayushcart.entity;

import java.util.EnumSet;
import java.util.Set;

/**
 * Lifecycle of an order. Each status knows which statuses it may move to next,
 * so invalid jumps (e.g. DELIVERED -> PLACED) are rejected in one place.
 */
public enum OrderStatus {
    PLACED,
    CONFIRMED,
    SHIPPED,
    DELIVERED,
    CANCELLED;

    public Set<OrderStatus> allowedNext() {
        return switch (this) {
            case PLACED -> EnumSet.of(CONFIRMED, CANCELLED);
            case CONFIRMED -> EnumSet.of(SHIPPED, CANCELLED);
            case SHIPPED -> EnumSet.of(DELIVERED);
            case DELIVERED, CANCELLED -> EnumSet.noneOf(OrderStatus.class);
        };
    }

    public boolean canMoveTo(OrderStatus next) {
        return allowedNext().contains(next);
    }

    /** A customer may cancel only before the order is shipped. */
    public boolean isCancellableByCustomer() {
        return this == PLACED || this == CONFIRMED;
    }
}
