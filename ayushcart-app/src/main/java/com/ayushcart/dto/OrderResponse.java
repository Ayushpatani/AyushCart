package com.ayushcart.dto;

import com.ayushcart.entity.Order;
import com.ayushcart.entity.OrderItem;
import com.ayushcart.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(
        Long id,
        OrderStatus status,
        BigDecimal totalAmount,
        List<Item> items,
        AddressDto shippingAddress,
        String customerName,
        String customerEmail,
        Instant createdAt,
        Instant updatedAt
) {
    public record Item(Long productId, String productName, BigDecimal unitPrice,
                       int quantity, BigDecimal lineTotal) {

        static Item from(OrderItem i) {
            return new Item(i.getProduct().getId(), i.getProductName(), i.getUnitPrice(),
                    i.getQuantity(), i.getLineTotal());
        }
    }

    public static OrderResponse from(Order o) {
        return new OrderResponse(o.getId(), o.getStatus(), o.getTotalAmount(),
                o.getItems().stream().map(Item::from).toList(),
                AddressDto.from(o.getShippingAddress()),
                o.getUser().getFullName(), o.getUser().getEmail(),
                o.getCreatedAt(), o.getUpdatedAt());
    }
}
