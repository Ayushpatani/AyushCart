package com.ayushcart.service;

import com.ayushcart.dto.AddressDto;
import com.ayushcart.dto.OrderResponse;
import com.ayushcart.dto.PageResponse;
import com.ayushcart.entity.*;
import com.ayushcart.exception.BadRequestException;
import com.ayushcart.exception.ResourceNotFoundException;
import com.ayushcart.repository.CartRepository;
import com.ayushcart.repository.OrderRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;

    public OrderService(OrderRepository orderRepository, CartRepository cartRepository) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
    }

    /**
     * Checkout. Everything here runs in ONE transaction: if any product is out of
     * stock, nothing is saved — no half-created orders, no stock deducted.
     */
    @Transactional
    public OrderResponse placeOrder(String email, AddressDto address) {
        Cart cart = cartRepository.findByUserEmail(email)
                .filter(c -> !c.getItems().isEmpty())
                .orElseThrow(() -> new BadRequestException("Your cart is empty"));

        Order order = new Order(cart.getUser(), address.toEntity());
        for (CartItem item : cart.getItems()) {
            Product product = item.getProduct();
            if (!product.isActive()) {
                throw new BadRequestException(product.getName() + " is no longer available. Remove it from your cart.");
            }
            if (item.getQuantity() > product.getStock()) {
                throw new BadRequestException("Only " + product.getStock() + " of " + product.getName()
                        + " left in stock. Update your cart.");
            }
            product.decreaseStock(item.getQuantity());
            order.addItem(new OrderItem(product, item.getQuantity()));
        }

        Order saved = orderRepository.save(order);
        cart.clear();
        return OrderResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> myOrders(String email, Pageable pageable) {
        return PageResponse.from(orderRepository.findByUserEmail(email, pageable).map(OrderResponse::from));
    }

    @Transactional(readOnly = true)
    public OrderResponse myOrder(String email, Long orderId) {
        return OrderResponse.from(getOwnedOrder(email, orderId));
    }

    @Transactional
    public OrderResponse cancelMyOrder(String email, Long orderId) {
        Order order = getOwnedOrder(email, orderId);
        if (!order.getStatus().isCancellableByCustomer()) {
            throw new BadRequestException("Orders can only be cancelled before they are shipped");
        }
        cancel(order);
        return OrderResponse.from(order);
    }

    // ---------- admin ----------

    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> allOrders(OrderStatus status, Pageable pageable) {
        var page = (status == null) ? orderRepository.findAll(pageable) : orderRepository.findByStatus(status, pageable);
        return PageResponse.from(page.map(OrderResponse::from));
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrder(Long orderId) {
        return OrderResponse.from(getEntity(orderId));
    }

    @Transactional
    public OrderResponse updateStatus(Long orderId, OrderStatus newStatus) {
        Order order = getEntity(orderId);
        OrderStatus current = order.getStatus();
        if (!current.canMoveTo(newStatus)) {
            throw new BadRequestException("Cannot change an order from " + current + " to " + newStatus);
        }
        if (newStatus == OrderStatus.CANCELLED) {
            cancel(order);
        } else {
            order.setStatus(newStatus);
        }
        return OrderResponse.from(order);
    }

    /** Puts the items back into stock. */
    private void cancel(Order order) {
        order.getItems().forEach(i -> i.getProduct().increaseStock(i.getQuantity()));
        order.setStatus(OrderStatus.CANCELLED);
    }

    private Order getOwnedOrder(String email, Long orderId) {
        // Looking up by id AND owner means users can never read someone else's order
        return orderRepository.findByIdAndUserEmail(orderId, email)
                .orElseThrow(() -> new ResourceNotFoundException("Order " + orderId + " not found"));
    }

    private Order getEntity(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order " + orderId + " not found"));
    }
}
