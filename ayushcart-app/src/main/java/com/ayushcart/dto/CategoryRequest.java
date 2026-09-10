package com.ayushcart.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequest(
        @NotBlank(message = "Category name is required")
        @Size(max = 80, message = "Category name must be at most 80 characters")
        String name,

        @Size(max = 300, message = "Description must be at most 300 characters")
        String description
) {
}
