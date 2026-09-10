package com.ayushcart.repository;

import com.ayushcart.entity.Order;
import com.ayushcart.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findByUserEmail(String email, Pageable pageable);

    Optional<Order> findByIdAndUserEmail(Long id, String email);

    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    long countByStatus(OrderStatus status);

    /** JPQL works on entity/field names, not table/column names. Returns null when there are no orders. */
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status <> :excluded")
    BigDecimal sumTotalAmountExcludingStatus(@Param("excluded") OrderStatus excluded);
}
