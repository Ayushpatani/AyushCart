package com.ayushcart.controller;

import com.ayushcart.dto.AddressDto;
import com.ayushcart.dto.OrderResponse;
import com.ayushcart.dto.PageResponse;
import com.ayushcart.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/** The logged-in customer's own orders. */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /** Checkout: turns the current cart into an order. Body = shipping address. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse place(Principal principal, @Valid @RequestBody AddressDto address) {
        return orderService.placeOrder(principal.getName(), address);
    }

    @GetMapping
    public PageResponse<OrderResponse> list(Principal principal,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return orderService.myOrders(principal.getName(), pageable);
    }

    @GetMapping("/{id}")
    public OrderResponse get(Principal principal, @PathVariable Long id) {
        return orderService.myOrder(principal.getName(), id);
    }

    @PostMapping("/{id}/cancel")
    public OrderResponse cancel(Principal principal, @PathVariable Long id) {
        return orderService.cancelMyOrder(principal.getName(), id);
    }
}
