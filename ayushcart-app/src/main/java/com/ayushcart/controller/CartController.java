package com.ayushcart.controller;

import com.ayushcart.dto.AddToCartRequest;
import com.ayushcart.dto.CartResponse;
import com.ayushcart.dto.UpdateCartItemRequest;
import com.ayushcart.service.CartService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public CartResponse get(Principal principal) {
        return cartService.getCart(principal.getName());
    }

    @PostMapping("/items")
    public CartResponse add(Principal principal, @Valid @RequestBody AddToCartRequest request) {
        return cartService.addItem(principal.getName(), request);
    }

    @PutMapping("/items/{productId}")
    public CartResponse update(Principal principal, @PathVariable Long productId,
                               @Valid @RequestBody UpdateCartItemRequest request) {
        return cartService.updateItem(principal.getName(), productId, request.quantity());
    }

    @DeleteMapping("/items/{productId}")
    public CartResponse remove(Principal principal, @PathVariable Long productId) {
        return cartService.removeItem(principal.getName(), productId);
    }

    @DeleteMapping
    public CartResponse clear(Principal principal) {
        return cartService.clear(principal.getName());
    }
}
