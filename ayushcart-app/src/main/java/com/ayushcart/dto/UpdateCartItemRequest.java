package com.ayushcart.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/** Quantity 0 removes the item. */
public record UpdateCartItemRequest(
        @NotNull(message = "Quantity is required")
        @Min(value = 0, message = "Quantity cannot be negative")
        @Max(value = 99, message = "Quantity must be at most 99")
        Integer quantity
) {
}
