package com.ayushcart.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderStatusTest {

    @Test
    void followsTheNormalDeliveryPath() {
        assertThat(OrderStatus.PLACED.canMoveTo(OrderStatus.CONFIRMED)).isTrue();
        assertThat(OrderStatus.CONFIRMED.canMoveTo(OrderStatus.SHIPPED)).isTrue();
        assertThat(OrderStatus.SHIPPED.canMoveTo(OrderStatus.DELIVERED)).isTrue();
    }

    @Test
    void rejectsSkippingOrGoingBackwards() {
        assertThat(OrderStatus.PLACED.canMoveTo(OrderStatus.DELIVERED)).isFalse();
        assertThat(OrderStatus.DELIVERED.canMoveTo(OrderStatus.PLACED)).isFalse();
        assertThat(OrderStatus.CANCELLED.allowedNext()).isEmpty();
    }

    @Test
    void customersCanOnlyCancelBeforeShipping() {
        assertThat(OrderStatus.PLACED.isCancellableByCustomer()).isTrue();
        assertThat(OrderStatus.CONFIRMED.isCancellableByCustomer()).isTrue();
        assertThat(OrderStatus.SHIPPED.isCancellableByCustomer()).isFalse();
    }
}
