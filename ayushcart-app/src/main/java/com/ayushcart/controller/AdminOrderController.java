package com.ayushcart.controller;

import com.ayushcart.dto.DashboardStats;
import com.ayushcart.dto.OrderResponse;
import com.ayushcart.dto.PageResponse;
import com.ayushcart.dto.UpdateOrderStatusRequest;
import com.ayushcart.entity.OrderStatus;
import com.ayushcart.service.DashboardService;
import com.ayushcart.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminOrderController {

    private final OrderService orderService;
    private final DashboardService dashboardService;

    public AdminOrderController(OrderService orderService, DashboardService dashboardService) {
        this.orderService = orderService;
        this.dashboardService = dashboardService;
    }

    @GetMapping("/stats")
    public DashboardStats stats() {
        return dashboardService.stats();
    }

    /** Optional filter: /api/admin/orders?status=PLACED */
    @GetMapping("/orders")
    public PageResponse<OrderResponse> orders(
            @RequestParam(required = false) OrderStatus status,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return orderService.allOrders(status, pageable);
    }

    @GetMapping("/orders/{id}")
    public OrderResponse order(@PathVariable Long id) {
        return orderService.getOrder(id);
    }

    @PatchMapping("/orders/{id}/status")
    public OrderResponse updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateOrderStatusRequest request) {
        return orderService.updateStatus(id, request.status());
    }
}
