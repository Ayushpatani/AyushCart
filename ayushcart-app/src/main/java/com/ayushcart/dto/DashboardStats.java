package com.ayushcart.dto;

import java.math.BigDecimal;

public record DashboardStats(
        long activeProducts,
        long lowStockProducts,
        long customers,
        long totalOrders,
        long ordersAwaitingAction,
        BigDecimal revenue
) {
}
