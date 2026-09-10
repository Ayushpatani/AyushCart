package com.ayushcart.dto;

import com.ayushcart.entity.Cart;
import com.ayushcart.entity.CartItem;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(List<Item> items, int totalItems, BigDecimal totalAmount) {

    public record Item(Long productId, String name, String categoryName, String imageUrl, BigDecimal unitPrice,
                       int quantity, int stock, boolean active, BigDecimal lineTotal) {

        static Item from(CartItem i) {
            var p = i.getProduct();
            return new Item(p.getId(), p.getName(), p.getCategory().getName(), p.getImageUrl(), p.getPrice(),
                    i.getQuantity(), p.getStock(), p.isActive(), i.getLineTotal());
        }
    }

    public static CartResponse from(Cart cart) {
        List<Item> items = cart.getItems().stream().map(Item::from).toList();
        int totalItems = items.stream().mapToInt(Item::quantity).sum();
        return new CartResponse(items, totalItems, cart.getTotal());
    }
}
