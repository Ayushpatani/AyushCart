package com.ayushcart.service;

import com.ayushcart.dto.AddToCartRequest;
import com.ayushcart.entity.*;
import com.ayushcart.exception.BadRequestException;
import com.ayushcart.repository.CartRepository;
import com.ayushcart.repository.ProductRepository;
import com.ayushcart.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

/** Pure unit test: repositories are mocked, no database or Spring context. */
@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    private static final String EMAIL = "test@ayushcart.com";

    @Mock CartRepository cartRepository;
    @Mock UserRepository userRepository;
    @Mock ProductRepository productRepository;
    @InjectMocks CartService cartService;

    private Product product;
    private Cart cart;

    @BeforeEach
    void setUp() {
        product = new Product("Yoga Mat", "desc", new BigDecimal("699.00"), 5, null,
                new Category("Sports", null));
        ReflectionTestUtils.setField(product, "id", 1L);
        cart = new Cart(new User("Test", EMAIL, "hash", Role.CUSTOMER));

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        // lenient: not every test reaches the cart lookup (Mockito's strict mode would flag it as unused)
        lenient().when(cartRepository.findByUserEmail(EMAIL)).thenReturn(Optional.of(cart));
    }

    @Test
    void addingTheSameProductTwiceIncreasesQuantity() {
        when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> inv.getArgument(0));

        cartService.addItem(EMAIL, new AddToCartRequest(1L, 2));
        var response = cartService.addItem(EMAIL, new AddToCartRequest(1L, 1));

        assertThat(response.items()).hasSize(1);
        assertThat(response.totalItems()).isEqualTo(3);
        assertThat(response.totalAmount()).isEqualByComparingTo("2097.00");
    }

    @Test
    void cannotAddMoreThanAvailableStock() {
        assertThatThrownBy(() -> cartService.addItem(EMAIL, new AddToCartRequest(1L, 6)))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Only 5");
    }

    @Test
    void cannotAddInactiveProduct() {
        product.setActive(false);
        assertThatThrownBy(() -> cartService.addItem(EMAIL, new AddToCartRequest(1L, 1)))
                .hasMessageContaining("not found");
    }
}
