package com.ayushcart.service;

import com.ayushcart.dto.DashboardStats;
import com.ayushcart.entity.OrderStatus;
import com.ayushcart.entity.Role;
import com.ayushcart.repository.OrderRepository;
import com.ayushcart.repository.ProductRepository;
import com.ayushcart.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Objects;

@Service
public class DashboardService {

    static final int LOW_STOCK_THRESHOLD = 5;

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public DashboardService(ProductRepository productRepository, UserRepository userRepository,
                            OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional(readOnly = true)
    public DashboardStats stats() {
        return new DashboardStats(
                productRepository.countByActiveTrue(),
                productRepository.countByActiveTrueAndStockLessThanEqual(LOW_STOCK_THRESHOLD),
                userRepository.countByRole(Role.CUSTOMER),
                orderRepository.count(),
                orderRepository.countByStatus(OrderStatus.PLACED) + orderRepository.countByStatus(OrderStatus.CONFIRMED),
                Objects.requireNonNullElse(orderRepository.sumTotalAmountExcludingStatus(OrderStatus.CANCELLED),
                        BigDecimal.ZERO));
    }
}
