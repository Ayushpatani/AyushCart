package com.ayushcart.service;

import com.ayushcart.dto.AddToCartRequest;
import com.ayushcart.dto.CartResponse;
import com.ayushcart.entity.Cart;
import com.ayushcart.entity.CartItem;
import com.ayushcart.entity.Product;
import com.ayushcart.exception.BadRequestException;
import com.ayushcart.exception.ResourceNotFoundException;
import com.ayushcart.repository.CartRepository;
import com.ayushcart.repository.ProductRepository;
import com.ayushcart.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {

    static final int MAX_QUANTITY_PER_ITEM = 99;

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository, UserRepository userRepository,
                       ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public CartResponse getCart(String email) {
        return CartResponse.from(getOrCreateCart(email));
    }

    @Transactional
    public CartResponse addItem(String email, AddToCartRequest request) {
        Product product = productRepository.findById(request.productId())
                .filter(Product::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("Product " + request.productId() + " not found"));
        Cart cart = getOrCreateCart(email);

        var existing = cart.findItem(product.getId());
        int newQuantity = existing.map(CartItem::getQuantity).orElse(0) + request.quantity();
        checkQuantity(product, newQuantity);

        if (existing.isPresent()) {
            existing.get().setQuantity(newQuantity);
        } else {
            cart.addItem(new CartItem(product, newQuantity));
        }
        return CartResponse.from(cartRepository.save(cart));
    }

    /** Sets the quantity of an item; 0 removes it. */
    @Transactional
    public CartResponse updateItem(String email, Long productId, int quantity) {
        Cart cart = getOrCreateCart(email);
        CartItem item = cart.findItem(productId)
                .orElseThrow(() -> new ResourceNotFoundException("That product is not in your cart"));
        if (quantity == 0) {
            cart.removeItem(item);
        } else {
            checkQuantity(item.getProduct(), quantity);
            item.setQuantity(quantity);
        }
        return CartResponse.from(cart);
    }

    @Transactional
    public CartResponse removeItem(String email, Long productId) {
        Cart cart = getOrCreateCart(email);
        cart.findItem(productId).ifPresent(cart::removeItem);
        return CartResponse.from(cart);
    }

    @Transactional
    public CartResponse clear(String email) {
        Cart cart = getOrCreateCart(email);
        cart.clear();
        return CartResponse.from(cart);
    }

    private void checkQuantity(Product product, int quantity) {
        if (quantity > MAX_QUANTITY_PER_ITEM) {
            throw new BadRequestException("You can buy at most " + MAX_QUANTITY_PER_ITEM + " of one product");
        }
        if (quantity > product.getStock()) {
            throw new BadRequestException(product.getStock() == 0
                    ? product.getName() + " is out of stock"
                    : "Only " + product.getStock() + " of " + product.getName() + " left in stock");
        }
    }

    private Cart getOrCreateCart(String email) {
        return cartRepository.findByUserEmail(email).orElseGet(() -> {
            var user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            return cartRepository.save(new Cart(user));
        });
    }
}
